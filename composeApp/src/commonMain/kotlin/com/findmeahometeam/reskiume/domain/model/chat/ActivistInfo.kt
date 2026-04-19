package com.findmeahometeam.reskiume.domain.model.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.ActivistInfoEntity

data class ActivistInfo(
    val id: String,
    val chatId: String,
    val uid: String
) {
    fun toEntity(): ActivistInfoEntity {
        return ActivistInfoEntity(
            id = id,
            chatId = chatId,
            uid = uid
        )
    }
}
