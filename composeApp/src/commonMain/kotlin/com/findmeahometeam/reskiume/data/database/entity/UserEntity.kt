package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.User

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val savedBy: String,
    val username: String,
    val description: String,
    val image: String,
    val isLoggedIn: Boolean,
    val country: String,
    val city: String,
    val receiveRescueNotifications: Boolean
) {
    fun toDomain(): User {
        return User(
            uid = uid,
            savedBy = savedBy,
            username = username,
            description = description,
            image = image,
            isLoggedIn = isLoggedIn,
            country = country,
            city = city,
            receiveRescueNotifications = receiveRescueNotifications
        )
    }
}