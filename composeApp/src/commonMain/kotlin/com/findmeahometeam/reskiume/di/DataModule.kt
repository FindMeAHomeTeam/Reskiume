package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.database.LocalCacheRepositoryImpl
import com.findmeahometeam.reskiume.data.database.LocalReviewRepositoryImpl
import com.findmeahometeam.reskiume.data.database.LocalUserRepositoryImpl
import com.findmeahometeam.reskiume.data.database.NonHumanAnimalRepositoryImpl
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.local.NonHumanAnimalRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::LocalCacheRepositoryImpl) bind LocalCacheRepository::class
    singleOf(::LocalUserRepositoryImpl) bind LocalUserRepository::class
    singleOf(::LocalReviewRepositoryImpl) bind LocalReviewRepository::class
    singleOf(::NonHumanAnimalRepositoryImpl) bind NonHumanAnimalRepository::class
}
