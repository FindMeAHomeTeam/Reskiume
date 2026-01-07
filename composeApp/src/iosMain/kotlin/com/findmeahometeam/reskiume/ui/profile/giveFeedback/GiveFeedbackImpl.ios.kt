package com.findmeahometeam.reskiume.ui.profile.giveFeedback

import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication

class GiveFeedbackImpl: GiveFeedback {

    override fun sendEmail(subject: String, body: String, onError: () -> Unit) {

        // Use NSURLComponents to build the email URL safely
        val components: NSURLComponents = NSURLComponents().apply {
            setScheme("mailto")
            setPath("findmeahomeappsteam@gmail.com")
            setQueryItems(
                listOf(
                    NSURLQueryItem(name = "subject", value = subject),
                    NSURLQueryItem(name = "body", value = body)
                )
            )
        }
        val url: NSURL? = components.URL

        // Perform the open action
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        } else {
            onError()
        }
    }
}
