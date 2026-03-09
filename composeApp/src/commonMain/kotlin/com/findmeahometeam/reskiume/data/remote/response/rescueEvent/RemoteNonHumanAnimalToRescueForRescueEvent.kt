package com.findmeahometeam.reskiume.data.remote.response.rescueEvent

import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import kotlinx.serialization.Serializable

@Serializable
data class RemoteNonHumanAnimalToRescueForRescueEvent(
    val nonHumanAnimalId: String? = "",
    val caregiverId: String? = "",
    val rescueEventId: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nonHumanAnimalId" to nonHumanAnimalId,
            "caregiverId" to caregiverId,
            "rescueEventId" to rescueEventId
        )
    }

    fun toDomain(): NonHumanAnimalToRescue {
        return NonHumanAnimalToRescue(
            nonHumanAnimalId = nonHumanAnimalId ?: "",
            caregiverId = caregiverId ?: "",
            rescueEventId = rescueEventId ?: ""
        )
    }
}
