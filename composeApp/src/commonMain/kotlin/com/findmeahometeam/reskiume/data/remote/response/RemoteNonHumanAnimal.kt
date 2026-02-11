package com.findmeahometeam.reskiume.data.remote.response

import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.AgeCategory
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.Gender

data class RemoteNonHumanAnimal(
    val id: String? = "",
    val caregiverId: String? = "",
    val name: String? = "",
    val ageCategory: AgeCategory? = AgeCategory.UNSELECTED,
    val description: String? = "",
    val imageUrl: String? = "",
    val nonHumanAnimalType: NonHumanAnimalType? = NonHumanAnimalType.UNSELECTED,
    val gender: Gender? = Gender.UNSELECTED,
    val adoptionState: AdoptionState? = AdoptionState.LOOKING_FOR_ADOPTION,
    val fosterHomeId: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "caregiverId" to caregiverId,
            "name" to name,
            "ageCategory" to ageCategory,
            "description" to description,
            "imageUrl" to imageUrl,
            "nonHumanAnimalType" to nonHumanAnimalType,
            "gender" to gender,
            "adoptionState" to adoptionState,
            "fosterHomeId" to fosterHomeId
        )
    }

    fun toDomain(): NonHumanAnimal {
        return NonHumanAnimal(
            id = id ?: "",
            caregiverId = caregiverId ?: "",
            savedBy = "",
            name = name ?: "",
            ageCategory = ageCategory ?: AgeCategory.UNSELECTED,
            description = description ?: "",
            imageUrl = imageUrl ?: "",
            nonHumanAnimalType = nonHumanAnimalType ?: NonHumanAnimalType.UNSELECTED,
            gender = gender ?: Gender.UNSELECTED,
            adoptionState = adoptionState ?: AdoptionState.LOOKING_FOR_ADOPTION,
            fosterHomeId = fosterHomeId ?: ""
        )
    }
}
