package com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface CheckAllMyFosterHomesUtil {

    fun downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<FosterHome>>

    fun downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
        allFosterHomesFlow: Flow<List<FosterHome>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<FosterHome>>
}
