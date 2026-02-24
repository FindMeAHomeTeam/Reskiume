package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope

class FakeDeleteNonHumanAnimalUtil(
    private val nonHumanAnimalToDelete: NonHumanAnimal = nonHumanAnimal
): DeleteNonHumanAnimalUtil {

    override fun deleteNonHumanAnimal(
        id: String,
        caregiverId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        if (nonHumanAnimalToDelete.id == id && nonHumanAnimalToDelete.caregiverId == caregiverId) {
            onComplete()
        } else {
            onError()
        }
    }
}
