package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.UserEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import kotlin.String

data class User(
    val uid: String = "",
    val savedBy : String = "",
    val username: String,
    val description: String,
    val email: String? = null,
    val image: String,
    val isLoggedIn: Boolean,
    val country: String,
    val city: String,
    val receiveRescueNotifications : Boolean
) {
    fun toEntity(): UserEntity {
        return UserEntity(
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

    fun toData(): RemoteUser {
        return RemoteUser(
            uid = uid,
            username = username,
            description = description,
            image = image,
            country = country,
            city = city,
            receiveRescueNotifications = receiveRescueNotifications
        )
    }
}