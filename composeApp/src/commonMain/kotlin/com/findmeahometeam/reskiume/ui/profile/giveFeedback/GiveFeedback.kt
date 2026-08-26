package com.findmeahometeam.reskiume.ui.profile.giveFeedback

interface GiveFeedback {
    fun sendEmail(subject: String, body: String = "", onError: () -> Unit)
}
