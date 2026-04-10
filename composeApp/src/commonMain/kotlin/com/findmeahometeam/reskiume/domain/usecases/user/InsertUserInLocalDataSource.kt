package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class InsertUserInLocalDataSource(
    private val authRepository: AuthRepository,
    private val manageImagePath: ManageImagePath,
    private val localUserRepository: LocalUserRepository,
    private val fCMSubscriberRepository: FCMSubscriberRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        user: User,
        onInsertUser: (isSuccess: Boolean) -> Unit
    ) {
        val myUid = getMyUid()
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(user.image)

        localUserRepository.insertUser(
            user.copy(
                savedBy = myUid,
                image = imageFileName
            ).toEntity(),
            onInsertUser = { rowId ->
                if (rowId > 0) {

                    if (user.uid != myUid || user.subscriptions.isEmpty()) {
                        onInsertUser(true)
                    } else {
                        insertAllSubscriptions(user) { isSuccess ->

                            onInsertUser(isSuccess)
                        }
                    }
                } else {
                    onInsertUser(false)
                }
            }
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun insertAllSubscriptions(
        user: User,
        onComplete: (isSuccess: Boolean) -> Unit
    ) {
        user.subscriptions.forEachIndexed { index, subscription ->

            val isSubscribed = fCMSubscriberRepository.subscribeToTopic(subscription.topic).first()
            if (isSubscribed) {

                localUserRepository.insertSubscription(
                    subscription.toEntity(),
                    onInsertSubscription = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertUserInLocalDataSource",
                                "insertAllSubscriptions: inserted the subscription id ${subscription.subscriptionId} for the user ${subscription.uid} in the local data source"
                            )
                            if (user.subscriptions.size == index + 1) {
                                onComplete(true)
                            }
                        } else {
                            log.e(
                                "InsertUserInLocalDataSource",
                                "insertAllSubscriptions: failed to insert the subscription id ${subscription.subscriptionId} for the user ${subscription.uid} in the local data source"
                            )
                            onComplete(false)
                        }
                    }
                )
            } else {
                onComplete(false)
            }
        }
    }
}
