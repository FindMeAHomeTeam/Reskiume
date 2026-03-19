package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class FakeCheckNonHumanAnimalUtil(
    private val nonHumanAnimalToReviewList: List<NonHumanAnimal> = listOf(nonHumanAnimal)
): CheckNonHumanAnimalUtil {

    override fun getNonHumanAnimalFlow(
        nonHumanAnimalId: String,
        caregiverId: String,
        coroutineScope: CoroutineScope
    ): Flow<NonHumanAnimal> = nonHumanAnimalToReviewList.filter { it.id == nonHumanAnimalId && it.caregiverId == caregiverId }.asFlow()
}
