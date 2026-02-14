package com.findmeahometeam.reskiume.ui.util

// In Android, get the image path or the file name is not necessary because
// the file path does not change with every reinstall, unlike in iOS.
class ManageImagePathImpl: ManageImagePath {

    override fun getImagePathForFileName(fileName: String): String = fileName

    override fun getFileNameFromLocalImagePath(localImagePath: String): String = localImagePath
}
