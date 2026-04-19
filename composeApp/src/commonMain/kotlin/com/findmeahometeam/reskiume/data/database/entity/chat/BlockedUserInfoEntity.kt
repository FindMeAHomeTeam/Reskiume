package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.chat.BlockedUserInfo

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("chatId")
    ]
)
data class BlockedUserInfoEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val uid: String
) {
    fun toDomain(): BlockedUserInfo {
        return BlockedUserInfo(
            id = id,
            chatId = chatId,
            uid = uid
        )
    }
}
