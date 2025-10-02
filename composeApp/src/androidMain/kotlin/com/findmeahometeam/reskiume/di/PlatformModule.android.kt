package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.data.auth.AuthRepositoryAndroidImpl
import com.findmeahometeam.reskiume.domain.repository.AuthRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::AuthRepositoryAndroidImpl) bind AuthRepository::class
}