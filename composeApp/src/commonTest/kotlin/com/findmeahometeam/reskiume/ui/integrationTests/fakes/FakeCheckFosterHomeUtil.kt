package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCheckFosterHomeUtil(
    private val fosterHomeToCheck: FosterHome = fosterHome
): CheckFosterHomeUtil {

    override fun getFosterHomeFlow(
        fosterHomeId: String,
        ownerId: String,
        coroutineScope: CoroutineScope
    ): Flow<FosterHome> {
        return if (fosterHomeId == fosterHomeToCheck.id && ownerId == fosterHomeToCheck.ownerId) {
            flowOf(fosterHomeToCheck)
        } else {
            flowOf()
        }
    }
}
