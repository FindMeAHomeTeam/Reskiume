package com.findmeahometeam.reskiume.domain.model.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.BlockedUserInfoEntity
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteBlockedUserInfo

data class BlockedUserInfo(
    val id: String,
    val chatId: String,
    val uid: String
) {
    fun toEntity(): BlockedUserInfoEntity {
        return BlockedUserInfoEntity(
            id = id,
            chatId = chatId,
            uid = uid
        )
    }

    fun toData(): RemoteBlockedUserInfo {
        return RemoteBlockedUserInfo(
            id = id,
            chatId = chatId,
            uid = uid
        )
    }
}
