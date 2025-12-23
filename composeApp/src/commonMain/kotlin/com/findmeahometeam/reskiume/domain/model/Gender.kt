package com.findmeahometeam.reskiume.domain.model

import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.non_human_animal_gender_female
import reskiume.composeapp.generated.resources.non_human_animal_gender_male
import reskiume.composeapp.generated.resources.non_human_animal_gender_unselected

enum class Gender {
    UNSELECTED, FEMALE, MALE
}

fun Gender.toStringResource(): StringResource {
    return when(this) {
        Gender.UNSELECTED -> Res.string.non_human_animal_gender_unselected
        Gender.FEMALE -> Res.string.non_human_animal_gender_female
        Gender.MALE -> Res.string.non_human_animal_gender_male
    }
}
