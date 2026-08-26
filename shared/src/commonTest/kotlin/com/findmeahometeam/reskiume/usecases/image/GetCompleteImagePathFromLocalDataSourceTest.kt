package com.findmeahometeam.reskiume.usecases.image

import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetCompleteImagePathFromLocalDataSourceTest {

    val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
    }

    private val getImagePathForFileNameFromLocalDataSource =
        GetImagePathForFileNameFromLocalDataSource(manageImagePath)

    @Test
    fun `given a non human animal image_when the app gets its complete path_then it calls to getCompleteImagePath`() =
        runTest {
            getImagePathForFileNameFromLocalDataSource(nonHumanAnimal.imageUrl)
            verify {
                manageImagePath.getImagePathForFileName(nonHumanAnimal.imageUrl)
            }
        }
}
