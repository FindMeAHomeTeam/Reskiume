package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageInLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteImageInLocalDataSourceTest {

    val storageRepository: StorageRepository = mock {
        everySuspend { deleteLocalImage(user.uid, user.image, any()) } returns Unit
    }

    private val deleteImageInLocalDataSource =
        DeleteImageInLocalDataSource(storageRepository)

    @Test
    fun `given a local user image_when the app deletes it_then it calls to deleteLocalImage`() =
        runTest {
            deleteImageInLocalDataSource(user.uid, user.image, {})
            verifySuspend {
                storageRepository.deleteLocalImage(user.uid, user.image, any())
            }
        }

    @Test
    fun `given an empty local user image_when the app deletes it_then it doesn't call to deleteLocalImage`() =
        runTest {
            deleteImageInLocalDataSource(user.uid, "", {})
            verifySuspend(exactly(0)) {
                storageRepository.deleteLocalImage(user.uid, user.image, any())
            }
        }
}
