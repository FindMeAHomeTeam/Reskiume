package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalGenderForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalTypeForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome

@Entity
data class FosterHomeEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val savedBy: String,
    val title: String,
    val description: String,
    val conditions: String,
    val imageUrl: String,
    val longitude: Double,
    val latitude: Double,
    val country: String,
    val city: String,
    val available: Boolean
) {
    fun toDomain(
        allAcceptedNonHumanAnimalTypes: List<AcceptedNonHumanAnimalTypeForFosterHome>,
        allAcceptedNonHumanAnimalGenders: List<AcceptedNonHumanAnimalGenderForFosterHome>,
        allResidentNonHumanAnimals: List<ResidentNonHumanAnimalForFosterHome>
    ): FosterHome {
        return FosterHome(
            id = id,
            ownerId = ownerId,
            savedBy = savedBy,
            title = title,
            description = description,
            conditions = conditions,
            imageUrl = imageUrl,
            longitude = longitude,
            latitude = latitude,
            country = country,
            city = city,
            available = available,
            allAcceptedNonHumanAnimalTypes = allAcceptedNonHumanAnimalTypes,
            allAcceptedNonHumanAnimalGenders = allAcceptedNonHumanAnimalGenders,
            allResidentNonHumanAnimals = allResidentNonHumanAnimals
        )
    }
}

data class FosterHomeWithAllNonHumanAnimalData(
    @Embedded val fosterHomeEntity: FosterHomeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "fosterHomeId"
    )
    val allAcceptedNonHumanAnimalTypes: List<AcceptedNonHumanAnimalTypeEntityForFosterHome>,
    @Relation(
        parentColumn = "id",
        entityColumn = "fosterHomeId"
    )
    val allAcceptedNonHumanAnimalGenders: List<AcceptedNonHumanAnimalGenderEntityForFosterHome>,
    @Relation(
        parentColumn = "id",
        entityColumn = "fosterHomeId"
    )
    val allResidentNonHumanAnimalIds: List<ResidentNonHumanAnimalIdEntityForFosterHome>
)
