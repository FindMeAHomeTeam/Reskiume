package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.SaveImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInRemoteRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    // localCache
    factoryOf(::InsertCacheInLocalRepository)
    factoryOf(::ModifyCacheInLocalRepository)
    factoryOf(::GetDataByManagingObjectLocalCacheTimestamp)
    factoryOf(::DeleteCacheFromLocalRepository)

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
    factoryOf(::DeleteUsersFromLocalDataSource)
    factoryOf(::DeleteUserFromRemoteDataSource)
    factoryOf(::UploadImageToRemoteDataSource)
    factoryOf(::DeleteImageFromRemoteDataSource)
    factoryOf(::DeleteImageInLocalDataSource)
    factoryOf(::SaveImageToLocalDataSource)
    factoryOf(::ModifyUserEmailInAuthDataSource)
    factoryOf(::ModifyUserPasswordInAuthDataSource)

    // review
    factoryOf(::InsertReviewInLocalRepository)
    factoryOf(::DeleteReviewsFromLocalRepository)
    factoryOf(::GetReviewsFromLocalRepository)
    factoryOf(::InsertReviewInRemoteRepository)
    factoryOf(::DeleteReviewsFromRemoteRepository)
    factoryOf(::GetReviewsFromRemoteRepository)
}
