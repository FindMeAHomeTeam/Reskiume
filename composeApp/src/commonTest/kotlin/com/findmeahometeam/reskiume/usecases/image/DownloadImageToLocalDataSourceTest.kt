package com.findmeahometeam.reskiume.usecases.image

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DownloadImageToLocalDataSourceTest {

    val storageRepository: StorageRepository = mock {
        everySuspend { downloadImage(user.uid, Section.USERS, any()) } returns Unit
    }

    private val downloadImageToLocalDataSource =
        DownloadImageToLocalDataSource(storageRepository)

    @Test
    fun `given a local user image_when the app saves it_then it calls to saveImage`() =
        runTest {
            downloadImageToLocalDataSource(user.uid, Section.USERS, {})
            verifySuspend {
                storageRepository.downloadImage(user.uid, Section.USERS, any())
            }
        }
}
