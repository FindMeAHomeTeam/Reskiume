package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import kotlinx.coroutines.flow.first

class InsertSubscriptionInLocalDataSource(
    private val localUserRepository: LocalUserRepository,
    private val fCMSubscriberRepository: FCMSubscriberRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        allSubscriptionsToSubscribe: List<Subscription>,
        onInsertSubscription: (isSuccess: Boolean) -> Unit
    ) {
        allSubscriptionsToSubscribe.forEach { subscription ->

            invoke(subscription, onInsertSubscription)
        }
    }

    suspend operator fun invoke(
        subscription: Subscription,
        onInsertSubscription: suspend (isSuccess: Boolean) -> Unit
    ) {
        val isSubscribed = fCMSubscriberRepository.subscribeToTopic(subscription.topic).first()
        if (isSubscribed) {

            localUserRepository.insertSubscription(
                subscription.toEntity(),
                onInsertSubscription = { rowId ->
                    if (rowId > 0) {
                        log.d(
                            "InsertSubscriptionInLocalDataSource",
                            "InsertSubscriptionInLocalDataSource: inserted the subscription id ${subscription.subscriptionId} for the user ${subscription.uid} in the local data source"
                        )
                    } else {
                        log.e(
                            "InsertSubscriptionInLocalDataSource",
                            "InsertSubscriptionInLocalDataSource: failed to insert the subscription id ${subscription.subscriptionId} for the user ${subscription.uid} in the local data source"
                        )
                    }
                    onInsertSubscription(rowId > 0)
                }
            )
        } else {
            onInsertSubscription(false)
        }
    }
}
