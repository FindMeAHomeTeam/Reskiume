package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.RescueArea
import com.findmeahometeam.reskiume.domain.model.User

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val description: String,
    val email: String,
    val imageUrl: String,
    val isAvailable: Boolean,
    val rescueArea: String
) {
    fun toDomain(): User {
        return User(
            uid = uid,
            name = name,
            description = description,
            email = email,
            imageUrl = imageUrl,
            isAvailable = isAvailable,
            rescueArea = RescueArea.fromDisplayName(rescueArea)!!
        )
    }
}