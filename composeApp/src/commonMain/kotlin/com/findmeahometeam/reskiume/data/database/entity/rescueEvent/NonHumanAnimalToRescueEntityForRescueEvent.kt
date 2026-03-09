package com.findmeahometeam.reskiume.data.database.entity.rescueEvent

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RescueEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["rescueEventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("rescueEventId")
    ]
)
data class NonHumanAnimalToRescueEntityForRescueEvent(
    @PrimaryKey
    val nonHumanAnimalId: String,
    val caregiverId: String,
    val rescueEventId: String
) {
    fun toDomain(): NonHumanAnimalToRescue {
        return NonHumanAnimalToRescue(
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId,
            rescueEventId = rescueEventId
        )
    }
}
