package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFCMSubscriberRepository(
    private val allUserTopics: MutableList<Subscription> = mutableListOf()
) : FCMSubscriberRepository {

    override suspend fun subscribeToTopic(topic: String): Flow<Boolean> {

        return if (allUserTopics.any { it.topic == topic }) {
            flowOf(false)
        } else {
            allUserTopics.add(
                user.subscriptions[0].copy(
                    subscriptionId = topic + user.uid,
                    topic = topic
                )
            )
            flowOf(true)
        }
    }

    override suspend fun subscribeToAllTopics(allTopics: List<Subscription>): Flow<Boolean> {

        return if (allUserTopics.any { userTopic -> allTopics.any { userTopic.topic == it.topic } }) {
            flowOf(false)
        } else {
            allUserTopics.addAll(
                allTopics.map {
                    user.subscriptions[0].copy(
                        subscriptionId = it.subscriptionId,
                        topic = it.topic
                    )
                }
            )
            flowOf(true)
        }
    }

    override suspend fun unsubscribeFromTopic(topic: String): Flow<Boolean> {

        val topicToDelete = allUserTopics.find { it.topic == topic }

        return if (topicToDelete == null) {
            flowOf(false)
        } else {
            allUserTopics.remove(topicToDelete)
            flowOf(true)
        }
    }

    override suspend fun unsubscribeFromAllTopics(allTopics: List<Subscription>): Flow<Boolean> {

        val topicsToDelete =
            allUserTopics.filter { userTopic -> allTopics.any { userTopic.topic == it.topic } }

        return if (topicsToDelete.isEmpty()) {
            flowOf(false)
        } else {
            allUserTopics.removeAll(topicsToDelete)
            flowOf(true)
        }
    }
}
