package com.findmeahometeam.reskiume.ui.core.navigation.bottomNavigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.navigation.Routes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.bottom_bar_item_chats
import reskiume.composeapp.generated.resources.bottom_bar_item_chats_content_description
import reskiume.composeapp.generated.resources.bottom_bar_item_foster_homes
import reskiume.composeapp.generated.resources.bottom_bar_item_foster_homes_content_description
import reskiume.composeapp.generated.resources.bottom_bar_item_profile
import reskiume.composeapp.generated.resources.bottom_bar_item_profile_content_description
import reskiume.composeapp.generated.resources.bottom_bar_item_rescue
import reskiume.composeapp.generated.resources.bottom_bar_item_rescue_content_description
import reskiume.composeapp.generated.resources.ic_chats
import reskiume.composeapp.generated.resources.ic_foster_homes
import reskiume.composeapp.generated.resources.ic_user
import reskiume.composeapp.generated.resources.ic_rescue_events

sealed class BottomBarItem {

    abstract val route: String
    abstract val title: @Composable () -> String
    abstract val icon: @Composable () -> Unit


    data class FosterHomes(
        override val route: String = Routes.FOSTER_HOMES.route,
        override val title: @Composable () -> String = { stringResource(Res.string.bottom_bar_item_foster_homes) },
        override val icon: @Composable (() -> Unit) = {
            Icon(
                modifier = Modifier.size(24.dp),
                contentDescription = stringResource(Res.string.bottom_bar_item_foster_homes_content_description),
                painter = painterResource(Res.drawable.ic_foster_homes)
            )
        }
    ) : BottomBarItem()

    data class RescueEvents(
        override val route: String = Routes.RESCUE.route,
        override val title: @Composable () -> String = { stringResource(Res.string.bottom_bar_item_rescue) },
        override val icon: @Composable (() -> Unit) = {
            Icon(
                modifier = Modifier.size(24.dp),
                contentDescription = stringResource(Res.string.bottom_bar_item_rescue_content_description),
                painter = painterResource(Res.drawable.ic_rescue_events)
            )
        }
    ) : BottomBarItem()

    data class Chats(
        override val route: String = Routes.CHATS.route,
        override val title: @Composable () -> String = { stringResource(Res.string.bottom_bar_item_chats) },
        override val icon: @Composable (() -> Unit) = {
            Icon(
                modifier = Modifier.size(24.dp),
                contentDescription = stringResource(Res.string.bottom_bar_item_chats_content_description),
                painter = painterResource(Res.drawable.ic_chats)
            )
        }
    ) : BottomBarItem()

    data class Profile(
        override val route: String = Routes.PROFILE.route,
        override val title: @Composable () -> String = { stringResource(Res.string.bottom_bar_item_profile) },
        override val icon: @Composable (() -> Unit) = {
            Icon(
                modifier = Modifier.size(24.dp),
                contentDescription = stringResource(Res.string.bottom_bar_item_profile_content_description),
                painter = painterResource(Res.drawable.ic_user)
            )
        }
    ) : BottomBarItem()
}