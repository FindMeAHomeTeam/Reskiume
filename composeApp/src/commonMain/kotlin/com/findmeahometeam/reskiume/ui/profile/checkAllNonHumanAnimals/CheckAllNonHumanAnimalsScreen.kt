package com.findmeahometeam.reskiume.ui.profile.checkAllNonHumanAnimals

import androidx.compose.foundation.background
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
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListItem
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_non_human_animals_screen_no_non_human_animals
import reskiume.composeapp.generated.resources.check_all_non_human_animals_screen_register_non_human_animal
import reskiume.composeapp.generated.resources.check_all_non_human_animals_screen_registered_non_human_animals_title

@Composable
fun CheckAllNonHumanAnimalsScreen(
    onBackPressed: () -> Unit,
    onNonHumanAnimalClick: (nonHumanAnimalId: String, caregiverId: String) -> Unit,
    onCreateNonHumanAnimal: () -> Unit
) {

    val checkAllNonHumanAnimalsViewmodel: CheckAllNonHumanAnimalsViewmodel =
        koinViewModel<CheckAllNonHumanAnimalsViewmodel>()
    val nonHumanAnimalList: List<NonHumanAnimal> by checkAllNonHumanAnimalsViewmodel.nonHumanAnimalListFlow.collectAsState(
        initial = emptyList()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            } else {
                LazyColumn {
                    items(nonHumanAnimalList) { nonHumanAnimal ->
                        RmListItem(
                            title = nonHumanAnimal.name,
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
            Spacer(modifier = Modifier.weight(1f))
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
