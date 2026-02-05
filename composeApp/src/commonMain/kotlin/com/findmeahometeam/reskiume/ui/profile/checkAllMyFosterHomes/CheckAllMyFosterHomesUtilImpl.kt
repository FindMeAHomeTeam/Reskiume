package com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CheckAllMyFosterHomesUtilImpl(
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val insertFosterHomeInLocalRepository: InsertFosterHomeInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyFosterHomeInLocalRepository: ModifyFosterHomeInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val log: Log
) : CheckAllMyFosterHomesUtil {

    override fun downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
        coroutineScope: CoroutineScope,
        myUid: String
    ): Flow<List<FosterHome>> =
        allFosterHomesFlow.map { fosterHomeList ->
            fosterHomeList.map { fosterHome ->

                if (fosterHome.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHome.ownerId,
                        extraId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    )
                    val fosterHomeWithLocalImage =
                        fosterHome.copy(imageUrl = localImagePath.ifBlank { fosterHome.imageUrl })

                    val localFosterHome: FosterHome? = getFosterHomeFromLocalRepository(
                        fosterHome.id,
                        coroutineScope
                    ).firstOrNull()

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepository(
                            fosterHomeWithLocalImage,
                            coroutineScope,
                            myUid
                        )
                    } else {
                        modifyFosterHomeInLocalRepository(
                            fosterHomeWithLocalImage,
                            coroutineScope,
                            myUid
                        )
                    }
                    fosterHomeWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    val localFosterHome: FosterHome? = getFosterHomeFromLocalRepository(
                        fosterHome.id,
                        coroutineScope
                    ).firstOrNull()

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepository(
                            fosterHome,
                            coroutineScope,
                            myUid
                        )
                    } else {
                        modifyFosterHomeInLocalRepository(
                            fosterHome,
                            coroutineScope,
                            myUid
                        )
                    }
                    fosterHome
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    private fun insertFosterHomeInLocalRepository(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        coroutineScope.launch {

            insertFosterHomeInLocalRepository(fosterHome) { isSuccess ->
                if (isSuccess) {
                    log.d(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Foster home ${fosterHome.id} added to local database"
                    )
                    coroutineScope.launch {

                        insertCacheInLocalRepository(
                            LocalCache(
                                cachedObjectId = fosterHome.id,
                                savedBy = myUid,
                                section = Section.FOSTER_HOMES,
                                timestamp = Clock.System.now().epochSeconds
                            )
                        ) { rowId ->

                            if (rowId > 0) {
                                log.d(
                                    "CheckAllMyFosterHomesUtilImpl",
                                    "${fosterHome.id} added to local cache in section ${Section.FOSTER_HOMES}"
                                )
                            } else {
                                log.e(
                                    "CheckAllMyFosterHomesUtilImpl",
                                    "Error adding ${fosterHome.id} to local cache in section ${Section.FOSTER_HOMES}"
                                )
                            }
                        }
                    }
                } else {
                    log.e(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Error adding the foster home ${fosterHome.id} to local database"
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun modifyFosterHomeInLocalRepository(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        coroutineScope.launch {

            modifyFosterHomeInLocalRepository(fosterHome) { isSuccess ->
                if (isSuccess) {
                    log.d(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Foster home ${fosterHome.id} modified in local database"
                    )
                    coroutineScope.launch {

                        modifyCacheInLocalRepository(
                            LocalCache(
                                cachedObjectId = fosterHome.id,
                                savedBy = myUid,
                                section = Section.FOSTER_HOMES,
                                timestamp = Clock.System.now().epochSeconds
                            )
                        ) { rowsUpdated ->

                            if (rowsUpdated > 0) {
                                log.d(
                                    "CheckAllMyFosterHomesUtilImpl",
                                    "${fosterHome.id} updated in local cache in section ${Section.FOSTER_HOMES}"
                                )
                            } else {
                                log.e(
                                    "CheckAllMyFosterHomesUtilImpl",
                                    "Error updating ${fosterHome.id} in local cache in section ${Section.FOSTER_HOMES}"
                                )
                            }
                        }
                    }
                } else {
                    log.e(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Error modifying the foster home ${fosterHome.id} in local database"
                    )
                }
            }
        }
    }

    override fun downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
        coroutineScope: CoroutineScope,
        myUid: String
    ): Flow<List<FosterHome>> =
        allFosterHomesFlow.map { fosterHomeList ->
            fosterHomeList.map { fosterHome ->

                if (fosterHome.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHome.ownerId,
                        extraId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    )
                    val fosterHomeWithLocalImage =
                        fosterHome.copy(imageUrl = localImagePath.ifBlank { fosterHome.imageUrl })

                    modifyFosterHomeInLocalRepository(
                        fosterHomeWithLocalImage,
                        coroutineScope,
                        myUid
                    )
                    fosterHomeWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    modifyFosterHomeInLocalRepository(
                        fosterHome,
                        coroutineScope,
                        myUid
                    )
                    fosterHome
                }
            }
        }
}
