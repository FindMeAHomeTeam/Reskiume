package com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal

import kotlinx.coroutines.CoroutineScope

interface DeleteNonHumanAnimalUtil {

    fun deleteNonHumanAnimal(
        id: String,
        caregiverId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean = false, // In case the user is not owner of the remote data
        onError: () -> Unit,
        onComplete: () -> Unit
    )
}
