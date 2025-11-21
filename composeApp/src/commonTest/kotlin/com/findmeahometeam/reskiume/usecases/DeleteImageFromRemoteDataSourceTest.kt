package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteImageFromRemoteDataSourceTest {

    val storageRepository: StorageRepository = mock {
        everySuspend { deleteRemoteImage(user.uid, Section.USERS, any()) } returns Unit
    }

    private val deleteImageFromRemoteDataSource =
        DeleteImageFromRemoteDataSource(storageRepository)

    @Test
    fun `given a remote user image_when the app deletes it_then it calls to deleteRemoteImage`() =
        runTest {
            deleteImageFromRemoteDataSource(user.uid, Section.USERS, user.image, {})
            verifySuspend {
                storageRepository.deleteRemoteImage(user.uid, Section.USERS, any())
            }
        }

    @Test
    fun `given an empty remote user image_when the app deletes it_then it doesn't call to deleteRemoteImage`() =
        runTest {
            deleteImageFromRemoteDataSource(user.uid, Section.USERS, "", {})
            verifySuspend(exactly(0)) {
                storageRepository.deleteRemoteImage(user.uid, Section.USERS, any())
            }
        }
}
