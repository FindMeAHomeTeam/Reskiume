package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class ModifyUserInLocalDataSource(
    private val manageImagePath: ManageImagePath,
    private val fCMSubscriberRepository: FCMSubscriberRepository,
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(updatedUser: User): Flow<Boolean> = flow {

        val myUid = getMyUid()
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(updatedUser.image)

        var isSuccess = localUserRepository.upsertUser(
            updatedUser.copy(
                savedBy = myUid,
                image = imageFileName
            ).toEntity(),
            subscriptions = if (updatedUser.uid == myUid && updatedUser.subscriptions.isNotEmpty()) {
                updatedUser.subscriptions.map { it.toEntity() }
            } else {
                emptyList()
            }
        ).first()

        if (isSuccess) {

            if (updatedUser.uid == myUid) {
                isSuccess = manageAllSubscriptions(updatedUser)
            }
            emit(isSuccess)
        }
    }

    private suspend fun manageAllSubscriptions(updatedUser: User): Boolean {

        var isSuccess = true

        val previousUser = localUserRepository.getUser(updatedUser.uid).first()!!

        val previousSubscriptions =
            previousUser.allSubscriptions.map { it.toDomain() }.toSet()

        val updatedSubscriptions =
            updatedUser.subscriptions.toSet()

        val allSubscriptionsToManage: Set<Subscription> =
            (previousSubscriptions - updatedSubscriptions) +
                    (updatedSubscriptions - previousSubscriptions)

        allSubscriptionsToManage.forEach { subscriptionToManage ->
            if (isSuccess) {
                if (updatedSubscriptions.contains(subscriptionToManage)) {

                    val subscriptionEntity =
                        updatedUser.subscriptions.first {
                            it.subscriptionId == subscriptionToManage.subscriptionId
                        }.toEntity()

                    isSuccess =
                        fCMSubscriberRepository.subscribeToTopic(subscriptionEntity.topic).first()
                } else {

                    val subscriptionEntity =
                        previousUser.allSubscriptions.first {
                            it.subscriptionId == subscriptionToManage.subscriptionId
                        }

                    isSuccess =
                        fCMSubscriberRepository.unsubscribeFromTopic(subscriptionEntity.topic)
                            .first()
                }
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
