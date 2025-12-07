package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal

data class NonHumanAnimal(
    val id: Int,
    val caregiverId: String,
    val savedBy: String,
    val name: String,
    val ageCategory: AgeCategory,
    val description: String,
    val imageUrl: String,
    val nonHumanAnimalType: NonHumanAnimalType
) {

    fun toEntity(): NonHumanAnimalEntity {
        return NonHumanAnimalEntity(
            id = id,
            caregiverId = caregiverId,
            savedBy = savedBy,
            name = name,
            ageCategory = ageCategory,
            description = description,
            imageUrl = imageUrl,
            nonHumanAnimalType = nonHumanAnimalType
        )
    }

    fun toData(): RemoteNonHumanAnimal {
        return RemoteNonHumanAnimal(
            id = id,
            caregiverId = caregiverId,
            name = name,
            ageCategory = ageCategory,
            description = description,
            imageUrl = imageUrl,
            nonHumanAnimalType = nonHumanAnimalType
        )
    }
}
