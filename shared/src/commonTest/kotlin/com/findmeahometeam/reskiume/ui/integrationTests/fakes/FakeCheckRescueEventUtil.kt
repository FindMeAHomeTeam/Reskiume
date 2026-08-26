package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCheckRescueEventUtil(
    private val rescueEventToCheck: RescueEvent = rescueEvent
): CheckRescueEventUtil {

    override fun getRescueEventFlow(
        rescueEventId: String,
        creatorId: String,
        coroutineScope: CoroutineScope
    ): Flow<RescueEvent?> {
        return if (rescueEventId == rescueEventToCheck.id && creatorId == rescueEventToCheck.creatorId) {
            flowOf(rescueEventToCheck)
        } else {
            flowOf(null)
        }
    }
}
