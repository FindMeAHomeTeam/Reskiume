package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.authUser.CreateUserWithEmailAndPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
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

    factoryOf(::ObserveAuthStateInAuthDataSource)
    factoryOf(::CreateUserWithEmailAndPasswordInAuthDataSource)
    factoryOf(::SignInWithEmailAndPasswordFromAuthDataSource)
    factoryOf(::SignOutFromAuthDataSource)
    factoryOf(::DeleteUserFromAuthDataSource)
    factoryOf(::InsertUserInLocalDataSource)
    factoryOf(::InsertUserInRemoteDataSource)
    factoryOf(::GetUserFromLocalDataSource)
    factoryOf(::GetUserFromRemoteDataSource)
    factoryOf(::ModifyUserInLocalDataSource)
    factoryOf(::ModifyUserInRemoteDataSource)
    factoryOf(::DeleteUsersFromLocalDataSource)
    factoryOf(::DeleteUserFromRemoteDataSource)
    factoryOf(::UploadImageToRemoteDataSource)
    factoryOf(::DeleteImageFromRemoteDataSource)
    factoryOf(::DeleteImageFromLocalDataSource)
    factoryOf(::DownloadImageToLocalDataSource)
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
