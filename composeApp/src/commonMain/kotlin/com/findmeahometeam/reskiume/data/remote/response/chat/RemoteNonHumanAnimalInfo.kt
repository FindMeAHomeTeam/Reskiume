package com.findmeahometeam.reskiume.data.remote.response.chat

import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo

data class RemoteNonHumanAnimalInfo(
    val id: String? = "",
    val chatId: String? = "",
    val nonHumanAnimalId: String? = "",
    val caregiverId: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "chatId" to chatId,
            "nonHumanAnimalId" to nonHumanAnimalId,
            "caregiverId" to caregiverId
        )
    }

    fun toDomain(): NonHumanAnimalInfo {
        return NonHumanAnimalInfo(
            id = id ?: "",
            chatId = chatId ?: "",
            nonHumanAnimalId = nonHumanAnimalId ?: "",
            caregiverId = caregiverId ?: ""
        )
    }
}
