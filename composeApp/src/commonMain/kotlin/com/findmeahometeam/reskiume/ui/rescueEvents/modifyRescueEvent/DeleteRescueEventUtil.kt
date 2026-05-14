package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import kotlinx.coroutines.CoroutineScope

interface DeleteRescueEventUtil {
    fun deleteRescueEvent(
        id: String,
        creatorId: String,
        nonHumanAnimalState: NonHumanAnimalState,
        coroutineScope: CoroutineScope,
        deleteOnLocal: Boolean, // In case the user is owner or not of the remote data or
        deleteOnRemote: Boolean, // the rescue event is finished and needs the local rescue event to remember the review
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
