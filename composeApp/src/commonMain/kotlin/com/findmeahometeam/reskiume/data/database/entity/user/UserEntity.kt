package com.findmeahometeam.reskiume.data.database.entity.user

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.model.user.User

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val savedBy: String,
    val username: String,
    val description: String,
    val image: String,
    val isLoggedIn: Boolean,
    val countryForRescueEventNotifications: String,
    val cityForRescueEventNotifications: String,
    val fcmToken: String
) {
    fun toDomain(
        subscriptions: List<Subscription>
    ): User {
        return User(
            uid = uid,
            savedBy = savedBy,
            username = username,
            description = description,
            image = image,
            isLoggedIn = isLoggedIn,
            countryForRescueEventNotifications = countryForRescueEventNotifications,
            cityForRescueEventNotifications = cityForRescueEventNotifications,
            fcmToken = fcmToken,
            subscriptions = subscriptions
        )
    }
}

data class UserWithAllSubscriptionData(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "uid"
    )
    val allSubscriptions: List<SubscriptionEntityForUser>
)
