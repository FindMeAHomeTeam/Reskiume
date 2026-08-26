package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

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
data class AcceptedNonHumanAnimalEntityForFosterHome(
    @PrimaryKey
    val acceptedNonHumanAnimalId: String,
    val fosterHomeId: String,
    val acceptedNonHumanAnimalType: NonHumanAnimalType,
    val acceptedNonHumanAnimalGender: Gender
) {
    fun toDomain(): AcceptedNonHumanAnimalForFosterHome {
        return AcceptedNonHumanAnimalForFosterHome(
            acceptedNonHumanAnimalId = acceptedNonHumanAnimalId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender
        )
    }
}
