package com.findmeahometeam.reskiume.ui.profile.createReview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmReviewActivistListItem
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.shared.generated.resources.Res
import reskiume.shared.generated.resources.create_review_screen_description
import reskiume.shared.generated.resources.create_review_screen_submit_reviews_button
import reskiume.shared.generated.resources.create_review_screen_title

@Composable
fun CreateReviewScreen(
    onBackPressed: () -> Unit,
    onFinished: () -> Unit
) {
    val createReviewViewmodel: CreateReviewViewmodel =
        koinViewModel<CreateReviewViewmodel>()

    val activistsToReviewState: UiState<List<User>> by createReviewViewmodel.activistsToReviewState.collectAsStateWithLifecycle()

    val submitReviewsState: UiState<Unit> by createReviewViewmodel.submitReviewsState.collectAsStateWithLifecycle()

    var hasMapOfReviews: HashMap<String, UiCreateReview> by rememberSaveable {
        mutableStateOf(
            hashMapOf()
        )
    }

    var activistsSize: Int by rememberSaveable { mutableStateOf(-1) }
    var activistsReviewedSize: Int by rememberSaveable { mutableStateOf(0) }

    val isAddReviewsButtonEnabled by remember(
        activistsSize,
        activistsReviewedSize
    ) {
        derivedStateOf {
            activistsSize == activistsReviewedSize
        }
    }

    RmScaffold(
        title = stringResource(Res.string.create_review_screen_title),
        onBackPressed = onBackPressed,
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmText(
                text = stringResource(Res.string.create_review_screen_description),
                fontSize = 20.sp
            )
            RmResultState(activistsToReviewState) { activistsToReview: List<User> ->

                activistsSize = activistsToReview.size

                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(
                        items = activistsToReview,
                        key = { it.hashCode() }
                    ) { activistToReview ->

                        RmReviewActivistListItem(
                            modifier = Modifier.animateItem(),
                            activistToReview = activistToReview,
                            onReview = { activistToReviewId: String, rating: Float, description: String ->

                                if (hasMapOfReviews[activistToReviewId] == null
                                    || hasMapOfReviews[activistToReviewId]!!.description.isEmpty()
                                    && description.isNotEmpty()
                                ) {
                                    activistsReviewedSize += 1

                                } else if (description.isEmpty()) {
                                    activistsReviewedSize -= 1
                                }
                                hasMapOfReviews[activistToReviewId] = UiCreateReview(
                                    reviewedUid = activistToReviewId,
                                    rating = rating,
                                    description = description
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(submitReviewsState !is UiState.Idle) {
                    Column {

                        RmResultState(submitReviewsState) {
                            onFinished()
                        }
                    }
                }
                RmButton(
                    text = stringResource(Res.string.create_review_screen_submit_reviews_button),
                    enabled = isAddReviewsButtonEnabled,
                    onClick = {
                        createReviewViewmodel.submitReviews(
                            hasMapOfReviews.values.toList()
                        )
                    }
                )
            }
        }
    }
}
