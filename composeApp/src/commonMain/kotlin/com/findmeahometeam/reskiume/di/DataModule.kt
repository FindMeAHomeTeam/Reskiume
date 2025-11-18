package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.database.LocalUserRepositoryImpl
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::LocalUserRepositoryImpl) bind LocalUserRepository::class
}
