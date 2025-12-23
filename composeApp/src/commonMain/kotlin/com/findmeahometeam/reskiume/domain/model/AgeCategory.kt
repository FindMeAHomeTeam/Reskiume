package com.findmeahometeam.reskiume.domain.model

import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.non_human_animal_age_category_adult
import reskiume.composeapp.generated.resources.non_human_animal_age_category_baby
import reskiume.composeapp.generated.resources.non_human_animal_age_category_senior
import reskiume.composeapp.generated.resources.non_human_animal_age_category_unselected
import reskiume.composeapp.generated.resources.non_human_animal_age_category_young

enum class AgeCategory {
    UNSELECTED,
    BABY,
    YOUNG,
    ADULT,
    SENIOR
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
