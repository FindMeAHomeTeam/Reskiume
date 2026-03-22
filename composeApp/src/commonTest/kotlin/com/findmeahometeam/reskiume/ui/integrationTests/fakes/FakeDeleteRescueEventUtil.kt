package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import kotlinx.coroutines.CoroutineScope

class FakeDeleteRescueEventUtil(
    private val rescueEventToDelete: RescueEvent = rescueEvent
): DeleteRescueEventUtil {

    override fun deleteRescueEvent(
        id: String,
        creatorId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        if (rescueEventToDelete.id == id && rescueEventToDelete.creatorId == creatorId) {
            onComplete()
        } else {
            onError()
        }
    }
}
