package com.findmeahometeam.reskiume.ui.fosterHomes.shareService

import android.content.Context
import android.content.Intent

class ShareServiceAndroidImpl(
    private val context: Context
): ShareService {

    override fun shareContent(
        text: String,
        onError: () -> Unit
    ) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(shareIntent, null))
        } else {
            onError()
        }
    }
}
