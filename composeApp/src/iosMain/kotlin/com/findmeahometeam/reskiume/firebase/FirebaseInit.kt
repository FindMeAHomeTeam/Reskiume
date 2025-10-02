package com.findmeahometeam.reskiume.firebase

import cocoapods.FirebaseCore.FIRApp
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun initFirebase() {
    FIRApp.configure()
}