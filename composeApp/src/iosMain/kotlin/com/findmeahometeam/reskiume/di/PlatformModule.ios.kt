package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.remote.auth.AuthRepositoryIosImpl
import com.findmeahometeam.reskiume.data.database.ReskiumeDatabase
import com.findmeahometeam.reskiume.data.database.getDatabase
import com.findmeahometeam.reskiume.data.remote.database.RealtimeDatabaseRepositoryIosImpl
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthUserRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.RealtimeDatabaseRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::AuthRepositoryIosImpl) bind AuthRepository::class
    single<ReskiumeDatabase> { getDatabase() }
    singleOf(::RealtimeDatabaseRepositoryIosImpl) bind RealtimeDatabaseRepository::class
}
