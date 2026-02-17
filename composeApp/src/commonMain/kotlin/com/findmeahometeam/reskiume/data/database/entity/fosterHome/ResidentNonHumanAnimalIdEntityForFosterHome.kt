package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = FosterHomeEntity::class,
            parentColumns = ["id"],
            childColumns = ["fosterHomeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("fosterHomeId")
    ]
)
data class ResidentNonHumanAnimalIdEntityForFosterHome(
    @PrimaryKey
    val nonHumanAnimalId: String,
    val caregiverId: String,
    val fosterHomeId: String
) {
    fun toDomain(): ResidentNonHumanAnimalForFosterHome {
        return ResidentNonHumanAnimalForFosterHome(
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId,
            fosterHomeId = fosterHomeId
        )
    }
}
