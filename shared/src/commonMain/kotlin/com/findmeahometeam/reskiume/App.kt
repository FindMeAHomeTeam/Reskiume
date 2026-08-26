package com.findmeahometeam.reskiume

import androidx.compose.runtime.Composable
import com.findmeahometeam.reskiume.ui.core.navigation.NavigationWrapper
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinConfiguration
import org.koin.mp.KoinPlatformTools

@Composable
fun App(config: KoinAppDeclaration = {}) {
    val koinAlreadyStarted = KoinPlatformTools.defaultContext().getOrNull() != null

    if (koinAlreadyStarted) {
        NavigationWrapper()
    } else {
        KoinApplication(
            configuration = koinConfiguration(declaration = { config.invoke(this) }),
            content = {
                NavigationWrapper()
            })
    }
}
