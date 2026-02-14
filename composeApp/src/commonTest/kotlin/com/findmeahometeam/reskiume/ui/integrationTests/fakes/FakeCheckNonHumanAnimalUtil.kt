package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCheckNonHumanAnimalUtil(
    private val nonHumanAnimal: NonHumanAnimal = com.findmeahometeam.reskiume.nonHumanAnimal
): CheckNonHumanAnimalUtil {

    override fun getNonHumanAnimalFlow(
        nonHumanAnimalId: String,
        caregiverId: String
    ): Flow<NonHumanAnimal> = if(nonHumanAnimal.id == nonHumanAnimalId && nonHumanAnimal.caregiverId == caregiverId) {
        flowOf(nonHumanAnimal)
    } else {
        flowOf()
    }
}
