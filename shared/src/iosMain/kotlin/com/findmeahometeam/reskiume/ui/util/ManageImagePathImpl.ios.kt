package com.findmeahometeam.reskiume.ui.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

class ManageImagePathImpl : ManageImagePath {

    override fun getImagePathForFileName(fileName: String): String {
        val paths: List<*> =
            NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        val documents: String = paths.first() as String
        return documents.toPath().resolve(fileName).toString()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun getFileNameFromLocalImagePath(localImagePath: String): String {

        if (localImagePath.isBlank()) return localImagePath

        val fileName = localImagePath.split("/").last()
        val sourcePath = localImagePath.split("file:/").last()
        val destinationPath = getImagePathForFileName(fileName)

        // Copy item from temp directory to documents directory, if exists
        copyItemAtPathIfExists(sourcePath, destinationPath)
        return fileName
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun copyItemAtPathIfExists(sourcePath: String, destinationPath: String) {

        if (NSFileManager.defaultManager.fileExistsAtPath(sourcePath)
            && !NSFileManager.defaultManager.fileExistsAtPath(destinationPath)
        ) {
            handleError {
                NSFileManager.defaultManager.copyItemAtPath(
                    sourcePath,
                    getImagePathForFileName(destinationPath),
                    it
                )
            }
            removeItemAtPath(sourcePath)
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun <T : Any> handleError(code: (CPointer<ObjCObjectVar<NSError?>>) -> T): T {
        return memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            val result = code(errorPtr.ptr)

            if (errorPtr.value != null) {
                throw Exception(errorPtr.value?.localizedDescription() ?: "")
            } else {
                result
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun removeItemAtPath(sourcePath: String) {

        handleError {
            NSFileManager.defaultManager.removeItemAtPath(
                sourcePath,
                it
            )
        }
    }
}
