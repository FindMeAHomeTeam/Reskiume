package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.User

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val description: String,
    val email: String,
    val image: String,
    val isAvailable: Boolean
) {
    fun toDomain(): User {
        return User(
            uid = uid,
            username = username,
            description = description,
            email = email,
            image = image,
            isAvailable = isAvailable
        )
    }
}