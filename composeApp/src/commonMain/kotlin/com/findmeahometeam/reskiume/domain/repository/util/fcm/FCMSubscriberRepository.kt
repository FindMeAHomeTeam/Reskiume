package com.findmeahometeam.reskiume.domain.repository.util.fcm

import com.findmeahometeam.reskiume.domain.model.user.Subscription
import kotlinx.coroutines.flow.Flow

interface FCMSubscriberRepository {

    suspend fun subscribeToTopic(
        topic: String
    ): Flow<Boolean>

    suspend fun subscribeToAllTopics(
        allTopics: List<Subscription>
    ): Flow<Boolean>

    suspend fun unsubscribeFromTopic(
        topic: String
    ): Flow<Boolean>

    suspend fun unsubscribeFromAllTopics(
        allTopics: List<Subscription>
    ): Flow<Boolean>
}