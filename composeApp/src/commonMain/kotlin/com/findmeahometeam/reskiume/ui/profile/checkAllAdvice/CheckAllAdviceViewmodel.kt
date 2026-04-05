package com.findmeahometeam.reskiume.ui.profile.checkAllAdvice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.Advice
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.UiState.Success
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_all
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_care
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_rehome
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_rescue

class CheckAllAdviceViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val checkActivistUtil: CheckActivistUtil,
    private val stringProvider: StringProvider
) : ViewModel() {

    private var selectedAdviceType: AdviceType = AdviceType.ALL

    private var myUid: String = ""

    private val _adviceListState: MutableStateFlow<UiState<List<Advice>>> =
        MutableStateFlow(Success(getAdviceList(AdviceType.ALL)))

    val adviceListState: StateFlow<UiState<List<Advice>>> = _adviceListState.asStateFlow()

    val authState: Flow<AuthUser?> = observeAuthStateInAuthDataSource().map { authUser ->
        myUid = authUser?.uid ?: " "
        authUser
    }

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

            _adviceListState.value = Success(getAdviceList(adviceType))
        }
    }

    fun searchAdvice(query: String) {

        viewModelScope.launch {
            _adviceListState.value =
                Success(
                    data = getAdviceList(selectedAdviceType).filter {
                        stringProvider.getStringResource(it.title).contains(query, ignoreCase = true)
                                || stringProvider.getStringResource(it.description).contains(query, ignoreCase = true)
                    }
                )
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
                    myUserUid = myUid
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
