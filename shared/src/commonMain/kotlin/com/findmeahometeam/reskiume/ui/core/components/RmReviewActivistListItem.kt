package com.findmeahometeam.reskiume.ui.core.components

import RmRatingBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import org.jetbrains.compose.resources.stringResource
import reskiume.shared.generated.resources.Res
import reskiume.shared.generated.resources.create_review_screen_activist_avatar_content_description
import reskiume.shared.generated.resources.review_activist_list_item_leave_review

@Composable
fun RmReviewActivistListItem(
    modifier: Modifier = Modifier,
    activistToReview: User,
    onReview: (activistToReviewId: String, rating: Float, description: String) -> Unit
) {
    var rating by rememberSaveable { mutableStateOf(0f) }
    var description by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
                .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp))
                .padding(16.dp)
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RmDisplayAvatarOrPlaceholder(
                avatar = activistToReview.image,
                avatarSize = 48.dp,
                contentDescription = stringResource(Res.string.create_review_screen_activist_avatar_content_description),
            )
            Spacer(modifier = Modifier.width(8.dp))
            RmTextBold(
                text = activistToReview.username,
                textToBold = activistToReview.username,
                fontSize = 16.sp,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RmText(
                text = rating.toString(),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .background(primaryGreen)
                    .padding(3.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            RmRatingBar(
                rating = rating,
                onRatingChanged = {
                    rating = it
                    if (description.isNotEmpty()) {
                        onReview(activistToReview.uid, rating, description)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        RmTextField(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            text = description,
            label = stringResource(
                Res.string.review_activist_list_item_leave_review,
                activistToReview.username
            ),
            onValueChange = {
                description = it
                onReview(activistToReview.uid, rating, description)
            }
        )
    }
}
