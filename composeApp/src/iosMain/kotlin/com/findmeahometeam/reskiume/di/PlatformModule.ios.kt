package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.remote.auth.AuthRepositoryIosImpl
import com.findmeahometeam.reskiume.data.database.ReskiumeDatabase
import com.findmeahometeam.reskiume.data.database.getDatabase
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepositoryForIosDelegateWrapper
import com.findmeahometeam.reskiume.data.remote.auth.AuthRepositoryForIosDelegateWrapperImpl
import com.findmeahometeam.reskiume.data.remote.auth.AuthUserRepositoryForIosDelegateImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteReview.RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegateImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapperImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteUser.RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegateImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapperImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryIosImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryIosImpl
import com.findmeahometeam.reskiume.data.remote.storage.StorageRepositoryForIosDelegateWrapperImpl
import com.findmeahometeam.reskiume.data.remote.storage.StorageRepositoryIosImpl
import com.findmeahometeam.reskiume.data.util.analytics.Analytics
import com.findmeahometeam.reskiume.data.util.analytics.AnalyticsForIosDelegateWrapperImpl
import com.findmeahometeam.reskiume.data.util.analytics.AnalyticsIosImpl
import com.findmeahometeam.reskiume.data.util.log.CrashlyticsForIosDelegateWrapperImpl
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.log.LogIosImpl
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepositoryForIosDelegateWrapper
import com.findmeahometeam.reskiume.domain.repository.util.analytics.AnalyticsForIosWrapper
import com.findmeahometeam.reskiume.domain.repository.util.log.CrashlyticsForIosWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::LogIosImpl) bind Log::class
    singleOf(::AnalyticsIosImpl) bind Analytics::class
    singleOf(::AuthRepositoryIosImpl) bind AuthRepository::class
    singleOf(::AuthUserRepositoryForIosDelegateImpl) bind AuthUserRepositoryForIosDelegate::class
    singleOf(::AuthRepositoryForIosDelegateWrapperImpl) bind AuthRepositoryForIosDelegateWrapper::class
    single<ReskiumeDatabase> { getDatabase() }
    singleOf(::RealtimeDatabaseRemoteUserRepositoryIosImpl) bind RealtimeDatabaseRemoteUserRepository::class
    singleOf(::RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegateImpl) bind RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate::class
    singleOf(::RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapperImpl) bind RealtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper::class
    singleOf(::RealtimeDatabaseRemoteReviewRepositoryIosImpl) bind RealtimeDatabaseRemoteReviewRepository::class
    singleOf(::RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegateImpl) bind RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate::class
    singleOf(::RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapperImpl) bind RealtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper::class
    singleOf(::StorageRepositoryForIosDelegateWrapperImpl) bind StorageRepositoryForIosDelegateWrapper::class
    singleOf(::StorageRepositoryIosImpl) bind StorageRepository::class
    singleOf(::CrashlyticsForIosDelegateWrapperImpl) bind CrashlyticsForIosWrapper::class
    singleOf(::AnalyticsForIosDelegateWrapperImpl) bind AnalyticsForIosWrapper::class
}
