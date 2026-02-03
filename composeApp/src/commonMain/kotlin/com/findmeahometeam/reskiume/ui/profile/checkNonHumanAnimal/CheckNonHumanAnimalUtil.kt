package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface CheckNonHumanAnimalUtil {
    fun getNonHumanAnimalFlow(
        coroutineScope: CoroutineScope,
        nonHumanAnimalId: String,
        caregiverId: String
    ): Flow<UiState<NonHumanAnimal>>
}
