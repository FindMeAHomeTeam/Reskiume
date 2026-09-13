package com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import kotlinx.coroutines.CoroutineScope

interface CheckAllFosterHomesUtil {

    suspend fun updateLocalRepositoryWithRemoteFosterHomes(
        allRemoteFosterHomes: Set<FosterHome>,
        allLocalFosterHomes: Set<FosterHome>,
        myUid: String,
        coroutineScope: CoroutineScope
    )
}
