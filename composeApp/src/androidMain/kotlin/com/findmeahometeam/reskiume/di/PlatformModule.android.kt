package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.remote.auth.AuthRepositoryAndroidImpl
import com.findmeahometeam.reskiume.data.database.ReskiumeDatabase
import com.findmeahometeam.reskiume.data.database.getDatabase
import com.findmeahometeam.reskiume.data.remote.database.RealtimeDatabaseRepositoryAndroidImpl
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::AuthRepositoryAndroidImpl) bind AuthRepository::class
    single<ReskiumeDatabase> { getDatabase(get()) }
    singleOf(::RealtimeDatabaseRepositoryAndroidImpl) bind RealtimeDatabaseRepository::class
}
