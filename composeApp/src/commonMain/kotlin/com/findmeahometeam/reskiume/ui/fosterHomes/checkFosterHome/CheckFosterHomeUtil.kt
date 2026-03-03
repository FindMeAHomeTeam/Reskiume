package com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface CheckFosterHomeUtil {
    fun getFosterHomeFlow(
        fosterHomeId: String,
        ownerId: String,
        coroutineScope: CoroutineScope
    ): Flow<FosterHome>
}
