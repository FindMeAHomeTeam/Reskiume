package com.findmeahometeam.reskiume.domain.repository.util.log

interface CrashlyticsForIos {
    fun log(message: String)
    fun logError(message: String)
}
