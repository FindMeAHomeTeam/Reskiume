package com.findmeahometeam.reskiume.domain.repository.util.fcm

import com.findmeahometeam.reskiume.domain.model.user.Subscription

interface FCMSubscriberRepositoryForIosDelegate {

    suspend fun subscribeToTopic(topic: String)

    suspend fun subscribeToAllTopics(allTopics: List<Subscription>)

    suspend fun unsubscribeFromTopic(topic: String)

    suspend fun unsubscribeFromAllTopics(allTopics: List<Subscription>)
}
