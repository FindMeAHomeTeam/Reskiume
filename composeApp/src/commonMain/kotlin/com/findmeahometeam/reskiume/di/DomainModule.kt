package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::ObserveAuthStateFromAuthDataSource)
    factoryOf(::CreateUserWithEmailAndPasswordFromAuthDataSource)
    factoryOf(::SignInWithEmailAndPasswordFromAuthDataSource)
    factoryOf(::SignOutFromAuthDataSource)
    factoryOf(::DeleteUserFromAuthDataSource)
    factoryOf(::InsertUserToLocalDataSource)
    factoryOf(::InsertUserToRemoteDataSource)
    factoryOf(::GetUserFromLocalDataSource)
    factoryOf(::GetUserFromRemoteDataSource)
    factoryOf(::ModifyUserFromLocalDataSource)
    factoryOf(::ModifyUserFromRemoteDataSource)
    factoryOf(::DeleteUserFromLocalDataSource)
    factoryOf(::DeleteUserFromRemoteDataSource)
    factoryOf(::UploadImageToRemoteDataSource)
    factoryOf(::DeleteImageFromRemoteDataSource)
}
