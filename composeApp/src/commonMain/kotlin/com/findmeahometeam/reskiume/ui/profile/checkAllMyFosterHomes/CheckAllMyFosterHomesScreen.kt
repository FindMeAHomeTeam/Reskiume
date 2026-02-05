package com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmFosterHomeListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_my_foster_homes_screen_no_foster_homes
import reskiume.composeapp.generated.resources.check_all_my_foster_homes_screen_title

@Composable
fun CheckAllMyFosterHomesScreen(
    onBackPressed: () -> Unit,
    onFosterHomeClicked: (fosterHomeId: String) -> Unit,
    onCreateFosterHome: () -> Unit
) {
    val checkAllMyFosterHomesViewmodel: CheckAllMyFosterHomesViewmodel =
        koinViewModel<CheckAllMyFosterHomesViewmodel>()

    val fosterHomeListState: UiState<List<FosterHome>> by checkAllMyFosterHomesViewmodel.fetchAllMyFosterHomes()
        .collectAsState(initial = UiState.Loading())

    RmScaffold(
        onBackPressed = onBackPressed,
        title = stringResource(Res.string.check_all_my_foster_homes_screen_title),
        floatingActionButton = {
            //
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmResultState(fosterHomeListState) { fosterHomeList: List<FosterHome> ->

                AnimatedVisibility(visible = fosterHomeList.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        RmText(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.check_all_my_foster_homes_screen_no_foster_homes),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (fosterHomeList.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(
                            items = fosterHomeList,
                            key = { it.hashCode() }
                        ) { fosterHome ->
                            RmFosterHomeListItem(
                                modifier = Modifier.animateItem(),
                                title = fosterHome.title,
                                imageUrl = fosterHome.imageUrl,
                                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals,
                                allResidentNonHumanAnimalForFosterHome = fosterHome.allResidentNonHumanAnimals,
                                distance = null,
                                city = fosterHome.city,
                                onClick = {
                                    onFosterHomeClicked(fosterHome.id)
                                },
                                isEnabled = fosterHome.available
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
