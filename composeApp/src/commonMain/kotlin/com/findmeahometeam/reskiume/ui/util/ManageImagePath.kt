package com.findmeahometeam.reskiume.ui.util

interface ManageImagePath {
    fun getImagePathForFileName(fileName: String): String

    fun getFileNameFromLocalImagePath(localImagePath: String): String
}
