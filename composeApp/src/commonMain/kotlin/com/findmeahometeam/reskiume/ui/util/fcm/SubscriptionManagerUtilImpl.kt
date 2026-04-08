package com.findmeahometeam.reskiume.ui.util.fcm

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteSubscriptionFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertSubscriptionInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.util.fcm.SubscribeToAllTopicsFromSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.util.fcm.UnsubscribeFromAllTopicsFromSubscriberRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SubscriptionManagerUtilImpl(
    private val subscribeToAllTopicsFromSubscriberRepository: SubscribeToAllTopicsFromSubscriberRepository,
    private val modifyUserInRemoteDataSource: ModifyUserInRemoteDataSource,
    private val insertSubscriptionInLocalDataSource: InsertSubscriptionInLocalDataSource,
    private val unsubscribeFromAllTopicsFromSubscriberRepository: UnsubscribeFromAllTopicsFromSubscriberRepository,
    private val deleteSubscriptionFromLocalDataSource: DeleteSubscriptionFromLocalDataSource,
    private val log: Log
) : SubscriptionManagerUtil {

    override suspend fun subscribeToAllTopicsAfterLogin(user: User) {

        val isSuccess = subscribeToAllTopicsFromSubscriberRepository(user.subscriptions).first()
        if (isSuccess) {
            log.d(
                "SubscriptionManagerUtil",
                "subscribeToAllTopicsAfterLogin: Successfully subscribed to the topics ${user.subscriptions.map { "${it.topic} " }} after the user ${user.uid} logs in"
            )
        } else {
            log.e(
                "SubscriptionManagerUtil",
                "subscribeToAllTopicsAfterLogin: Failed to subscribe to the topics ${user.subscriptions.map { "${it.topic} " }} after the user ${user.uid} logs in"
            )
        }
    }

    override suspend fun subscribeToTopic(
        user: User,
        topic: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        val subscription = createSubscription(user.uid, topic)
        val updatedUserWithSubscription =
            user.copy(subscriptions = user.subscriptions + subscription)

        modifyUserInRemoteDataSource(updatedUserWithSubscription) { result: DatabaseResult ->

            if (result is DatabaseResult.Success) {

                coroutineScope.launch {

                    insertSubscriptionInLocalDataSource(
                        subscription
                    ) { isSuccess: Boolean ->

                        if (isSuccess) {
                            log.d(
                                "SubscriptionManagerUtil",
                                "subscribeToTopic: Successfully subscribed to the topic $topic"
                            )
                        } else {
                            log.e(
                                "SubscriptionManagerUtil",
                                "subscribeToTopic: Failed to insert the subscription in the local data source for the user ${user.uid} after subscribing to the topic $topic and updating the user in the remote data source"
                            )
                        }
                        onComplete()
                    }
                }
            } else {
                log.e(
                    "SubscriptionManagerUtil",
                    "subscribeToTopic: Failed to update the user ${user.uid} with the new subscription in the remote data source after subscribing to the topic $topic"
                )
                onComplete()
            }
        }
    }

    override suspend fun unsubscribeFromAllTopicsAfterLogOut(user: User) {

        val isSuccess =
            unsubscribeFromAllTopicsFromSubscriberRepository(user.subscriptions).first()
        if (isSuccess) {
            log.d(
                "SubscriptionManagerUtil",
                "unsubscribeFromAllTopicsAfterLogOut: Successfully unsubscribed to the topics ${user.subscriptions.map { "${it.topic} " }} after the user ${user.uid} logs out"
            )
        } else {
            log.e(
                "SubscriptionManagerUtil",
                "unsubscribeFromAllTopicsAfterLogOut: Failed to unsubscribe to the topics ${user.subscriptions.map { "${it.topic} " }} after the user ${user.uid} logs out"
            )
        }
    }

    override suspend fun unsubscribeFromTopic(
        user: User,
        topicToUnsubscribe: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        val allRemainingSubscriptions =
            user.subscriptions.filter { it.topic != topicToUnsubscribe }
        val subscriptionToDelete =
            user.subscriptions.find { it.topic == topicToUnsubscribe }!!

        val updatedUserWithSubscriptions =
            user.copy(subscriptions = allRemainingSubscriptions)

        modifyUserInRemoteDataSource(updatedUserWithSubscriptions) { result: DatabaseResult ->

            if (result is DatabaseResult.Success) {

                coroutineScope.launch {
                    deleteSubscriptionFromLocalDataSource(
                        subscriptionToDelete
                    ) { isSuccess: Boolean ->

                        if (isSuccess) {
                            log.d(
                                "SubscriptionManagerUtil",
                                "unsubscribeFromTopic: Successfully unsubscribed to the topic ${subscriptionToDelete.topic}"
                            )
                        } else {
                            log.e(
                                "SubscriptionManagerUtil",
                                "unsubscribeFromTopic: Failed to delete the topic ${subscriptionToDelete.topic} in the local data source for the user ${user.uid} after unsubscribing to the topics and updating the user in the remote data source"
                            )
                        }
                        onComplete()
                    }
                }
            } else {
                log.e(
                    "SubscriptionManagerUtil",
                    "unsubscribeFromTopic: Failed to update the user ${user.uid} with the remaining topics ${allRemainingSubscriptions.map { it.topic }} in the remote data source after unsubscribing to the topic ${subscriptionToDelete.topic}"
                )
                onComplete()
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createSubscription(
        uid: String,
        topic: String
    ): Subscription =
        Subscription(
            subscriptionId = Clock.System.now().epochSeconds.toString() + uid,
            uid = uid,
            topic = topic
        )
}
