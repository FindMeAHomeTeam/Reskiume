package com.findmeahometeam.reskiume.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyNonHumanAnimalInLocalRepositoryTest {

    val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

        every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
    }

    val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
        everySuspend {
            modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
        } returns Unit
    }

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val modifyNonHumanAnimalInLocalRepository =
        ModifyNonHumanAnimalInLocalRepository(manageImagePath, localNonHumanAnimalRepository, authRepository)

    @Test
    fun `given a non human animal_when the user modify them in the local repository_then getFileNameFromLocalImagePath and modifyNonHumanAnimal are called`() =
        runTest {
            modifyNonHumanAnimalInLocalRepository(nonHumanAnimal, {})
            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl)
                localNonHumanAnimalRepository.modifyNonHumanAnimal(nonHumanAnimal.toEntity(), any())
            }
        }
}
