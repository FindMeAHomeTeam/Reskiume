package com.findmeahometeam.reskiume.ui.profile.giveFeedback

expect interface GiveFeedback {
    fun sendEmail(subject: String, body: String = "", onError: () -> Unit)
}
