package com.findmeahometeam.reskiume.domain.model.user

import com.findmeahometeam.reskiume.data.database.entity.user.SubscriptionEntityForUser
import com.findmeahometeam.reskiume.data.remote.response.remoterUser.RemoteSubscription

data class Subscription(
    val subscriptionId: String,
    val uid: String,
    val topic: String
) {
    fun toEntity(): SubscriptionEntityForUser {
        return SubscriptionEntityForUser(
            subscriptionId = subscriptionId,
            uid = uid,
            topic = topic
        )
    }

    fun toData(): RemoteSubscription {
        return RemoteSubscription(
            subscriptionId = subscriptionId,
            uid = uid,
            topic = topic
        )
    }
}
