package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtil
import kotlinx.coroutines.CoroutineScope

class FakeDeleteFosterHomeUtil(
    private val fosterHomeToDelete: FosterHome = fosterHome
): DeleteFosterHomeUtil {

    override fun deleteFosterHome(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        if (fosterHomeToDelete.id == id && fosterHomeToDelete.ownerId == ownerId) {
            onComplete()
        } else {
            onError()
        }
    }
}
