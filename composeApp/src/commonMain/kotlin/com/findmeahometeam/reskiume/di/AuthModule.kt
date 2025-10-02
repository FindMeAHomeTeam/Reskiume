package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthState
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmail
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authModule = module {
    factoryOf(::ObserveAuthState)
    factoryOf(::SignInWithEmail)
}