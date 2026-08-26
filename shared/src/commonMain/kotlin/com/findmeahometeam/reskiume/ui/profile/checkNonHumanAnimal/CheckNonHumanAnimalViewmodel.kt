package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckNonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CheckNonHumanAnimalViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource
) : ViewModel() {

    private val nonHumanAnimalId: String =
        saveStateHandleProvider.provideObjectRoute(CheckNonHumanAnimal::class).nonHumanAnimalId

    private val caregiverId: String =
        saveStateHandleProvider.provideObjectRoute(CheckNonHumanAnimal::class).caregiverId

    val nonHumanAnimalFlow: StateFlow<UiState<NonHumanAnimal>> =
        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
            nonHumanAnimalId,
            caregiverId,
            viewModelScope
        ).map {
            it.copy(
                imageUrl = if (it.imageUrl.isEmpty()) {
                    it.imageUrl
                } else {
                    getImagePathForFileNameFromLocalDataSource(it.imageUrl)
                }
            )
        }.toUiState()
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = UiState.Loading()
            )
}
