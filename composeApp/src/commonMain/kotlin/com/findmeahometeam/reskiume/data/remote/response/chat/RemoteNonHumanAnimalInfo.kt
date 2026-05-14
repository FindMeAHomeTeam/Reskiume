package com.findmeahometeam.reskiume.data.remote.response.chat

import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo

data class RemoteNonHumanAnimalInfo(
    val nonHumanAnimalId: String? = "",
    val chatId: String? = "",
    val caregiverId: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nonHumanAnimalId" to nonHumanAnimalId,
            "chatId" to chatId,
            "caregiverId" to caregiverId
        )
    }

    fun toDomain(): NonHumanAnimalInfo {
        return NonHumanAnimalInfo(
            nonHumanAnimalId = nonHumanAnimalId ?: "",
            chatId = chatId ?: "",
            caregiverId = caregiverId ?: ""
        )
    }
}
