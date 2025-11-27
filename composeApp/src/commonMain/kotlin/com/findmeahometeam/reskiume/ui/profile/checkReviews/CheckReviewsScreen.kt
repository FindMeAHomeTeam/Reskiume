package com.findmeahometeam.reskiume.ui.profile.checkReviews

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
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmHeader
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListReviewItem
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_reviews_screen_user_profile_title
import reskiume.composeapp.generated.resources.check_reviews_screen_my_reviews_title
import reskiume.composeapp.generated.resources.check_reviews_screen_no_reviews
import reskiume.composeapp.generated.resources.check_reviews_screen_user_deleted
import reskiume.composeapp.generated.resources.check_reviews_screen_user_reviews_title
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun CheckReviewsScreen(
    onBackPressed: () -> Unit,
    onReviewClick: (uid: String) -> Unit
) {

    val checkReviewsViewmodel: CheckReviewsViewmodel = koinViewModel<CheckReviewsViewmodel>()
    val uiReviewList: List<UiReview> by checkReviewsViewmodel.reviewListFlow.collectAsState(initial = emptyList())
    val user: User? by checkReviewsViewmodel.getUserDataIfNotMine().collectAsState(initial = null)

    RmScaffold(
        title = if (user == null) {
            stringResource(Res.string.check_reviews_screen_my_reviews_title)
        } else {
            stringResource(Res.string.check_reviews_screen_user_profile_title, user!!.username)
        },
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiReviewList.isEmpty()) {
                if (user != null) {
                    RmHeader(
                        user = user,
                        displayDescription = true
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (user == null || user?.image?.isNotBlank() == true) {
                    Icon(
                        modifier = Modifier.size(190.dp),
                        painter = painterResource(Res.drawable.reskiume),
                        contentDescription = null,
                        tint = primaryGreen
                    )
                }
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_reviews_screen_no_reviews),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn {
                    if (user != null) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RmHeader(
                                    user = user,
                                    displayDescription = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                RmText(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    text = stringResource(Res.string.check_reviews_screen_user_reviews_title),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    items(uiReviewList) { uiReview ->
                        RmListReviewItem(
                            title = uiReview.authorName.ifBlank { stringResource(Res.string.check_reviews_screen_user_deleted) },
                            description = uiReview.description,
                            listAvatarType = RmListAvatarType.Image(resource = uiReview.authorUri),
                            rating = uiReview.rating,
                            date = uiReview.date,
                            onClick = {
                                if (uiReview.authorUid.isNotBlank()) {
                                    onReviewClick(uiReview.authorUid)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
