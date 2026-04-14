package com.findmeahometeam.reskiume.ui.util

import android.content.Context
import androidx.core.net.toUri
import java.io.File

class ManageImagePathImpl(
    private val context: Context
) : ManageImagePath {

    // In Android, get the image path or the file name is not necessary because
    // the file path does not change with every reinstall, unlike in iOS
    override fun getImagePathForFileName(fileName: String): String = fileName

    override fun getFileNameFromLocalImagePath(localImagePath: String): String {

        return if (localImagePath.isBlank()) {
            localImagePath
        } else {
            val uri = localImagePath.toUri()
            val cachedFile = File(uri.path ?: return localImagePath)
            if (localImagePath.contains("file:///") && cachedFile.exists()) {

                val filename = uri.lastPathSegment ?: return localImagePath
                val permanentFile = File(context.filesDir, filename)
                cachedFile.copyTo(permanentFile, overwrite = true)
                cachedFile.delete()
                permanentFile.absolutePath
            } else {
                localImagePath
            }
        }
    }
}
