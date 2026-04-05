package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.firstOrNull

class InsertUserInLocalDataSource(
    private val manageImagePath: ManageImagePath,
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        user: User,
        onInsertUser: (isSuccess: Boolean) -> Unit
    ) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(user.image)

        localUserRepository.insertUser(
            user.copy(
                savedBy = getMyUid(),
                image = imageFileName
            ).toEntity(),
            onInsertUser = { rowId ->
                if (rowId > 0) {

                    val isSuccess = insertAllSubscriptions(user)
                    onInsertUser(isSuccess)
                } else {
                    onInsertUser(false)
                }
            }
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun insertAllSubscriptions(user: User): Boolean {

        var isSuccess = true

        user.subscriptions.forEach { subscription ->
            if (isSuccess) {

                localUserRepository.insertSubscription(
                    subscription.toEntity(),
                    onInsertSubscription = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertUserInLocalDataSource",
                                "insertAllSubscriptions: inserted the subscription id ${subscription.subscriptionId} for the user ${subscription.uid} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertUserInLocalDataSource",
                                "insertAllSubscriptions: failed to insert the subscription id ${subscription.subscriptionId} for the user ${subscription.uid} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }
}
