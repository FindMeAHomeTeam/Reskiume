package com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome

import kotlinx.coroutines.CoroutineScope

interface DeleteFosterHomeUtil {
    fun deleteFosterHome(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
