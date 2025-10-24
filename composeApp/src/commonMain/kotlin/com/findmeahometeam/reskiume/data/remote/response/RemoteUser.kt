package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.User

data class RemoteUser(
    val uid: String? = "",
    val username: String? = "",
    val description: String? = "",
    val email: String? = "",
    val image: String? = "",
    val isAvailable: Boolean? = false,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "description" to description,
            "email" to email,
            "image" to image,
            "isAvailable" to isAvailable,
        )
    }

    fun toData(): User {
        return User(
            uid = uid ?: "",
            username = username ?: "",
            description = description ?: "",
            email = email ?: "",
            image = image ?: "",
            isAvailable = isAvailable ?: true
        )
    }
}
