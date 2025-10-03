package com.findmeahometeam.reskiume

import androidx.compose.runtime.Composable
import com.findmeahometeam.reskiume.di.authModule
import com.findmeahometeam.reskiume.di.platformModule
import com.findmeahometeam.reskiume.ui.core.navigation.NavigationWrapper
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            platformModule, authModule
        )
    }) {
        NavigationWrapper()
    }
}