package com.findmeahometeam.reskiume

import androidx.compose.ui.window.ComposeUIViewController
import com.findmeahometeam.reskiume.di.dataModule
import com.findmeahometeam.reskiume.di.domainModule
import com.findmeahometeam.reskiume.di.platformModule
import com.findmeahometeam.reskiume.di.uiModule

fun MainViewController() = ComposeUIViewController {
    App(config = {
        modules(
            platformModule, domainModule, dataModule, uiModule
        )
    })
}