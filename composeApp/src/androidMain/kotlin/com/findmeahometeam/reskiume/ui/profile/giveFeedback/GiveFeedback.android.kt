package com.findmeahometeam.reskiume.ui.profile.giveFeedback

actual interface GiveFeedback {
    actual fun sendEmail(subject: String, body: String, onError: () -> Unit)
}
