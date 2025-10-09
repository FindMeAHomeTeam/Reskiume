package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.UserEntity

data class User(
    val uid: String = "",
    val name: String,
    val description: String,
    val email: String,
    val imageUrl: String,
    val isAvailable: Boolean = true
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            uid = uid,
            name = name,
            description = description,
            email = email,
            imageUrl = imageUrl,
            isAvailable = isAvailable
        )
    }
}