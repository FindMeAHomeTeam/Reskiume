package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UploadImageToRemoteDataSourceTest {

    val storageRepository: StorageRepository = mock {
        everySuspend { uploadImage(user.uid, Paths.USERS, user.image, any()) } returns Unit
    }

    private val uploadImageToRemoteDataSource =
        UploadImageToRemoteDataSource(storageRepository)

    @Test
    fun `given a user image_when the app saves it in the remote data source_then it calls to uploadImage`() =
        runTest {
            uploadImageToRemoteDataSource(user.uid, Paths.USERS, user.image, {})
            verifySuspend {
                storageRepository.uploadImage(user.uid, Paths.USERS, user.image, any())
            }
        }

    @Test
    fun `given an empty user image_when the app saves it in the remote data source_then it doesn't call to uploadImage`() =
        runTest {
            uploadImageToRemoteDataSource(user.uid, Paths.USERS, "", {})
            verifySuspend(exactly(0)) {
                storageRepository.uploadImage(user.uid, Paths.USERS, "", any())
            }
        }
}
