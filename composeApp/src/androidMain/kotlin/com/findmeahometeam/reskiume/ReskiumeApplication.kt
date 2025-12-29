package com.findmeahometeam.reskiume

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.database

class ReskiumeApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)
    }
}
