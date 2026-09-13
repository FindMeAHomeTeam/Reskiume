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
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlin.collections.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CheckAllFosterHomesUtilImpl(
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val insertFosterHomeInLocalRepository: InsertFosterHomeInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyFosterHomeInLocalRepository: ModifyFosterHomeInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val deleteFosterHomeUtil: DeleteFosterHomeUtil,
    private val log: Log
) : CheckAllFosterHomesUtil {

    override suspend fun updateLocalRepositoryWithRemoteFosterHomes(
        allRemoteFosterHomes: Set<FosterHome>,
        allLocalFosterHomes: Set<FosterHome>,
        myUid: String,
        coroutineScope: CoroutineScope
    ) {
        val allFosterHomesIdsToManage: Set<String> =
            allRemoteFosterHomes.map { it.id } union allLocalFosterHomes.map { it.id }

        allFosterHomesIdsToManage.forEach { fosterHomeIdToManage ->

            val fosterHomeToManage = allRemoteFosterHomes.find { it.id == fosterHomeIdToManage }
                ?: allLocalFosterHomes.find { it.id == fosterHomeIdToManage }!!

            if (allRemoteFosterHomes.any { it.id == fosterHomeToManage.id }) {

                val localFosterHome: FosterHome? =
                    allLocalFosterHomes.find { it.id == fosterHomeToManage.id }

                if (fosterHomeToManage.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHomeToManage.ownerId,
                        extraId = fosterHomeToManage.id,
                        section = Section.FOSTER_HOMES
                    )
                    val fosterHomeWithLocalImage =
                        fosterHomeToManage.copy(imageUrl = localImagePath.ifBlank { fosterHomeToManage.imageUrl })

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepo(
                            fosterHomeWithLocalImage,
                            coroutineScope,
                            myUid
                        )
                    } else {
                        modifyFosterHomeInLocalRepo(
                            fosterHomeWithLocalImage,
                            coroutineScope,
                            myUid
                        )
                    }
                } else {
                    log.d(
                        "CheckAllMyFosterHomesUtilImpl",
                        "updateLocalRepositoryWithRemoteFosterHomes: Foster home ${fosterHomeToManage.id} has no avatar image to save locally."
                    )

                    if (localFosterHome == null) {
                        insertFosterHomeInLocalRepo(
                            fosterHomeToManage,
                            coroutineScope,
                            myUid
                        )
                    } else {
                        modifyFosterHomeInLocalRepo(
                            fosterHomeToManage,
                            coroutineScope,
                            myUid
                        )
                    }
                }
            } else {
                deleteLocalFosterHome(
                    id = fosterHomeToManage.id,
                    ownerId = fosterHomeToManage.ownerId,
                    coroutineScope = coroutineScope
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun insertFosterHomeInLocalRepo(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        val isSuccess = insertFosterHomeInLocalRepository(
            fosterHome,
            coroutineScope
        ).first()

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

    @OptIn(ExperimentalTime::class)
    private suspend fun modifyFosterHomeInLocalRepo(
        updatedFosterHome: FosterHome,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        val previousFosterHome = getFosterHomeFromLocalRepository(updatedFosterHome.id).first()!!

        val isSuccess = modifyFosterHomeInLocalRepository(
            updatedFosterHome = updatedFosterHome,
            previousFosterHome = previousFosterHome,
            coroutineScope = coroutineScope
        ).first()

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

    private fun deleteLocalFosterHome(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope
    ) {
        deleteFosterHomeUtil.deleteFosterHome(
            id = id,
            ownerId = ownerId,
            coroutineScope = coroutineScope,
            deleteOnLocal = true,
            deleteOnRemote = false,
            onError = {
                log.e(
                    "CheckAllMyFosterHomesUtilImpl",
                    "deleteLocalFosterHome: Error deleting the local foster home $id with the owner id $ownerId"
                )
            },
            onComplete = {
                log.d(
                    "CheckAllMyFosterHomesUtilImpl",
                    "deleteLocalFosterHome: Deleted local foster home $id with the owner id $ownerId"
                )
            }
        )
    }
}
