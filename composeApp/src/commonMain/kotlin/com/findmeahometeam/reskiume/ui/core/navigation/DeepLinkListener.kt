package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavUri

const val FOSTER_HOME_DEEP_LINK="vegan-for-the-animals://reskiu.me/fosterHome"
const val RESCUE_EVENT_DEEP_LINK="vegan-for-the-animals://reskiu.me/rescueEvent"

@Composable
fun DeepLinkListener(navController: NavController) {

    // The effect is produced only once, as `Unit` never changes
    DisposableEffect(Unit) {
        // Sets up the listener to call `NavController.navigate()`
        // for the composable that has a matching `navDeepLink` listed
        ExternalUriHandler.listener = { uri ->
            navController.navigate(NavUri(uri))
        }
        // Removes the listener when the composable is no longer active
        onDispose {
            ExternalUriHandler.listener = null
        }
    }
}
