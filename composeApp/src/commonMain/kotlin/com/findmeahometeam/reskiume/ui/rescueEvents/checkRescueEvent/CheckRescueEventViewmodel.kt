package com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class CheckRescueEventViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkRescueEventUtil: CheckRescueEventUtil,
    private val checkActivistUtil: CheckActivistUtil,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) : ViewModel() {

    private val rescueEventId: String =
        saveStateHandleProvider.provideObjectRoute(CheckRescueEvent::class).rescueEventId

    private val creatorId: String =
        saveStateHandleProvider.provideObjectRoute(CheckRescueEvent::class).creatorId

    private var myUid = ""

    val rescueEventFlow: Flow<UiState<UiRescueEvent>> =
        checkRescueEventUtil.getRescueEventFlow(
            rescueEventId,
            creatorId,
            viewModelScope
        ).map { rescueEvent: RescueEvent? ->

            if (rescueEvent == null) {
                return@map null
            }
            myUid = observeAuthStateInAuthDataSource().firstOrNull()?.uid ?: " "

            val creator = checkActivistUtil.getUser(
                activistUid = rescueEvent.creatorId,
                myUserUid = myUid
            )
            if (creator == null) {
                null
            } else {
                UiRescueEvent(
                    rescueEvent = rescueEvent.copy(
                        imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                            rescueEvent.imageUrl
                        } else {
                            getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                        }
                    ),
                    allUiNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            nonHumanAnimalToRescue.nonHumanAnimalId,
                            nonHumanAnimalToRescue.caregiverId,
                            viewModelScope
                        ).firstOrNull()
                    },
                    creator = creator
                )
            }
        }.toUiState()

    fun isLoggedIn(): Boolean = myUid.isNotBlank()

    fun canIStartTheChat(): Boolean = myUid != creatorId
}
