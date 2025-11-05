package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.User

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val description: String,
    val image: String,
    val isAvailable: Boolean,
    val lastLogout: Long
) {
    fun toDomain(): User {
        return User(
            uid = uid,
            username = username,
            description = description,
            image = image,
            isAvailable = isAvailable,
            lastLogout = lastLogout
        )
    }
}