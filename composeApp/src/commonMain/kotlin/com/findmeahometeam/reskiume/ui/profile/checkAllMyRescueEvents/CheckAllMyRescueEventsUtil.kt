package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface CheckAllMyRescueEventsUtil {

    fun downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
        allRescueEventsFlow: Flow<List<RescueEvent>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<RescueEvent>>

    fun downloadImageAndModifyRescueEventsInLocalRepositoryFromFlow(
        allRescueEventsFlow: Flow<List<RescueEvent>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<RescueEvent>>
}
