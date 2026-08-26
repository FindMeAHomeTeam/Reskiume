package com.findmeahometeam.reskiume.data.database.entity.rescueEvent

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueNeed

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
data class NeedToCoverEntityForRescueEvent(
    @PrimaryKey
    val needToCoverId: String,
    val rescueNeed: RescueNeed,
    val rescueEventId: String
) {
    fun toDomain(): NeedToCover {
        return NeedToCover(
            needToCoverId = needToCoverId,
            rescueNeed = rescueNeed,
            rescueEventId = rescueEventId
        )
    }
}
