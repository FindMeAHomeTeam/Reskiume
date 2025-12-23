package com.findmeahometeam.reskiume.domain.model

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
import reskiume.composeapp.generated.resources.non_human_animal_type_bird
import reskiume.composeapp.generated.resources.non_human_animal_type_bobine
import reskiume.composeapp.generated.resources.non_human_animal_type_cat
import reskiume.composeapp.generated.resources.non_human_animal_type_dog
import reskiume.composeapp.generated.resources.non_human_animal_type_equid
import reskiume.composeapp.generated.resources.non_human_animal_type_ferret
import reskiume.composeapp.generated.resources.non_human_animal_type_hog
import reskiume.composeapp.generated.resources.non_human_animal_type_other
import reskiume.composeapp.generated.resources.non_human_animal_type_ovine
import reskiume.composeapp.generated.resources.non_human_animal_type_rabbit
import reskiume.composeapp.generated.resources.non_human_animal_type_reptile
import reskiume.composeapp.generated.resources.non_human_animal_type_rodent
import reskiume.composeapp.generated.resources.non_human_animal_type_unselected

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
        FISH -> Res.string.non_human_animal_type_reptile
        EQUID -> Res.string.non_human_animal_type_equid
        HOG -> Res.string.non_human_animal_type_hog
        OVINE -> Res.string.non_human_animal_type_ovine
        BOBINE -> Res.string.non_human_animal_type_bobine
        OTHER -> Res.string.non_human_animal_type_other
    }
}
