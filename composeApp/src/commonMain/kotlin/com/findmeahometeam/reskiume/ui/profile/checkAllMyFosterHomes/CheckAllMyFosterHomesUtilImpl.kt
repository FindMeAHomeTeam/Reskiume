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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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
                        fosterHome.id
                    ).firstOrNull()

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepo(
                            fosterHomeWithLocalImage,
                            myUid
                        )
                    } else {
                        modifyFosterHomeInLocalRepo(
                            fosterHomeWithLocalImage,
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
                        fosterHome.id
                    ).firstOrNull()

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepo(
                            fosterHome,
                            myUid
                        )
                    } else {
                        modifyFosterHomeInLocalRepo(
                            fosterHome,
                            myUid
                        )
                    }
                    fosterHome
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    private suspend fun insertFosterHomeInLocalRepo(
        fosterHome: FosterHome,
        myUid: String
    ) {
        insertFosterHomeInLocalRepository(fosterHome) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Foster home ${fosterHome.id} added to local database"
                )
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
            } else {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Error adding the foster home ${fosterHome.id} to local database"
                )
            }
        }

    }

    @OptIn(ExperimentalTime::class)
    private suspend fun modifyFosterHomeInLocalRepo(
        updatedFosterHome: FosterHome,
        myUid: String
    ) {
        val previousFosterHome = getFosterHomeFromLocalRepository(updatedFosterHome.id).first()!!

        modifyFosterHomeInLocalRepository(
            updatedFosterHome,
            previousFosterHome
        ) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Foster home ${updatedFosterHome.id} modified in local database"
                )
                modifyCacheInLocalRepository(
                    LocalCache(
                        cachedObjectId = updatedFosterHome.id,
                        savedBy = myUid,
                        section = Section.FOSTER_HOMES,
                        timestamp = Clock.System.now().epochSeconds
                    )
                ) { rowsUpdated ->

                    if (rowsUpdated > 0) {
                        log.d(
                            "CheckAllMyFosterHomesUtilImpl",
                            "${updatedFosterHome.id} updated in local cache in section ${Section.FOSTER_HOMES}"
                        )
                    } else {
                        log.e(
                            "CheckAllMyFosterHomesUtilImpl",
                            "Error updating ${updatedFosterHome.id} in local cache in section ${Section.FOSTER_HOMES}"
                        )
                    }
                }
            } else {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "Error modifying the foster home ${updatedFosterHome.id} in local database"
                )
            }
        }
    }

    override fun downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
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

                    modifyFosterHomeInLocalRepo(
                        fosterHomeWithLocalImage,
                        myUid
                    )
                    fosterHomeWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyFosterHomesUtilImpl",
                        "Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    modifyFosterHomeInLocalRepo(
                        fosterHome,
                        myUid
                    )
                    fosterHome
                }
            }
        }
}
