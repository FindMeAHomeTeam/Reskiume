package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtil
import kotlinx.coroutines.flow.Flow

class FakeCheckAllMyFosterHomesUtil: CheckAllMyFosterHomesUtil {

    override fun downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
        myUid: String
    ): Flow<List<FosterHome>> = allFosterHomesFlow

    override fun downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
        myUid: String
    ): Flow<List<FosterHome>> = allFosterHomesFlow
}
