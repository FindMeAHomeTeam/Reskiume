package com.findmeahometeam.reskiume.data.database.entity.rescueEvent

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent

@Entity
data class RescueEventEntity(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val savedBy: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val longitude: Double,
    val latitude: Double,
    val country: String,
    val city: String
) {
    fun toDomain(
        allNonHumanAnimalsToRescue: List<NonHumanAnimalToRescue>,
        allNeedsToCover: List<NeedToCover>
    ): RescueEvent {
        return RescueEvent(
            id = id,
            creatorId = creatorId,
            savedBy = savedBy,
            title = title,
            description = description,
            imageUrl = imageUrl,
            allNonHumanAnimalsToRescue = allNonHumanAnimalsToRescue,
            allNeedsToCover = allNeedsToCover,
            longitude = longitude,
            latitude = latitude,
            country = country,
            city = city
        )
    }
}

data class RescueEventWithAllNeedsAndNonHumanAnimalData(
    @Embedded val rescueEventEntity: RescueEventEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "rescueEventId"
    )
    val allNonHumanAnimalsToRescue: List<NonHumanAnimalToRescueEntityForRescueEvent>,
    @Relation(
        parentColumn = "id",
        entityColumn = "rescueEventId"
    )
    val allNeedsToCover: List<NeedToCoverEntityForRescueEvent>
)
