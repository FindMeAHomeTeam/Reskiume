package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage

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
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val message: String,
    val senderId: String,
    val timestamp: Long
) {
    fun toDomain(): ChatMessage {
        return ChatMessage(
            id = id,
            chatId = chatId,
            message = message,
            senderId = senderId,
            timestamp = timestamp
        )
    }
}
