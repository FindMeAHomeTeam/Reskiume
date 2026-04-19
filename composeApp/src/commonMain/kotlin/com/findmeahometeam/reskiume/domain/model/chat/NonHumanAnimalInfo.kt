package com.findmeahometeam.reskiume.domain.model.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteNonHumanAnimalInfo

data class NonHumanAnimalInfo(
    val id: String,
    val chatId: String,
    val nonHumanAnimalId: String,
    val caregiverId: String
) {
    fun toEntity(): NonHumanAnimalInfoEntity {
        return NonHumanAnimalInfoEntity(
            id = id,
            chatId = chatId,
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId
        )
    }

    fun toData(): RemoteNonHumanAnimalInfo {
        return RemoteNonHumanAnimalInfo(
            id = id,
            chatId = chatId,
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId
        )
    }
}
