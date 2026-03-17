package com.findmeahometeam.reskiume.domain.model

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.BIRD
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.BOBINE
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.CAT
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.DOG
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.EQUID
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.FERRET
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.FISH
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.HOG
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.OTHER
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.OVINE
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.RABBIT
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.REPTILE
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.RODENT
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType.UNSELECTED
import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.non_human_animal_adopted
import reskiume.composeapp.generated.resources.non_human_animal_age_category_adult
import reskiume.composeapp.generated.resources.non_human_animal_age_category_baby
import reskiume.composeapp.generated.resources.non_human_animal_age_category_senior
import reskiume.composeapp.generated.resources.non_human_animal_age_category_unselected
import reskiume.composeapp.generated.resources.non_human_animal_age_category_young
import reskiume.composeapp.generated.resources.non_human_animal_gender_female
import reskiume.composeapp.generated.resources.non_human_animal_gender_male
import reskiume.composeapp.generated.resources.non_human_animal_gender_unselected
import reskiume.composeapp.generated.resources.non_human_animal_looking_for_adoption
import reskiume.composeapp.generated.resources.non_human_animal_needs_to_be_rescued
import reskiume.composeapp.generated.resources.non_human_animal_rehomed
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
    val gender: Gender,
    val adoptionState: AdoptionState = AdoptionState.LOOKING_FOR_ADOPTION,
    val fosterHomeId: String = ""
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
            gender = gender,
            adoptionState = adoptionState,
            fosterHomeId = fosterHomeId
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
            gender = gender,
            adoptionState = adoptionState,
            fosterHomeId = fosterHomeId
        )
    }
}

private fun NonHumanAnimal.toSaveableList(): List<Any?> = listOf(
    id,
    caregiverId,
    savedBy,
    name,
    ageCategory,
    description,
    imageUrl,
    nonHumanAnimalType,
    gender,
    adoptionState,
    fosterHomeId
)

private fun List<Any?>.fromSaveableList(): NonHumanAnimal = NonHumanAnimal(
    id = this[0] as String,
    caregiverId = this[1] as String,
    savedBy = this[2] as String,
    name = this[3] as String,
    ageCategory = this[4] as AgeCategory,
    description = this[5] as String,
    imageUrl = this[6] as String,
    nonHumanAnimalType = this[7] as NonHumanAnimalType,
    gender = this[8] as Gender,
    adoptionState = this[9] as AdoptionState,
    fosterHomeId = this[10] as String
)

val NonHumanAnimalSaver: Saver<NonHumanAnimal?, Any> = listSaver(
    save = { it?.toSaveableList() ?: listOf(null) },
    restore = { if(it[0] == null) null else it.fromSaveableList() }
)

val NonHumanAnimalListSaver: Saver<List<NonHumanAnimal>, Any> = listSaver(
    save = { nonHumanAnimals -> nonHumanAnimals.map { it.toSaveableList() } },
    restore = { savedList -> savedList.map { it.fromSaveableList() } }
)

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

enum class AdoptionState {
    LOOKING_FOR_ADOPTION, NEEDS_TO_BE_RESCUED, REHOMED, ADOPTED
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

fun AdoptionState.toStringResource(): StringResource {
    return when (this) {
        AdoptionState.LOOKING_FOR_ADOPTION -> Res.string.non_human_animal_looking_for_adoption
        AdoptionState.NEEDS_TO_BE_RESCUED -> Res.string.non_human_animal_needs_to_be_rescued
        AdoptionState.REHOMED -> Res.string.non_human_animal_rehomed
        AdoptionState.ADOPTED -> Res.string.non_human_animal_adopted
    }
}

fun AgeCategory.toEmoji(): String {
    return when (this) {
        AgeCategory.UNSELECTED -> ""
        AgeCategory.BABY -> "💞"
        AgeCategory.YOUNG -> "💝"
        AgeCategory.ADULT -> "♥️"
        AgeCategory.SENIOR -> "💖"
    }
}

fun NonHumanAnimalType.toEmoji(): String {
    return when (this) {
        UNSELECTED -> ""
        DOG -> "🐶"
        CAT -> "🐱"
        BIRD -> "🕊"
        RABBIT -> "🐰"
        RODENT -> "🐭"
        FERRET -> "🦦"
        REPTILE -> "🦎"
        FISH -> "🐠"
        EQUID -> "🐴"
        HOG -> "🐷"
        OVINE -> "🐑"
        BOBINE -> "🐮"
        OTHER -> "❤️"
    }
}

fun Gender.toEmoji(): String {
    return when (this) {
        Gender.UNSELECTED -> ""
        Gender.FEMALE -> "🩷"
        Gender.MALE -> "💙"
    }
}
