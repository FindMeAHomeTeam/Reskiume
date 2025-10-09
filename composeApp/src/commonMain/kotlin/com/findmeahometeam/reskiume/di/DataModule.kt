package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.database.LocalRepositoryImpl
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::LocalRepositoryImpl) bind LocalRepository::class
}
