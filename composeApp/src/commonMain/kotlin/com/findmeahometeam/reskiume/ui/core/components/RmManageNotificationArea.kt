package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_notifications
import reskiume.composeapp.generated.resources.manage_notification_area_notification_area
import reskiume.composeapp.generated.resources.manage_notification_area_disabled
import reskiume.composeapp.generated.resources.manage_notification_area_enabled
import reskiume.composeapp.generated.resources.manage_notification_area_get_rescue_events_notifications

@Composable
fun RmManageNotificationArea(
    receiveRescueNotifications: Boolean,
    onUpdateReceiveRescueNotifications: (receiveRescueNotifications: Boolean) -> Unit,
    countryForRescueEventNotifications: Country,
    cityForRescueEventNotifications: City,
    onSelectedNotificationArea: (selectedCountry: Country, selectedCity: City) -> Unit
) {
    val placeUtil: PlaceUtil = koinInject<PlaceUtil>()

    var receiveNotifications: Boolean by rememberSaveable { mutableStateOf(receiveRescueNotifications) }
    var selectedCountry: Country by rememberSaveable(countryForRescueEventNotifications) { mutableStateOf(countryForRescueEventNotifications) }
    var selectedCity: City by rememberSaveable(cityForRescueEventNotifications) { mutableStateOf(cityForRescueEventNotifications) }

    RmListSwitchItem(
        title = if (receiveNotifications) {
            stringResource(Res.string.manage_notification_area_enabled)
        } else {
            stringResource(Res.string.manage_notification_area_disabled)
        },
        description = stringResource(Res.string.manage_notification_area_get_rescue_events_notifications),
        containerColor = backgroundColor,
        listAvatarType = RmListAvatarType.Icon(
            backgroundColor = tertiaryGreen,
            icon = Res.drawable.ic_notifications,
            iconColor = primaryGreen
        ),
        isChecked = receiveNotifications,
        onCheckedChange = { isChecked ->
            onUpdateReceiveRescueNotifications(isChecked)
            receiveNotifications = isChecked
        }
    )

    AnimatedVisibility(receiveNotifications) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            RmText(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                text = stringResource(Res.string.manage_notification_area_notification_area),
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            RmCountryAndCitySelectors(
                placeUtil = placeUtil,
                selectedCountry = selectedCountry,
                selectedCity = selectedCity,
                onSelectedCountry = { country ->
                    selectedCountry = country
                },
                onSelectedCity = { city ->
                    selectedCity = city
                }
            )
        }
    }

    LaunchedEffect(selectedCountry, selectedCity) {
        onSelectedNotificationArea(selectedCountry, selectedCity)
    }
}
