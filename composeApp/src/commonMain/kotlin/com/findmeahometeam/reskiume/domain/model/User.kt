package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.UserEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser

data class User(
    val uid: String = "",
    val username: String,
    val description: String,
    val email: String? = null,
    val image: String,
    val isAvailable: Boolean = true
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            uid = uid,
            username = username,
            description = description,
            image = image,
            isAvailable = isAvailable
        )
    }

    fun toData(): RemoteUser {
        return RemoteUser(
            uid = uid,
            username = username,
            description = description,
            image = image,
            available = isAvailable
        )
    }
}