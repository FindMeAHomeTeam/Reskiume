package com.findmeahometeam.reskiume.ui.profile.personalInformation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.user_screen_log_out_button

@Composable
fun PersonalInformationScreen(onBackPressed: () -> Unit) {

    val personalInformationViewmodel: PersonalInformationViewmodel = koinViewModel<PersonalInformationViewmodel>()

    RmScaffold(
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // TODO

            RmButton(text = stringResource(Res.string.user_screen_log_out_button)) {
                personalInformationViewmodel.logOut()
                onBackPressed()
            }
        }
    }

}