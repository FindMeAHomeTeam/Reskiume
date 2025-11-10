package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.SaveImageToLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SaveImageToLocalDataSourceTest {

    val storageRepository: StorageRepository = mock {
        everySuspend { saveImage(user.uid, Paths.USERS, any()) } returns Unit
    }

    private val saveImageToLocalDataSource =
        SaveImageToLocalDataSource(storageRepository)

    @Test
    fun `given a local user image_when the app saves it_then it calls to saveImage`() =
        runTest {
            saveImageToLocalDataSource(user.uid, Paths.USERS, {})
            verifySuspend {
                storageRepository.saveImage(user.uid, Paths.USERS, any())
            }
        }
}
