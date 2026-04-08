package com.findmeahometeam.reskiume.data.util.fcm

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FCMSubscriberRepositoryAndroidImpl(
    private val log: Log
) : FCMSubscriberRepository {

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                log.d(
                    "FCMSubscriberRepositoryAndroidImpl",
                    "FCM registration successful with token: ${task.result}"
                )
            } else {
                log.e(
                    "FCMSubscriberRepositoryAndroidImpl",
                    "Fetching FCM registration token failed: ${task.exception}"
                )
            }
        }
    }

    override suspend fun subscribeToTopic(topic: String): Flow<Boolean> =
        flow {
            Firebase.messaging.subscribeToTopic(topic).addOnCompleteListener {
                log.d(
                    "FCMSubscriberRepositoryAndroidImpl",
                    "subscribeToTopic: Subscribed to $topic topic"
                )
            }
            emit(true)
        }.catch { e ->
            log.e(
                "FCMSubscriberRepositoryAndroidImpl",
                "subscribeToTopic: Failed to subscribe to $topic topic: $e"
            )
            emit(false)
        }

    override suspend fun subscribeToAllTopics(allTopics: List<Subscription>): Flow<Boolean> =
        flow {
            allTopics.forEach { subscription ->
                Firebase.messaging.subscribeToTopic(subscription.topic).addOnCompleteListener {
                    log.d(
                        "FCMSubscriberRepositoryAndroidImpl",
                        "subscribeToTopic: Subscribed to ${subscription.topic} topic"
                    )
                }
            }
            emit(true)
        }.catch { e ->
            log.e(
                "FCMSubscriberRepositoryAndroidImpl",
                "subscribeToTopic: Failed to subscribe to any of these topics: ${allTopics.map { "${it.topic} " }} because $e"
            )
            emit(false)
        }

    override suspend fun unsubscribeFromTopic(
        topic: String
    ): Flow<Boolean> =
        flow {
            Firebase.messaging.unsubscribeFromTopic(topic).addOnCompleteListener {
                log.d(
                    "FCMSubscriberRepositoryAndroidImpl",
                    "unsubscribeFromTopic: Unsubscribed to $topic topic"
                )
            }
            emit(true)
        }.catch { e ->
            log.e(
                "FCMSubscriberRepositoryAndroidImpl",
                "unsubscribeFromTopic: Failed to unsubscribe to $topic topic: $e"
            )
            emit(false)
        }

    override suspend fun unsubscribeFromAllTopics(allTopics: List<Subscription>): Flow<Boolean> =
        flow {
            allTopics.forEach { subscription ->
                Firebase.messaging.unsubscribeFromTopic(subscription.topic).addOnCompleteListener {
                    log.d(
                        "FCMSubscriberRepositoryAndroidImpl",
                        "unsubscribeFromAllTopics: Unsubscribed to ${subscription.topic} topic"
                    )
                }
            }
            emit(true)
        }.catch { e ->
            log.e(
                "FCMSubscriberRepositoryAndroidImpl",
                "unsubscribeFromAllTopics: Failed to unsubscribe to any of these topics: ${allTopics.map { "${it.topic} " }} because $e"
            )
            emit(false)
        }
}
