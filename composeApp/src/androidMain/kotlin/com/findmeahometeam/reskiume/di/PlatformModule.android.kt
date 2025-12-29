package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.database.ReskiumeDatabase
import com.findmeahometeam.reskiume.data.database.getDatabase
import com.findmeahometeam.reskiume.data.remote.auth.AuthRepositoryAndroidImpl
import com.findmeahometeam.reskiume.data.remote.database.nonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepositoryAndroidImpl
import com.findmeahometeam.reskiume.data.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepositoryAndroidImpl
import com.findmeahometeam.reskiume.data.remote.storage.StorageRepositoryAndroidImpl
import com.findmeahometeam.reskiume.data.util.analytics.Analytics
import com.findmeahometeam.reskiume.data.util.analytics.AnalyticsAndroidImpl
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.log.LogAndroidImpl
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::LogAndroidImpl) bind Log::class
    singleOf(::AnalyticsAndroidImpl) bind Analytics::class
    singleOf(::AuthRepositoryAndroidImpl) bind AuthRepository::class
    single<ReskiumeDatabase> { getDatabase(get()) }
    single<DatabaseReference>{ Firebase.database.reference }
    singleOf(::RealtimeDatabaseRemoteUserRepositoryAndroidImpl) bind RealtimeDatabaseRemoteUserRepository::class
    singleOf(::RealtimeDatabaseRemoteReviewRepositoryAndroidImpl) bind RealtimeDatabaseRemoteReviewRepository::class
    singleOf(::StorageRepositoryAndroidImpl) bind StorageRepository::class
    singleOf(::RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl) bind RealtimeDatabaseRemoteNonHumanAnimalRepository::class
}
