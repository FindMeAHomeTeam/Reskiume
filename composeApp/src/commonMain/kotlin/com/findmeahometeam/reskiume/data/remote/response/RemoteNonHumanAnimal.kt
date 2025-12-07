package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.AgeCategory
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

data class RemoteNonHumanAnimal(
    val id: Int? = 0,
    val caregiverId: String? = "",
    val name: String? = "",
    val ageCategory: AgeCategory? = AgeCategory.BABY,
    val description: String? = "",
    val imageUrl: String? = "",
    val nonHumanAnimalType: NonHumanAnimalType? = NonHumanAnimalType.OTHER
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

    fun toDomain(): NonHumanAnimal {
        return NonHumanAnimal(
            id = id ?: 0,
            caregiverId = caregiverId ?: "",
            savedBy = "",
            name = name ?: "",
            ageCategory = ageCategory ?: AgeCategory.BABY,
            description = description ?: "",
            imageUrl = imageUrl ?: "",
            nonHumanAnimalType = nonHumanAnimalType ?: NonHumanAnimalType.OTHER
        )
    }
}
