package com.findmeahometeam.reskiume.ui.profile.giveFeedback

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class GiveFeedbackImpl(
    private val context: Context
): GiveFeedback {

    override fun sendEmail(subject: String, body: String, onError: () -> Unit) {

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("findmeahomeappsteam@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            onError()
        }
    }
}
