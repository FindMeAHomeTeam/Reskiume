package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.*
import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.non_human_animal_age_category_adult
import reskiume.composeapp.generated.resources.non_human_animal_age_category_baby
import reskiume.composeapp.generated.resources.non_human_animal_age_category_senior
import reskiume.composeapp.generated.resources.non_human_animal_age_category_unselected
import reskiume.composeapp.generated.resources.non_human_animal_age_category_young
import reskiume.composeapp.generated.resources.non_human_animal_gender_female
import reskiume.composeapp.generated.resources.non_human_animal_gender_male
import reskiume.composeapp.generated.resources.non_human_animal_gender_unselected
import reskiume.composeapp.generated.resources.non_human_animal_type_bird
import reskiume.composeapp.generated.resources.non_human_animal_type_bobine
import reskiume.composeapp.generated.resources.non_human_animal_type_cat
import reskiume.composeapp.generated.resources.non_human_animal_type_dog
import reskiume.composeapp.generated.resources.non_human_animal_type_equid
import reskiume.composeapp.generated.resources.non_human_animal_type_ferret
import reskiume.composeapp.generated.resources.non_human_animal_type_fish
import reskiume.composeapp.generated.resources.non_human_animal_type_hog
import reskiume.composeapp.generated.resources.non_human_animal_type_other
import reskiume.composeapp.generated.resources.non_human_animal_type_ovine
import reskiume.composeapp.generated.resources.non_human_animal_type_rabbit
import reskiume.composeapp.generated.resources.non_human_animal_type_reptile
import reskiume.composeapp.generated.resources.non_human_animal_type_rodent
import reskiume.composeapp.generated.resources.non_human_animal_type_unselected
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

enum class AgeCategory {
    UNSELECTED,
    BABY,
    YOUNG,
    ADULT,
    SENIOR
}

enum class NonHumanAnimalType {
    UNSELECTED,
    DOG,
    CAT,
    BIRD,
    RABBIT,
    RODENT,
    FERRET,
    REPTILE,
    FISH,
    EQUID,
    HOG,
    OVINE,
    BOBINE,
    OTHER
}

enum class Gender {
    UNSELECTED, FEMALE, MALE
}

fun AgeCategory.toStringResource(): StringResource {
    return when (this) {
        AgeCategory.UNSELECTED -> Res.string.non_human_animal_age_category_unselected
        AgeCategory.BABY -> Res.string.non_human_animal_age_category_baby
        AgeCategory.YOUNG -> Res.string.non_human_animal_age_category_young
        AgeCategory.ADULT -> Res.string.non_human_animal_age_category_adult
        AgeCategory.SENIOR -> Res.string.non_human_animal_age_category_senior
    }
}

fun NonHumanAnimalType.toStringResource(): StringResource {
    return when (this) {
        UNSELECTED -> Res.string.non_human_animal_type_unselected
        DOG -> Res.string.non_human_animal_type_dog
        CAT -> Res.string.non_human_animal_type_cat
        BIRD -> Res.string.non_human_animal_type_bird
        RABBIT -> Res.string.non_human_animal_type_rabbit
        RODENT -> Res.string.non_human_animal_type_rodent
        FERRET -> Res.string.non_human_animal_type_ferret
        REPTILE -> Res.string.non_human_animal_type_reptile
        FISH -> Res.string.non_human_animal_type_fish
        EQUID -> Res.string.non_human_animal_type_equid
        HOG -> Res.string.non_human_animal_type_hog
        OVINE -> Res.string.non_human_animal_type_ovine
        BOBINE -> Res.string.non_human_animal_type_bobine
        OTHER -> Res.string.non_human_animal_type_other
    }
}

fun Gender.toStringResource(): StringResource {
    return when (this) {
        Gender.UNSELECTED -> Res.string.non_human_animal_gender_unselected
        Gender.FEMALE -> Res.string.non_human_animal_gender_female
        Gender.MALE -> Res.string.non_human_animal_gender_male
    }
}
