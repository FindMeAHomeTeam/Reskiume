package com.findmeahometeam.reskiume.ui.fosterHomes.shareService

interface ShareService {
    fun shareContent(
        text: String,
        onError: () -> Unit
    )
}
