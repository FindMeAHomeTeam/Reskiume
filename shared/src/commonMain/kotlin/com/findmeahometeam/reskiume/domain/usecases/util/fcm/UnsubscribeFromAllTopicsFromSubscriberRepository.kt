package com.findmeahometeam.reskiume.domain.usecases.util.fcm

import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import kotlinx.coroutines.flow.Flow

class UnsubscribeFromAllTopicsFromSubscriberRepository(private val repository: FCMSubscriberRepository) {

    suspend operator fun invoke(
        allSubscriptions: List<Subscription>
    ): Flow<Boolean> = repository.unsubscribeFromAllTopics(allSubscriptions)
}
