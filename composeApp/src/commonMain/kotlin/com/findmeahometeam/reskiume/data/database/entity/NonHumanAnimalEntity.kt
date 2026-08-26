package com.findmeahometeam.reskiume.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.AgeCategory
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.Gender

@Entity
data class NonHumanAnimalEntity(
    @PrimaryKey val id: String,
    val caregiverId: String,
    val savedBy: String,
    val name: String,
    val ageCategory: AgeCategory,
    val description: String,
    val imageUrl: String,
    val nonHumanAnimalType: NonHumanAnimalType,
    val gender: Gender,
    val nonHumanAnimalState: NonHumanAnimalState,
    val fosterHomeId: String
) {

    fun toDomain(): NonHumanAnimal {
        return NonHumanAnimal(
            id = id,
            caregiverId = caregiverId,
            savedBy = savedBy,
            name = name,
            ageCategory = ageCategory,
            description = description,
            imageUrl = imageUrl,
            nonHumanAnimalType = nonHumanAnimalType,
            gender = gender,
            nonHumanAnimalState = nonHumanAnimalState,
            fosterHomeId = fosterHomeId
        )
    }
}
