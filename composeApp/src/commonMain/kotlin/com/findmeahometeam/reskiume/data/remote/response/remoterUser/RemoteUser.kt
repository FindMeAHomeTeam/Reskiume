package com.findmeahometeam.reskiume.data.remote.response.remoterUser

import com.findmeahometeam.reskiume.domain.model.user.User

data class RemoteUser(
    val uid: String? = "",
    val username: String? = "",
    val description: String? = "",
    val image: String? = "",
    val countryForRescueEventNotifications: String? = "",
    val cityForRescueEventNotifications: String? = "",
    val subscriptions: List<RemoteSubscription>? = emptyList()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "description" to description,
            "image" to image,
            "countryForRescueEventNotifications" to countryForRescueEventNotifications,
            "cityForRescueEventNotifications" to cityForRescueEventNotifications,
            "subscriptions" to subscriptions
        )
    }

    fun toDomain(): User {
        return User(
            uid = uid ?: "",
            username = username ?: "",
            description = description ?: "",
            image = image ?: "",
            isLoggedIn = false,
            countryForRescueEventNotifications = countryForRescueEventNotifications ?: "",
            cityForRescueEventNotifications = cityForRescueEventNotifications ?: "",
            subscriptions = subscriptions?.map { it.toDomain() } ?: emptyList()
        )
    }
}
