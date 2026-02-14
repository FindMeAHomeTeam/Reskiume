package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckNonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CheckNonHumanAnimalViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource
) : ViewModel() {

    private val nonHumanAnimalId: String =
        saveStateHandleProvider.provideObjectRoute(CheckNonHumanAnimal::class).nonHumanAnimalId

    private val caregiverId: String =
        saveStateHandleProvider.provideObjectRoute(CheckNonHumanAnimal::class).caregiverId

    val nonHumanAnimalFlow: Flow<UiState<NonHumanAnimal>> =
        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(nonHumanAnimalId, caregiverId).map {
            it.copy(
                imageUrl = if (it.imageUrl.isEmpty()) {
                    it.imageUrl
                } else {
                    getImagePathForFileNameFromLocalDataSource(it.imageUrl)
                }
            )
        }.toUiState()
}
