package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface CheckNonHumanAnimalUtil {
    fun getNonHumanAnimalFlow(
        nonHumanAnimalId: String,
        caregiverId: String,
        coroutineScope: CoroutineScope
    ): Flow<NonHumanAnimal>
}
