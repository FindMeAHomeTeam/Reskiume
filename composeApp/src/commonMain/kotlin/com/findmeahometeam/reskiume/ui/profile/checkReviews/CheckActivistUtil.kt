package com.findmeahometeam.reskiume.ui.profile.checkReviews

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CheckActivistUtil(
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertUserInLocalDataSource: InsertUserInLocalDataSource,
    private val modifyUserInLocalDataSource: ModifyUserInLocalDataSource,
    private val getCompleteImagePathFromLocalDataSource: GetCompleteImagePathFromLocalDataSource,
    private val log: Log
) {
    suspend fun getUser(
        activistUid: String,
        myUserUid: String,
        coroutineScope: CoroutineScope
    ): User? {

        val user: User? = getDataByManagingObjectLocalCacheTimestamp(
            cachedObjectId = activistUid,
            savedBy = myUserUid,
            section = Section.USERS,
            onCompletionInsertCache = {
                getUserFromRemoteDataSource(activistUid)
                    .saveImageAndInsertUserInLocalRepository(coroutineScope)
                    .firstOrNull()
            },
            onCompletionUpdateCache = {
                getUserFromRemoteDataSource(activistUid)
                    .saveImageAndModifyUserInLocalRepository(coroutineScope)
                    .firstOrNull()
            },
            onVerifyCacheIsRecent = {
                getUserFromLocalDataSource(activistUid)
            }
        )
        return user?.copy(
            image = if (user.image.isBlank()) {
                user.image
            } else {
                getCompleteImagePathFromLocalDataSource(user.image)
            }
        )
    }

    private fun Flow<User?>.saveImageAndInsertUserInLocalRepository(coroutineScope: CoroutineScope): Flow<User?> =
        this.map { user ->

            user?.let { activist ->
                if (activist.image.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = activist.uid,
                        extraId = "",
                        section = Section.USERS
                    )
                    val activistWithLocalImage =
                        activist.copy(image = localImagePath.ifBlank { activist.image })

                    insertUserInLocalRepository(activistWithLocalImage, coroutineScope)

                    activistWithLocalImage
                } else {
                    log.d(
                        "CheckUserUtil",
                        "User ${activist.uid} has no avatar image to save locally."
                    )
                    insertUserInLocalRepository(activist, coroutineScope)

                    activist
                }
            }
        }

    private fun insertUserInLocalRepository(user: User, coroutineScope: CoroutineScope) {

        coroutineScope.launch {

            insertUserInLocalDataSource(user) { rowId ->

                if (rowId > 0) {
                    log.d(
                        "CheckUserUtil",
                        "User ${user.uid} added to local database"
                    )
                } else {
                    log.e(
                        "CheckUserUtil",
                        "Error adding user ${user.uid} to local database"
                    )
                }
            }
        }
    }

    private fun Flow<User?>.saveImageAndModifyUserInLocalRepository(coroutineScope: CoroutineScope): Flow<User?> =
        this.map { user ->

            user?.let { activist ->
                if (activist.image.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = activist.uid,
                        extraId = "",
                        section = Section.USERS
                    )
                    val activistWithLocalImage =
                        activist.copy(image = localImagePath.ifBlank { activist.image })

                    modifyUserInLocalRepository(activistWithLocalImage, coroutineScope)

                    activistWithLocalImage
                } else {
                    log.d(
                        "CheckUserUtil",
                        "User ${activist.uid} has no avatar image to save locally."
                    )
                    modifyUserInLocalRepository(activist, coroutineScope)
                    activist
                }
            }
        }

    private fun modifyUserInLocalRepository(user: User, coroutineScope: CoroutineScope) {

        coroutineScope.launch {

            modifyUserInLocalDataSource(user) { rowsUpdated: Int ->

                if (rowsUpdated > 0) {
                    log.d(
                        "CheckUserUtil",
                        "Modified user with uid ${user.uid} into local data source."
                    )
                } else {
                    log.e(
                        "CheckUserUtil",
                        "Failed to modify user with uid ${user.uid} in local data source."
                    )
                }
            }
        }
    }
}
