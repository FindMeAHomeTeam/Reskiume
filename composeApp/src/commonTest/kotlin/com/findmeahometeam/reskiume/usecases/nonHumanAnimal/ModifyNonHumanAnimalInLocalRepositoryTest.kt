package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyNonHumanAnimalInLocalRepositoryTest {

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
        } returns Unit
    }

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val modifyNonHumanAnimalInLocalRepository =
        ModifyNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

    @Test
    fun `given a non human animal_when the user modify them in the local repository_then modifyNonHumanAnimal is called`() =
        runTest {
            modifyNonHumanAnimalInLocalRepository(nonHumanAnimal, {})
            verifySuspend {
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
            }
        }
}
