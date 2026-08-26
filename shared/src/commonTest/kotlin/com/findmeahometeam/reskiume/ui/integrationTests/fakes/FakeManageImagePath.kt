package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.ui.util.ManageImagePath

class FakeManageImagePath: ManageImagePath {
    override fun getImagePathForFileName(fileName: String): String = fileName

    override fun getFileNameFromLocalImagePath(localImagePath: String): String = localImagePath
}
