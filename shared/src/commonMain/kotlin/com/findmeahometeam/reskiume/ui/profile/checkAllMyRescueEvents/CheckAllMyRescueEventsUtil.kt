package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import kotlinx.coroutines.CoroutineScope

interface CheckAllMyRescueEventsUtil {

    suspend fun updateLocalRepositoryWithRemoteRescueEvents(
        allRemoteRescueEvents: Set<RescueEvent>,
        allLocalRescueEvents: Set<RescueEvent>,
        myUid: String,
        coroutineScope: CoroutineScope
    )
}
