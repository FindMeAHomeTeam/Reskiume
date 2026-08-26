package com.findmeahometeam.reskiume.domain.model.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteNonHumanAnimalInfo

data class NonHumanAnimalInfo(
    val nonHumanAnimalId: String,
    val chatId: String,
    val caregiverId: String
) {
    fun toEntity(): NonHumanAnimalInfoEntity {
        return NonHumanAnimalInfoEntity(
            nonHumanAnimalId = nonHumanAnimalId,
            chatId = chatId,
            caregiverId = caregiverId
        )
    }

    fun toData(): RemoteNonHumanAnimalInfo {
        return RemoteNonHumanAnimalInfo(
            nonHumanAnimalId = nonHumanAnimalId,
            chatId = chatId,
            caregiverId = caregiverId
        )
    }
}
