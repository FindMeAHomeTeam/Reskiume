package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsViewmodel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::HomeViewmodel)
    viewModelOf(::ProfileViewmodel)
    viewModelOf(::CreateAccountViewmodel)
    viewModelOf(::LoginAccountViewmodel)
    viewModelOf(::ModifyAccountViewmodel)
    viewModelOf(::DeleteAccountViewmodel)
    viewModelOf(::CheckReviewsViewmodel)
}
