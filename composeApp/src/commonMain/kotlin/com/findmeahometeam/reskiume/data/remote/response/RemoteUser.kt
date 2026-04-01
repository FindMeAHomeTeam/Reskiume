package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.User

data class RemoteUser(
    val uid: String? = "",
    val username: String? = "",
    val description: String? = "",
    val image: String? = "",
    val country: String? = "",
    val city: String? = "",
    val receiveRescueNotifications: Boolean? = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "description" to description,
            "image" to image,
            "country" to country,
            "city" to city,
            "receiveRescueNotifications" to receiveRescueNotifications
        )
    }

    fun toDomain(): User {
        return User(
            uid = uid ?: "",
            savedBy = "",
            username = username ?: "",
            description = description ?: "",
            image = image ?: "",
            country = country ?: "",
            city = city ?: "",
            isLoggedIn = false,
            receiveRescueNotifications = receiveRescueNotifications ?: false
        )
    }
}
