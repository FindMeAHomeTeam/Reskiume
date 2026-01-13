package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalTypeForFosterHome
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = FosterHomeEntity::class,
            parentColumns = ["id"],
            childColumns = ["fosterHomeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AcceptedNonHumanAnimalTypeEntityForFosterHome(
    @PrimaryKey(autoGenerate = true)
    val acceptedNonHumanAnimalTypeId: Int = 0,
    val fosterHomeId: String,
    val acceptedNonHumanAnimalType: NonHumanAnimalType
) {
    fun toDomain(): AcceptedNonHumanAnimalTypeForFosterHome {
        return AcceptedNonHumanAnimalTypeForFosterHome(
            acceptedNonHumanAnimalTypeId = acceptedNonHumanAnimalTypeId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType
        )
    }
}
