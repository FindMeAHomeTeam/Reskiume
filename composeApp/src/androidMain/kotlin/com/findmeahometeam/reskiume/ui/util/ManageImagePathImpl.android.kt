package com.findmeahometeam.reskiume.ui.util

// Update the image path is not necessary in Android because
// the file path does not change with every reinstall, unlike in iOS.
class ManageImagePathImpl: ManageImagePath {

    override fun getCompleteImagePath(imagePath: String): String = imagePath
}
