package com.findmeahometeam.reskiume.domain.model.user

import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.remote.response.remoterUser.RemoteUser
import kotlin.String

data class User(
    val uid: String = "",
    val savedBy : String = "",
    val username: String,
    val description: String,
    val email: String? = null,
    val image: String,
    val isLoggedIn: Boolean,
    val countryForRescueEventNotifications: String,
    val cityForRescueEventNotifications: String,
    val subscriptions: List<Subscription> = emptyList()
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            uid = uid,
            savedBy = savedBy,
            username = username,
            description = description,
            image = image,
            isLoggedIn = isLoggedIn,
            countryForRescueEventNotifications = countryForRescueEventNotifications,
            cityForRescueEventNotifications = cityForRescueEventNotifications,
        )
    }

    fun toData(): RemoteUser {
        return RemoteUser(
            uid = uid,
            username = username,
            description = description,
            image = image,
            countryForRescueEventNotifications = countryForRescueEventNotifications,
            cityForRescueEventNotifications = cityForRescueEventNotifications,
            subscriptions = subscriptions.map { it.toData() }
        )
    }
}