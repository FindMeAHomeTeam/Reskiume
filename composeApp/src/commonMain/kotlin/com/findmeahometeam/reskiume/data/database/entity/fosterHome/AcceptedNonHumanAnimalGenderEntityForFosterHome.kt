package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalGenderForFosterHome
import com.findmeahometeam.reskiume.domain.model.Gender

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
data class AcceptedNonHumanAnimalGenderEntityForFosterHome(
    @PrimaryKey(autoGenerate = true)
    val acceptedNonHumanAnimalGenderId: Int = 0,
    val fosterHomeId: String,
    val acceptedNonHumanAnimalGender: Gender
) {
    fun toDomain(): AcceptedNonHumanAnimalGenderForFosterHome {
        return AcceptedNonHumanAnimalGenderForFosterHome(
            acceptedNonHumanAnimalGenderId = acceptedNonHumanAnimalGenderId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender
        )
    }
}
