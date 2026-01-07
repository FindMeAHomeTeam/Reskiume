package com.findmeahometeam.reskiume.ui.profile.checkAllAdvice

import com.findmeahometeam.reskiume.domain.model.Advice
import com.findmeahometeam.reskiume.domain.model.AdviceImage
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.advice_care_bloat_torsion_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_care_bloat_torsion_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_care_feed_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_care_feed_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rehome_find_a_home_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rehome_find_a_home_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rehome_visit_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rehome_visit_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rescue_found_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rescue_found_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rescue_pick_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rescue_pick_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rescue_rejected_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rescue_rejected_non_human_animal_title

val rescueAdviceList = listOf(
    Advice(
        title = Res.string.advice_rescue_found_non_human_animal_title,
        description = Res.string.advice_rescue_found_non_human_animal_description,
        image = AdviceImage.RESCUE
    ),
    Advice(
        title = Res.string.advice_rescue_pick_non_human_animal_title,
        description = Res.string.advice_rescue_pick_non_human_animal_description,
        image = AdviceImage.RESCUE
    ),
    Advice(
        title = Res.string.advice_rescue_rejected_non_human_animal_title,
        description = Res.string.advice_rescue_rejected_non_human_animal_description,
        image = AdviceImage.RESCUE
    )
)

val rehomeAdviceList = listOf(
    Advice(
        title = Res.string.advice_rehome_find_a_home_non_human_animal_title,
        description = Res.string.advice_rehome_find_a_home_non_human_animal_description,
        image = AdviceImage.REHOME
    ),
    Advice(
        title = Res.string.advice_rehome_visit_non_human_animal_title,
        description = Res.string.advice_rehome_visit_non_human_animal_description,
        image = AdviceImage.REHOME
    )
)

val careAdviceList = listOf(
    Advice(
        title = Res.string.advice_care_feed_non_human_animal_title,
        description = Res.string.advice_care_feed_non_human_animal_description,
        image = AdviceImage.CARE
    ),
    Advice(
        title = Res.string.advice_care_bloat_torsion_non_human_animal_title,
        description = Res.string.advice_care_bloat_torsion_non_human_animal_description,
        image = AdviceImage.CARE
    )
)
