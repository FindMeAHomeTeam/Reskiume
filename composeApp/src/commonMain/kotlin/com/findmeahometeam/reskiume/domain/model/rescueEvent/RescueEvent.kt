package com.findmeahometeam.reskiume.domain.model.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventEntity
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class RescueEvent(
    val id: String,
    val creatorId: String,
    val savedBy: String = "",
    val title: String,
    val description: String,
    val imageUrl: String,
    val allNonHumanAnimalsToRescue: List<NonHumanAnimalToRescue>,
    val allNeedsToCover: List<NeedToCover>,
    val longitude: Double,
    val latitude: Double,
    val country: String,
    val city: String
) {
    @OptIn(ExperimentalTime::class)
    private fun setId(): String =
        id.ifBlank { Clock.System.now().epochSeconds.toString() + creatorId }

    fun toEntity(): RescueEventEntity {
        return RescueEventEntity(
            id = id.ifBlank { setId() },
            creatorId = creatorId,
            savedBy = savedBy,
            title = title,
            description = description,
            imageUrl = imageUrl,
            longitude = longitude,
            latitude = latitude,
            country = country,
            city = city
        )
    }

    fun toData(): RemoteRescueEvent {
        return RemoteRescueEvent(
            id = id.ifBlank { setId() },
            creatorId = creatorId,
            title = title,
            description = description,
            imageUrl = imageUrl,
            allNonHumanAnimalsToRescue = allNonHumanAnimalsToRescue.map { it.toData() },
            allNeedsToCover = allNeedsToCover.map { it.toData() },
            longitude = longitude,
            latitude = latitude,
            country = country,
            city = city
        )
    }
}
