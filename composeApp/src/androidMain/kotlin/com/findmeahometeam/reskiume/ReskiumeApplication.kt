package com.findmeahometeam.reskiume

import android.app.Application
import com.findmeahometeam.reskiume.di.dataModule
import com.findmeahometeam.reskiume.di.domainModule
import com.findmeahometeam.reskiume.di.platformModule
import com.findmeahometeam.reskiume.di.uiModule
import com.google.firebase.Firebase
import com.google.firebase.database.database
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ReskiumeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)
        startKoin {
            androidLogger()
            androidContext(this@ReskiumeApplication)
            modules(platformModule, domainModule, dataModule, uiModule)
        }
    }
}
