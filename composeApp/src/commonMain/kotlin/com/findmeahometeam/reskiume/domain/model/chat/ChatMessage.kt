package com.findmeahometeam.reskiume.domain.model.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.ChatMessageEntity
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage

data class ChatMessage(
    val id: String,
    val chatId: String,
    val message: String,
    val senderId: String,
    val timestamp: Long
) {
    fun toEntity(): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            chatId = chatId,
            message = message,
            senderId = senderId,
            timestamp = timestamp
        )
    }

    fun toData(): RemoteChatMessage {
        return RemoteChatMessage(
            id = id,
            chatId = chatId,
            message = message,
            senderId = senderId,
            timestamp = timestamp
        )
    }
}
