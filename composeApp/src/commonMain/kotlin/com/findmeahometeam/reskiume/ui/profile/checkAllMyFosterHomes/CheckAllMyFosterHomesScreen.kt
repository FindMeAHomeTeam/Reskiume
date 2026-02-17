package com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes

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
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmFosterHomeListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_my_foster_homes_screen_no_foster_homes
import reskiume.composeapp.generated.resources.check_all_my_foster_homes_screen_register_foster_home
import reskiume.composeapp.generated.resources.check_all_my_foster_homes_screen_title

@Composable
fun CheckAllMyFosterHomesScreen(
    myUid: String,
    onBackPressed: () -> Unit,
    onModifyFosterHome: (fosterHomeId: String) -> Unit,
    onCreateFosterHome: (ownerId: String) -> Unit
) {
    val checkAllMyFosterHomesViewmodel: CheckAllMyFosterHomesViewmodel =
        koinViewModel<CheckAllMyFosterHomesViewmodel>()

    val uiFosterHomeListState: UiState<List<UiFosterHome>> by checkAllMyFosterHomesViewmodel.fetchAllMyFosterHomes()
        .collectAsState(initial = UiState.Loading())

    RmScaffold(
        onBackPressed = onBackPressed,
        title = stringResource(Res.string.check_all_my_foster_homes_screen_title)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RmResultState(uiFosterHomeListState) { uiFosterHomeList: List<UiFosterHome> ->

                if (uiFosterHomeList.isEmpty()) {
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
                if (uiFosterHomeList.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            items = uiFosterHomeList,
                            key = { it.hashCode() }
                        ) { uiFosterHome ->
                            RmFosterHomeListItem(
                                modifier = Modifier.animateItem(),
                                title = uiFosterHome.fosterHome.title,
                                imageUrl = uiFosterHome.fosterHome.imageUrl,
                                allAcceptedNonHumanAnimals = uiFosterHome.fosterHome.allAcceptedNonHumanAnimals,
                                allResidentNonHumanAnimals = uiFosterHome.uiAllResidentNonHumanAnimals,
                                distance = null,
                                city = uiFosterHome.fosterHome.city,
                                onClick = {
                                    onModifyFosterHome(uiFosterHome.fosterHome.id)
                                },
                                isEnabled = uiFosterHome.fosterHome.available
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                RmButton(
                    text = stringResource(Res.string.check_all_my_foster_homes_screen_register_foster_home),
                    onClick = {
                        onCreateFosterHome(myUid)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
