package com.findmeahometeam.reskiume.ui.fosterHomes.shareService

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

class ShareServiceIosImpl: ShareService  {

    @OptIn(BetaInteropApi::class)
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
