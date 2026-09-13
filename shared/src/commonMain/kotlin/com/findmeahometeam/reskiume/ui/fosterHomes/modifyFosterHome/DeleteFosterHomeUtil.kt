package com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome

import kotlinx.coroutines.CoroutineScope

interface DeleteFosterHomeUtil {
    fun deleteFosterHome(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        deleteOnLocal: Boolean,
        deleteOnRemote: Boolean, // In case the user is owner of the remote data
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
