package com.findmeahometeam.reskiume.data.remote.response.chat

import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage

data class RemoteChatMessage(
    val id: String? = "",
    val chatId: String? = "",
    val message: String? = "",
    val senderId: String? = "",
    val timestamp: Long? = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "chatId" to chatId,
            "message" to message,
            "senderId" to senderId,
            "timestamp" to timestamp,
        )
    }

    fun toDomain(): ChatMessage {
        return ChatMessage(
            id = id ?: "",
            chatId = chatId ?: "",
            message = message ?: "",
            senderId = senderId ?: "",
            timestamp = timestamp ?: 0
        )
    }
}
