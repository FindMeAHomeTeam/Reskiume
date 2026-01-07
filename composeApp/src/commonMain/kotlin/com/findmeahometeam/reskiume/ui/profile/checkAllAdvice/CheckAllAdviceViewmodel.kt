package com.findmeahometeam.reskiume.ui.profile.checkAllAdvice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.model.Advice
import com.findmeahometeam.reskiume.domain.model.AdviceImage
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.UiState.Success
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.advice_care_bloat_torsion_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_care_bloat_torsion_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_care_feed_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_care_feed_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rehome_find_a_home_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rehome_find_a_home_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rehome_visit_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rehome_visit_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rescue_found_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rescue_found_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rescue_pick_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rescue_pick_non_human_animal_title
import reskiume.composeapp.generated.resources.advice_rescue_rejected_non_human_animal_description
import reskiume.composeapp.generated.resources.advice_rescue_rejected_non_human_animal_title
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_all
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_care
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_rehome
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_rescue

class CheckAllAdviceViewmodel(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val checkActivistUtil: CheckActivistUtil
) : ViewModel() {

    private var selectedAdviceType: AdviceType = AdviceType.ALL

    private var myUid: String? = null

    private val rescueAdviceList = listOf(
        Advice(
            title = Res.string.advice_rescue_found_non_human_animal_title,
            description = Res.string.advice_rescue_found_non_human_animal_description,
            image = AdviceImage.RESCUE
        ),
        Advice(
            title = Res.string.advice_rescue_pick_non_human_animal_title,
            description = Res.string.advice_rescue_pick_non_human_animal_description,
            image = AdviceImage.RESCUE
        ),
        Advice(
            title = Res.string.advice_rescue_rejected_non_human_animal_title,
            description = Res.string.advice_rescue_rejected_non_human_animal_description,
            image = AdviceImage.RESCUE
        )
    )

    private val rehomeAdviceList = listOf(
        Advice(
            title = Res.string.advice_rehome_find_a_home_non_human_animal_title,
            description = Res.string.advice_rehome_find_a_home_non_human_animal_description,
            image = AdviceImage.REHOME
        ),
        Advice(
            title = Res.string.advice_rehome_visit_non_human_animal_title,
            description = Res.string.advice_rehome_visit_non_human_animal_description,
            image = AdviceImage.REHOME
        )
    )

    private val careAdviceList = listOf(
        Advice(
            title = Res.string.advice_care_feed_non_human_animal_title,
            description = Res.string.advice_care_feed_non_human_animal_description,
            image = AdviceImage.CARE
        ),
        Advice(
            title = Res.string.advice_care_bloat_torsion_non_human_animal_title,
            description = Res.string.advice_care_bloat_torsion_non_human_animal_description,
            image = AdviceImage.CARE
        )
    )

    private val _adviceListState: MutableStateFlow<UiState<List<Advice>>> =
        MutableStateFlow(Success(getAdviceList(AdviceType.ALL)))

    val adviceListState: StateFlow<UiState<List<Advice>>> = _adviceListState.asStateFlow()

    private fun getAdviceList(adviceType: AdviceType): List<Advice> {
        return when (adviceType) {

            AdviceType.RESCUE -> {
                rescueAdviceList
            }

            AdviceType.REHOME -> {
                rehomeAdviceList
            }

            AdviceType.CARE -> {
                careAdviceList
            }

            AdviceType.ALL -> {
                rescueAdviceList + rehomeAdviceList + careAdviceList
            }
        }
    }

    fun updateAdviceList(adviceType: AdviceType) {

        selectedAdviceType = adviceType

        viewModelScope.launch {

            _adviceListState.emit(Success(getAdviceList(adviceType)))
        }
    }

    fun searchAdvice(query: String) {

        viewModelScope.launch {
            _adviceListState.emit(
                Success(
                    data = getAdviceList(selectedAdviceType).filter {
                        getString(it.title).contains(query, ignoreCase = true)
                                || getString(it.description).contains(query, ignoreCase = true)
                    }
                )
            )
        }
    }

    fun checkAuthState(onLoggedIn: (Boolean) -> Unit) {
        viewModelScope.launch {

            myUid = observeAuthStateInAuthDataSource().firstOrNull()?.uid
            onLoggedIn(myUid != null)
        }
    }

    fun retrieveAdviceAuthor(userId: String?, onAuthorFetched: (User?) -> Unit) {

        if (userId == null) {
            onAuthorFetched(null)
            return
        }
        viewModelScope.launch {
            val author =
                checkActivistUtil.getUser(
                    activistUid = userId,
                    myUserUid = myUid ?: "",
                    coroutineScope = viewModelScope
                )
            onAuthorFetched(author)
        }
    }
}

enum class AdviceType(val stringResource: StringResource) {
    ALL(Res.string.check_all_advice_screen_option_all),
    RESCUE(Res.string.check_all_advice_screen_option_rescue),
    REHOME(Res.string.check_all_advice_screen_option_rehome),
    CARE(Res.string.check_all_advice_screen_option_care)
}
