package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.UserEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser

data class User(
    val uid: String = "",
    val username: String,
    val description: String,
    val email: String,
    val image: String,
    val isAvailable: Boolean = true,
    val lastLogout: Long = 0L
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            uid = uid,
            username = username,
            description = description,
            email = email,
            image = image,
            isAvailable = isAvailable,
            lastLogout = lastLogout
        )
    }

    fun toData(): RemoteUser {
        return RemoteUser(
            uid = uid,
            username = username,
            description = description,
            email = email,
            image = image,
            available = isAvailable
        )
    }
}