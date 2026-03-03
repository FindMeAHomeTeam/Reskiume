package com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class CheckFosterHomeUtilImpl(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getFosterHomeFromRemoteRepository: GetFosterHomeFromRemoteRepository,
    private val deleteFosterHomeUtil: DeleteFosterHomeUtil,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertFosterHomeInLocalRepository: InsertFosterHomeInLocalRepository,
    private val modifyFosterHomeInLocalRepository: ModifyFosterHomeInLocalRepository,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val log: Log
) : CheckFosterHomeUtil {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFosterHomeFlow(
        fosterHomeId: String,
        ownerId: String,
        coroutineScope: CoroutineScope
    ): Flow<FosterHome> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            val myUid = authUser?.uid ?: " "

            getDataByManagingObjectLocalCacheTimestamp(
                cachedObjectId = fosterHomeId,
                savedBy = myUid,
                section = Section.FOSTER_HOMES,
                onCompletionInsertCache = {
                    getFosterHomeFromRemoteRepository(
                        fosterHomeId
                    ).downloadImageAndInsertFosterHomeInLocalRepository(coroutineScope)
                        .mapNotNull {
                            if (it == null) {
                                deleteFosterHome(
                                    fosterHomeId,
                                    ownerId,
                                    myUid,
                                    coroutineScope
                                )
                            }
                            it
                        }
                },
                onCompletionUpdateCache = {
                    getFosterHomeFromRemoteRepository(
                        fosterHomeId
                    ).downloadImageAndModifyFosterHomeInLocalRepository(coroutineScope)
                        .mapNotNull {
                            if (it == null) {
                                deleteFosterHome(
                                    fosterHomeId,
                                    ownerId,
                                    myUid,
                                    coroutineScope
                                )
                            }
                            it
                        }
                },
                onVerifyCacheIsRecent = {
                    getFosterHomeFromLocalRepository(fosterHomeId).mapNotNull {
                        if (it == null) {
                            deleteFosterHomeCacheFromLocalDataSource(fosterHomeId)
                        }
                        it
                    }
                }
            )
        }

    private fun Flow<FosterHome?>.downloadImageAndInsertFosterHomeInLocalRepository(coroutineScope: CoroutineScope): Flow<FosterHome?> =
        this.map { fosterHome: FosterHome? ->

            when {
                fosterHome == null -> fosterHome

                fosterHome.imageUrl.isBlank() -> {
                    log.d(
                        "CheckFosterHomeUtilImpl",
                        "downloadImageAndInsertFosterHomeInLocalRepository: Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    insertFosterHomesInLocalRepository(fosterHome, coroutineScope)

                    fosterHome
                }

                else -> {
                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHome.ownerId,
                        extraId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    )
                    val fosterHomeWithLocalImage = fosterHome.copy(
                        imageUrl = localImagePath.ifBlank { fosterHome.imageUrl }
                    )

                    insertFosterHomesInLocalRepository(fosterHomeWithLocalImage, coroutineScope)
                    fosterHomeWithLocalImage
                }
            }
        }

    private fun deleteFosterHome(
        fosterHomeId: String,
        ownerId: String,
        myUid: String,
        coroutineScope: CoroutineScope
    ) {
        deleteFosterHomeUtil.deleteFosterHome(
            id = fosterHomeId,
            ownerId = ownerId,
            coroutineScope = coroutineScope,
            onlyDeleteOnLocal = myUid != ownerId,
            onError = {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHome: Failed to delete the foster home $fosterHomeId"
                )
            },
            onComplete = {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHome: Foster home $fosterHomeId deleted successfully"
                )
            }
        )
    }

    private suspend fun deleteFosterHomeCacheFromLocalDataSource(
        id: String
    ) {
        deleteCacheFromLocalRepository(id) { rowsDeleted: Int ->

            if (rowsDeleted > 0) {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHomeCacheFromLocalDataSource: Foster home $id deleted in the local cache in section ${Section.FOSTER_HOMES}"
                )
            } else {
                log.e(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHomeCacheFromLocalDataSource: Error deleting the Foster home $id in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }
    }

    private suspend fun insertFosterHomesInLocalRepository(
        fosterHome: FosterHome,
        coroutineScope: CoroutineScope
    ) {
        insertFosterHomeInLocalRepository(
            fosterHome,
            coroutineScope
        ) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "insertFosterHomesInLocalRepository: Foster home ${fosterHome.id} added to local database"
                )
            } else {
                log.e(
                    "CheckFosterHomeUtilImpl",
                    "insertFosterHomesInLocalRepository: Error adding the Foster home ${fosterHome.id} to local database"
                )
            }
        }
    }

    private fun Flow<FosterHome?>.downloadImageAndModifyFosterHomeInLocalRepository(coroutineScope: CoroutineScope): Flow<FosterHome?> =
        this.map { fosterHome: FosterHome? ->

            when {
                fosterHome == null -> { /* do nothing */
                    fosterHome
                }

                fosterHome.imageUrl.isBlank() -> {
                    log.d(
                        "CheckFosterHomeUtilImpl",
                        "downloadImageAndModifyFosterHomeInLocalRepository: Foster home ${fosterHome.id} has no avatar image to save locally."
                    )
                    modifyFosterHomesInLocalRepository(fosterHome, coroutineScope)

                    fosterHome
                }

                else -> {
                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = fosterHome.ownerId,
                        extraId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    )

                    val fosterHomeWithLocalImage = fosterHome.copy(
                        imageUrl = localImagePath.ifBlank { fosterHome.imageUrl }
                    )

                    modifyFosterHomesInLocalRepository(fosterHomeWithLocalImage, coroutineScope)
                    fosterHomeWithLocalImage
                }
            }
        }

    private suspend fun modifyFosterHomesInLocalRepository(
        updatedFosterHome: FosterHome,
        coroutineScope: CoroutineScope
    ) {
        val previousFosterHome =
            getFosterHomeFromLocalRepository(updatedFosterHome.id).first()!!

        modifyFosterHomeInLocalRepository(
            updatedFosterHome,
            previousFosterHome,
            coroutineScope
        ) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "modifyFosterHomesInLocalRepository: Foster home ${updatedFosterHome.id} modified in local database"
                )
            } else {
                log.e(
                    "CheckFosterHomeUtilImpl",
                    "modifyFosterHomesInLocalRepository: Error modifying the Foster home ${updatedFosterHome.id} in local database"
                )
            }
        }
    }
}
