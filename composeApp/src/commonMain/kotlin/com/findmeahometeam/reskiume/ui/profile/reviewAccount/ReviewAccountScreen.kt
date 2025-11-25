package com.findmeahometeam.reskiume.ui.profile.reviewAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
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
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListReviewItem
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.reskiume
import reskiume.composeapp.generated.resources.review_account_screen_my_reviews_title
import reskiume.composeapp.generated.resources.review_account_screen_no_reviews
import reskiume.composeapp.generated.resources.review_account_screen_user_deleted

@Composable
fun ReviewAccountScreen(onBackPressed: () -> Unit) {

    val reviewAccountViewmodel: ReviewAccountViewmodel = koinViewModel<ReviewAccountViewmodel>()
    val uiReviewList by reviewAccountViewmodel.reviewListFlow.collectAsState(initial = emptyList())

    RmScaffold(
        title = stringResource(Res.string.review_account_screen_my_reviews_title),
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiReviewList.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier.size(190.dp),
                    painter = painterResource(Res.drawable.reskiume),
                    contentDescription = null,
                    tint = primaryGreen
                )
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.review_account_screen_no_reviews),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn {
                    items(uiReviewList) { uiReview ->
                        RmListReviewItem(
                            title = uiReview.authorName.ifBlank { stringResource(Res.string.review_account_screen_user_deleted) },
                            description = uiReview.description,
                            listAvatarType = RmListAvatarType.Image(resource = uiReview.authorUri),
                            rating = uiReview.rating,
                            date = uiReview.date
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
