package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome

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
data class ResidentNonHumanAnimalIdEntityForFosterHome(
    @PrimaryKey
    val residentNonHumanAnimalId: String,
    val caregiverId: String,
    val fosterHomeId: String
) {
    suspend fun toDomain(onFetchNonHumanAnimal: suspend (nonHumanAnimalId: String, caregiverId: String) -> NonHumanAnimal?): ResidentNonHumanAnimalForFosterHome {
        return ResidentNonHumanAnimalForFosterHome(
            residentNonHumanAnimal = onFetchNonHumanAnimal(residentNonHumanAnimalId, caregiverId),
            fosterHomeId = fosterHomeId
        )
    }
}
