package com.findmeahometeam.reskiume.data.database.entity.fosterHome

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Relation
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
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
        allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>,
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
            allAcceptedNonHumanAnimals = allAcceptedNonHumanAnimals,
            allResidentNonHumanAnimals = allResidentNonHumanAnimals
        )
    }
}

data class FosterHomeWithAllNonHumanAnimalData(
    @Embedded val fosterHomeEntity: FosterHomeEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["fosterHomeId"]
    )
    val allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalEntityForFosterHome>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["fosterHomeId"]
    )
    val allResidentNonHumanAnimalIds: List<ResidentNonHumanAnimalIdEntityForFosterHome>
)
