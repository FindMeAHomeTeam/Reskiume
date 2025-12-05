package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.AgeCategory
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

data class RemoteNonHumanAnimal(
    val id: String,
    val caregiverId: String,
    val name: String,
    val ageCategory: AgeCategory,
    val description: String,
    val imageUrl: String,
    val nonHumanAnimalType: NonHumanAnimalType
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "caregiverId" to caregiverId,
            "name" to name,
            "ageCategory" to ageCategory,
            "description" to description,
            "imageUrl" to imageUrl,
            "nonHumanAnimalType" to nonHumanAnimalType
        )
    }

    fun toData(): NonHumanAnimal {
        return NonHumanAnimal(
            id = id,
            caregiverId = caregiverId,
            savedBy = "",
            name = name,
            ageCategory = ageCategory,
            description = description,
            imageUrl = imageUrl,
            nonHumanAnimalType = nonHumanAnimalType
        )
    }
}
