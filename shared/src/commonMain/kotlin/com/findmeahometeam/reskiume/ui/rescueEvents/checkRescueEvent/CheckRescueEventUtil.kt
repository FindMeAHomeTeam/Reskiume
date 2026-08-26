package com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface CheckRescueEventUtil {
    fun getRescueEventFlow(
        rescueEventId: String,
        creatorId: String,
        coroutineScope: CoroutineScope
    ): Flow<RescueEvent?>
}
