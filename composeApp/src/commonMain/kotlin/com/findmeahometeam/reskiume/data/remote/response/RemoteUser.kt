package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.User

data class RemoteUser(
    val uid: String? = "",
    val username: String? = "",
    val description: String? = "",
    val email: String? = "",
    val image: String? = "",
    val available: Boolean? = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "description" to description,
            "email" to email,
            "image" to image,
            "available" to available,
        )
    }

    fun toData(): User {
        return User(
            uid = uid ?: "",
            username = username ?: "",
            description = description ?: "",
            email = email ?: "",
            image = image ?: "",
            isAvailable = available ?: true
        )
    }
}
