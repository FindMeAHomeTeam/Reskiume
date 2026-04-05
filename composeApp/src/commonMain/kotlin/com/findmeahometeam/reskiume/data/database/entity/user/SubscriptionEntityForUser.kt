package com.findmeahometeam.reskiume.data.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.user.Subscription

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("uid")
    ]
)
data class SubscriptionEntityForUser(
    @PrimaryKey
    val subscriptionId: String,
    val uid: String,
    val topic: String
) {
    fun toDomain(): Subscription {
        return Subscription(
            subscriptionId = subscriptionId,
            uid = uid,
            topic = topic
        )
    }
}
