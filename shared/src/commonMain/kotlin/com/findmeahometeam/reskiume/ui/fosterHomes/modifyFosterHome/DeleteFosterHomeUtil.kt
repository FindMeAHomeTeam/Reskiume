package com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome

import kotlinx.coroutines.CoroutineScope

interface DeleteFosterHomeUtil {
    fun deleteFosterHome(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean = false, // In case the user is not owner of the remote data
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
