package com.findmeahometeam.reskiume.ui.profile.checkAdvice

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.toAdviceImage
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmImage
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAdvice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_advice_screen_author
import reskiume.composeapp.generated.resources.check_advice_screen_author_image_content_description
import reskiume.composeapp.generated.resources.check_advice_screen_title

@Composable
fun CheckAdviceScreen(
    checkAdvice: CheckAdvice,
    onAuthorClick: (uid: String) -> Unit,
    onBackPressed: () -> Unit
) {
    RmScaffold(
        title = stringResource(Res.string.check_advice_screen_title),
        onBackPressed = onBackPressed,
    ) { padding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val adviceImage = checkAdvice.image.toAdviceImage()
            Box(
                modifier = Modifier
                    .background(
                        color = adviceImage.backgroundColor,
                        shape = CircleShape
                    ).size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(adviceImage.icon),
                    contentDescription = null,
                    tint = adviceImage.iconColor
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            RmText(
                modifier = Modifier.fillMaxWidth(),
                text = checkAdvice.title,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (checkAdvice.authorName != null) {
                Spacer(modifier = Modifier.height(5.dp))

                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAuthorClick(checkAdvice.authorUid!!) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RmImage(
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            imagePath = checkAdvice.authorImage!!,
                            contentDescription = stringResource(Res.string.check_advice_screen_author_image_content_description),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        RmText(
                            text = stringResource(
                                Res.string.check_advice_screen_author,
                                checkAdvice.authorName
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            RmText(
                text = checkAdvice.description,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}
