package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.deleteUser.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.login.LoginViewmodel
import com.findmeahometeam.reskiume.ui.profile.personalInformation.PersonalInformationViewmodel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::HomeViewmodel)
    viewModelOf(::ProfileViewmodel)
    viewModelOf(::CreateAccountViewmodel)
    viewModelOf(::LoginViewmodel)
    viewModelOf(::PersonalInformationViewmodel)
    viewModelOf(::DeleteAccountViewmodel)
}