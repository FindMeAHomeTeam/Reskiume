package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.ui.util.ManageImagePath

class FakeManageImagePath: ManageImagePath {
    override fun getCompleteImagePath(imagePath: String): String = imagePath
}
