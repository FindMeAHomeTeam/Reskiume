package com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.UiReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class CheckFosterHomeViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkFosterHomeUtil: CheckFosterHomeUtil,
    private val checkActivistUtil: CheckActivistUtil,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    checkReviewsUtil: CheckReviewsUtil,
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository
) : ViewModel() {

    private val fosterHomeId: String =
        saveStateHandleProvider.provideObjectRoute(CheckFosterHome::class).fosterHomeId

    private val ownerId: String =
        saveStateHandleProvider.provideObjectRoute(CheckFosterHome::class).ownerId

    private var myUid = ""

    val fosterHomeFlow: Flow<UiState<UiFosterHome>> =
        checkFosterHomeUtil.getFosterHomeFlow(
            fosterHomeId,
            ownerId,
            viewModelScope
        ).map { fosterHome: FosterHome? ->
            myUid = observeAuthStateInAuthDataSource().firstOrNull()?.uid ?: ""

            val owner = checkActivistUtil.getUser(
                activistUid = fosterHome!!.ownerId,
                myUserUid = myUid
            )
            if (owner == null) {
                null
            } else {
                UiFosterHome(
                    fosterHome = fosterHome.copy(
                        imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                            fosterHome.imageUrl
                        } else {
                            getImagePathForFileNameFromLocalDataSource(fosterHome.imageUrl)
                        }
                    ),
                    uiAllResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.mapNotNull { residentNonHumanAnimal ->

                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            residentNonHumanAnimal.nonHumanAnimalId,
                            residentNonHumanAnimal.caregiverId,
                            viewModelScope
                        ).firstOrNull()
                    },
                    owner = owner
                )
            }
        }.toUiState()

    val allAvailableNonHumanAnimalsLookingForAdoptionFlow: Flow<List<NonHumanAnimal>> =
        getAllNonHumanAnimalsFromLocalRepository().map {
            it.mapNotNull { nonHumanAnimal ->
                if (nonHumanAnimal.adoptionState == AdoptionState.LOOKING_FOR_ADOPTION) {
                    nonHumanAnimal
                } else {
                    null
                }
            }
        }

    val reviewListFlow: Flow<List<UiReview>> = checkReviewsUtil.getReviewListFlow(ownerId)

    fun isLoggedIn(): Boolean = myUid.isNotBlank()
}
