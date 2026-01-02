package com.findmeahometeam.reskiume.domain.usecases.image

import com.findmeahometeam.reskiume.ui.util.ManageImagePath

class GetCompleteImagePathFromLocalDataSource(private val manageImagePath: ManageImagePath) {

    operator fun invoke(imagePath: String): String = manageImagePath.getCompleteImagePath(imagePath)
}
