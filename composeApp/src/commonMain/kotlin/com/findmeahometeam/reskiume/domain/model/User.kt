package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.UserEntity

data class User(
    val uid: String = "",
    val username: String,
    val description: String,
    val email: String,
    val image: String,
    val isAvailable: Boolean = true
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            uid = uid,
            username = username,
            description = description,
            email = email,
            image = image,
            isAvailable = isAvailable
        )
    }
}