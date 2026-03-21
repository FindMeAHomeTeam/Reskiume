package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

import kotlinx.coroutines.CoroutineScope

interface DeleteRescueEventUtil {
    fun deleteRescueEvent(
        id: String,
        creatorId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean = false, // In case the user is not owner of the remote data
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
