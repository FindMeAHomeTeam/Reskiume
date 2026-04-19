package com.findmeahometeam.reskiume.data.remote.response.chat

import com.findmeahometeam.reskiume.domain.model.chat.BlockedUserInfo

data class RemoteBlockedUserInfo(
    val id: String? = "",
    val chatId: String? = "",
    val uid: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "chatId" to chatId,
            "uid" to uid
        )
    }

    fun toDomain(): BlockedUserInfo {
        return BlockedUserInfo(
            id = id ?: "",
            chatId = chatId ?: "",
            uid = uid ?: ""
        )
    }
}
