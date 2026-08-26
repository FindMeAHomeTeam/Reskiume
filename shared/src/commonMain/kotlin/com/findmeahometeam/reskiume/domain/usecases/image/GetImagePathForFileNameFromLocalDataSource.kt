package com.findmeahometeam.reskiume.domain.usecases.image

import com.findmeahometeam.reskiume.ui.util.ManageImagePath

class GetImagePathForFileNameFromLocalDataSource(private val manageImagePath: ManageImagePath) {

    operator fun invoke(fileName: String): String = manageImagePath.getImagePathForFileName(fileName)
}
