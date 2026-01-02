package com.findmeahometeam.reskiume.ui.util

import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

class ManageImagePathImpl: ManageImagePath {

    override fun getCompleteImagePath(imagePath: String): String {
        val paths: List<*> = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        val documents: String = paths.first() as String
        return documents.toPath().resolve(imagePath).toString()
    }
}
