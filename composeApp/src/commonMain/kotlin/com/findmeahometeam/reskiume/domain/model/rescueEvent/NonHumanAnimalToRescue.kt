package com.findmeahometeam.reskiume.domain.model.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NonHumanAnimalToRescueEntityForRescueEvent
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteNonHumanAnimalToRescueForRescueEvent

data class NonHumanAnimalToRescue(
    val nonHumanAnimalId: String,
    val caregiverId: String,
    val rescueEventId: String
) {
    fun toEntity(): NonHumanAnimalToRescueEntityForRescueEvent {
        return NonHumanAnimalToRescueEntityForRescueEvent(
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId,
            rescueEventId = rescueEventId
        )
    }

    fun toData(): RemoteNonHumanAnimalToRescueForRescueEvent {
        return RemoteNonHumanAnimalToRescueForRescueEvent(
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId,
            rescueEventId = rescueEventId
        )
    }
}
