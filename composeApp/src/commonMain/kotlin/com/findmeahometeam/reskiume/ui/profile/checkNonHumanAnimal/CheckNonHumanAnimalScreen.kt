package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmImage
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_non_human_animal_screen_non_human_animal_avatar_content_description
import reskiume.composeapp.generated.resources.check_non_human_animal_screen_non_human_animal_description
import reskiume.composeapp.generated.resources.check_non_human_animal_screen_non_human_animal_profile_title
import reskiume.composeapp.generated.resources.ic_female
import reskiume.composeapp.generated.resources.ic_male

@Composable
fun CheckNonHumanAnimalScreen(
    onBackPressed: () -> Unit,
) {
    val checkNonHumanAnimalViewmodel: CheckNonHumanAnimalViewmodel =
        koinViewModel<CheckNonHumanAnimalViewmodel>()

    val nonHumanAnimalState: UiState<NonHumanAnimal> by checkNonHumanAnimalViewmodel.nonHumanAnimalFlow.collectAsState(
        initial = UiState.Loading()
    )
    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(
            Res.string.check_non_human_animal_screen_non_human_animal_profile_title,
            if (nonHumanAnimalState is UiState.Success) (nonHumanAnimalState as UiState.Success<NonHumanAnimal>).data.name else ""
        ),
        onBackPressed = onBackPressed,
    ) { padding ->

        RmResultState(nonHumanAnimalState) { nonHumanAnimal: NonHumanAnimal ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    RmImage(
                        imagePath = nonHumanAnimal.imageUrl,
                        contentDescription =
                            stringResource(
                                Res.string.check_non_human_animal_screen_non_human_animal_avatar_content_description,
                                nonHumanAnimal.name
                            ),
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp))
                    )
                    Box(
                        modifier = Modifier.wrapContentSize()
                            .background(
                                color = when(nonHumanAnimal.adoptionState) {
                                    AdoptionState.LOOKING_FOR_ADOPTION -> tertiaryGreen
                                    AdoptionState.REHOMED -> secondaryGreen
                                    AdoptionState.ADOPTED -> primaryGreen
                                },
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(8.dp)
                    ) {
                        RmText(
                            text = stringResource(nonHumanAnimal.adoptionState.toStringResource()),
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row {

                    Column(modifier = Modifier.weight(1f)) {
                        RmText(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            text = nonHumanAnimal.name,
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(modifier = Modifier.padding(10.dp)) {
                            RmText(
                                text = nonHumanAnimal.nonHumanAnimalType.toEmoji()
                                        + " " + stringResource(nonHumanAnimal.nonHumanAnimalType.toStringResource())
                            )
                            RmText(text = " · ")
                            RmText(
                                text = nonHumanAnimal.ageCategory.toEmoji()
                                        + " " + stringResource(nonHumanAnimal.ageCategory.toStringResource())
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f).padding(top = 10.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Column(
                            modifier = Modifier.size(55.dp)
                                .background(
                                    color = primaryGreen,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(if (nonHumanAnimal.gender == Gender.FEMALE) Res.drawable.ic_female else Res.drawable.ic_male),
                                contentDescription = null,
                                tint = Color.White
                            )
                            RmText(
                                text = nonHumanAnimal.gender.toEmoji()
                                        + " " + stringResource(nonHumanAnimal.gender.toStringResource()),
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(modifier = Modifier.fillMaxWidth().alpha(0.1f), color = textColor)

                Spacer(modifier = Modifier.height(10.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_non_human_animal_screen_non_human_animal_description),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = nonHumanAnimal.description
                )
            }
        }
    }
}
