package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPassword
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromLocalSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthState
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPassword
import com.findmeahometeam.reskiume.domain.usecases.SignOut
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::ObserveAuthState)
    factoryOf(::CreateUserWithEmailAndPassword)
    factoryOf(::SignInWithEmailAndPassword)
    factoryOf(::SignOut)
    factoryOf(::DeleteUserFromRemoteSource)
    factoryOf(::InsertUserToLocalSource)
    factoryOf(::GetUserFromLocalSource)
    factoryOf(::ModifyUserFromLocalSource)
    factoryOf(::DeleteUserFromLocalSource)
}