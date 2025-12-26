package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckNonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.flow.Flow

class CheckNonHumanAnimalViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) : ViewModel() {

    private val nonHumanAnimalId: String =
        saveStateHandleProvider.provideObjectRoute(CheckNonHumanAnimal::class).nonHumanAnimalId

    private val caregiverId: String =
        saveStateHandleProvider.provideObjectRoute(CheckNonHumanAnimal::class).caregiverId

    val nonHumanAnimalFlow: Flow<UiState<NonHumanAnimal>> =
        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(viewModelScope, nonHumanAnimalId, caregiverId)
}
