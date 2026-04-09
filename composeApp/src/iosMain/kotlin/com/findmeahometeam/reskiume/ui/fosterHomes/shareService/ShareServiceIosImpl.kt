package com.findmeahometeam.reskiume.ui.fosterHomes.shareService

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.popoverPresentationController

class ShareServiceIosImpl : ShareService {

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    override fun shareContent(
        text: String,
        onError: () -> Unit
    ) {
        val activityItems: List<Any> = listOf(NSString.create(string = text))

        val activityViewController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )

        // Get the top-most view controller to present the activity view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (rootViewController != null) {

            // Check if the device is an iPad
            if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
                val uiView = rootViewController.view
                activityViewController.popoverPresentationController?.sourceView = uiView

                // Point the popover to the center of the screen
                uiView.bounds.useContents {
                    activityViewController.popoverPresentationController?.sourceRect = CGRectMake(
                        x = size.width / 2.0,
                        y = size.height / 2.0,
                        width = 0.0,
                        height = 0.0
                    )
                }
                // Disable the arrow so it just appears as a centered modal sheet
                activityViewController.popoverPresentationController?.permittedArrowDirections = 0u
            }

            rootViewController.presentViewController(
                activityViewController,
                animated = true,
                completion = null
            )
        } else {
            onError()
        }
    }
}
