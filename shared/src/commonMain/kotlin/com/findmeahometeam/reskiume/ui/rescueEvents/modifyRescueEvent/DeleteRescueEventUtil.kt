package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import kotlinx.coroutines.CoroutineScope

interface DeleteRescueEventUtil {
    fun deleteRescueEvent(
        id: String,
        creatorId: String,
        nonHumanAnimalState: NonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED,
        coroutineScope: CoroutineScope,
        deleteOnLocal: Boolean,
        deleteOnRemote: Boolean, // In case the user is owner of the remote data
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
