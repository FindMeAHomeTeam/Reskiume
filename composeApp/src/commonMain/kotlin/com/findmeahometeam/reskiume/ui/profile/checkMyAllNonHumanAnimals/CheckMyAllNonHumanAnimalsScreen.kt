package com.findmeahometeam.reskiume.ui.profile.checkMyAllNonHumanAnimals

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
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_non_human_animals_screen_no_non_human_animals
import reskiume.composeapp.generated.resources.check_all_non_human_animals_screen_register_non_human_animal
import reskiume.composeapp.generated.resources.check_all_non_human_animals_screen_registered_non_human_animals_title

@Composable
fun CheckAllMyNonHumanAnimalsScreen(
    onBackPressed: () -> Unit,
    onNonHumanAnimalClick: (nonHumanAnimalId: String, caregiverId: String) -> Unit,
    onCreateNonHumanAnimal: () -> Unit
) {

    val checkAllMyNonHumanAnimalsViewmodel: CheckAllMyNonHumanAnimalsViewmodel =
        koinViewModel<CheckAllMyNonHumanAnimalsViewmodel>()
    val nonHumanAnimalListState: UiState<List<NonHumanAnimal>> by checkAllMyNonHumanAnimalsViewmodel.nonHumanAnimalListFlow.collectAsState(
        initial = UiState.Loading()
    )

    RmScaffold(
        title = stringResource(Res.string.check_all_non_human_animals_screen_registered_non_human_animals_title),
        onBackPressed = onBackPressed,
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
            RmResultState(nonHumanAnimalListState) { nonHumanAnimalList ->

                if (nonHumanAnimalList.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    RmText(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        text = "🐷🐱",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp
                    )
                    RmText(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        text = stringResource(Res.string.check_all_non_human_animals_screen_no_non_human_animals),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            items = nonHumanAnimalList,
                            key = { it.hashCode() }
                        ) { nonHumanAnimal ->
                            RmListItem(
                                title = nonHumanAnimal.name,
                                titleTag = stringResource(nonHumanAnimal.adoptionState.toStringResource()),
                                titleTagColor = when (nonHumanAnimal.adoptionState) {
                                    AdoptionState.LOOKING_FOR_ADOPTION -> tertiaryGreen
                                    AdoptionState.REHOMED -> secondaryGreen
                                    AdoptionState.ADOPTED -> primaryGreen
                                },
                                description = nonHumanAnimal.description,
                                listAvatarType = RmListAvatarType.Image(nonHumanAnimal.imageUrl),
                                onClick = {
                                    onNonHumanAnimalClick(
                                        nonHumanAnimal.id,
                                        nonHumanAnimal.caregiverId
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                RmButton(
                    text = stringResource(Res.string.check_all_non_human_animals_screen_register_non_human_animal),
                    onClick = {
                        onCreateNonHumanAnimal()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
