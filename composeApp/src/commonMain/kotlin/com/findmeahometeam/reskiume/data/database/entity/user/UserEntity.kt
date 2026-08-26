package com.findmeahometeam.reskiume.data.database.entity.user

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Relation
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
    val cityForRescueEventNotifications: String
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
            subscriptions = subscriptions
        )
    }
}

data class UserWithAllSubscriptionData(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumns = ["uid"],
        entityColumns = ["uid"]
    )
    val allSubscriptions: List<SubscriptionEntityForUser>
)
