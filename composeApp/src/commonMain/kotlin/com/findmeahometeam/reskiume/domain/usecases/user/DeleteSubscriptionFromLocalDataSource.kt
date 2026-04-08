package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import kotlinx.coroutines.flow.first

class DeleteSubscriptionFromLocalDataSource(
    private val localUserRepository: LocalUserRepository,
    private val fCMSubscriberRepository: FCMSubscriberRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        allSubscriptionsToDelete: List<Subscription>,
        onDeleteSubscriptions: (isUpdated: Boolean) -> Unit
    ) {
        allSubscriptionsToDelete.forEach { subscription ->

            invoke(subscription, onDeleteSubscriptions)
        }
    }

    suspend operator fun invoke(
        subscriptionToDelete: Subscription,
        onDeleteSubscriptions: (isUpdated: Boolean) -> Unit
    ) {
        val isUnsubscribed =
            fCMSubscriberRepository.unsubscribeFromTopic(subscriptionToDelete.topic).first()

        if (isUnsubscribed) {

            localUserRepository.deleteSubscription(
                subscriptionToDelete.subscriptionId,
                onDeletedSubscription = { rowsDeleted ->
                    if (rowsDeleted > 0) {
                        log.d(
                            "DeleteSubscriptionFromLocalDataSource",
                            "DeleteSubscriptionFromLocalDataSource: deleted the subscription id ${subscriptionToDelete.subscriptionId} for the user ${subscriptionToDelete.uid} in the local data source"
                        )
                    } else {
                        log.e(
                            "DeleteSubscriptionFromLocalDataSource",
                            "DeleteSubscriptionFromLocalDataSource: failed to delete the subscription id ${subscriptionToDelete.subscriptionId} for the user ${subscriptionToDelete.uid} in the local data source"
                        )
                    }
                    onDeleteSubscriptions(rowsDeleted > 0)
                }
            )
        }
    }
}
