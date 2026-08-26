package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class FakeCheckAllMyRescueEventsUtil: CheckAllMyRescueEventsUtil {

    override fun downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
        allRescueEventsFlow: Flow<List<RescueEvent>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<RescueEvent>> = allRescueEventsFlow
}
