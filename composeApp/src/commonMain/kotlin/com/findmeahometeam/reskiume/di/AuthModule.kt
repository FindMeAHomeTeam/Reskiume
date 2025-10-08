package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthState
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPassword
import com.findmeahometeam.reskiume.domain.usecases.DeleteUser
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPassword
import com.findmeahometeam.reskiume.domain.usecases.SignOut
import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.deleteUser.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.login.LoginViewmodel
import com.findmeahometeam.reskiume.ui.profile.userScreen.PersonalInformationViewmodel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    factoryOf(::ObserveAuthState)
    factoryOf(::CreateUserWithEmailAndPassword)
    factoryOf(::SignInWithEmailAndPassword)
    factoryOf(::SignOut)
    factoryOf(::DeleteUser)
    viewModelOf(::HomeViewmodel)
    viewModelOf(::ProfileViewmodel)
    viewModelOf(::CreateAccountViewmodel)
    viewModelOf(::LoginViewmodel)
    viewModelOf(::PersonalInformationViewmodel)
    viewModelOf(::DeleteAccountViewmodel)
}
