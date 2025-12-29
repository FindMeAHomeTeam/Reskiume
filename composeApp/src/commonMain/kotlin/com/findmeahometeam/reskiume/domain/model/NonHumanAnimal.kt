package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class NonHumanAnimal(
    val id: String = "",
    val caregiverId: String,
    val savedBy: String = "",
    val name: String,
    val ageCategory: AgeCategory,
    val description: String,
    val imageUrl: String,
    val nonHumanAnimalType: NonHumanAnimalType,
    val gender: Gender
) {

    @OptIn(ExperimentalTime::class)
    private fun setId(): String =
        id.ifBlank { Clock.System.now().epochSeconds.toString() + caregiverId }

    fun toEntity(): NonHumanAnimalEntity {
        return NonHumanAnimalEntity(
            id = id.ifBlank { setId() },
            caregiverId = caregiverId,
            savedBy = savedBy,
            name = name,
            ageCategory = ageCategory,
            description = description,
            imageUrl = imageUrl,
            nonHumanAnimalType = nonHumanAnimalType,
            gender = gender
        )
    }

    fun toData(): RemoteNonHumanAnimal {
        return RemoteNonHumanAnimal(
            id = id.ifBlank { setId() },
            caregiverId = caregiverId,
            name = name,
            ageCategory = ageCategory,
            description = description,
            imageUrl = imageUrl,
            nonHumanAnimalType = nonHumanAnimalType,
            gender = gender
        )
    }
}
