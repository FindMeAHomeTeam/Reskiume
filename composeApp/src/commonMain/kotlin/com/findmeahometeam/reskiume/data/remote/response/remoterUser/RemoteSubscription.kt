package com.findmeahometeam.reskiume.data.remote.response.remoterUser

import com.findmeahometeam.reskiume.domain.model.user.Subscription

data class RemoteSubscription(
    val subscriptionId: String? = "",
    val uid: String? = "",
    val topic: String? = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "subscriptionId" to subscriptionId,
            "uid" to uid,
            "topic" to topic
        )
    }

    fun toDomain(): Subscription {
        return Subscription(
            subscriptionId = subscriptionId ?: "",
            uid = uid ?: "",
            topic = topic ?: ""
        )
    }
}
