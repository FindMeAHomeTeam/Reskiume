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

fun Map<String, Any?>.toRemoteUser(): RemoteUser {
    return RemoteUser(
        uid = this["uid"] as String?,
        username = this["username"] as String?,
        description = this["description"] as String?,
        email = this["email"] as String?,
        image = this["image"] as String?,
        isAvailable = this["isAvailable"] as Boolean?,
    )
}
