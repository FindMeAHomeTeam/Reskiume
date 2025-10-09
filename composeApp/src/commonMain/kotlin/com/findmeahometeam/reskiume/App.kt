package com.findmeahometeam.reskiume

import androidx.compose.runtime.Composable
import com.findmeahometeam.reskiume.di.dataModule
import com.findmeahometeam.reskiume.di.domainModule
import com.findmeahometeam.reskiume.di.platformModule
import com.findmeahometeam.reskiume.di.uiModule
import com.findmeahometeam.reskiume.ui.core.navigation.NavigationWrapper
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@Composable
@Preview
fun App(config:KoinAppDeclaration? = null) {
    KoinApplication(application = {
        config?.invoke(this)
        modules(
            platformModule, domainModule, dataModule, uiModule
        )
    }) {
        NavigationWrapper()
    }
}
