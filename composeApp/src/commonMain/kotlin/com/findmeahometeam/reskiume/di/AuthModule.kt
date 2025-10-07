package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthState
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPassword
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPassword
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.login.LoginViewmodel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    factoryOf(::ObserveAuthState)
    factoryOf(::CreateUserWithEmailAndPassword)
    factoryOf(::SignInWithEmailAndPassword)
    viewModelOf(::CreateAccountViewmodel)
    viewModelOf(::LoginViewmodel)
}