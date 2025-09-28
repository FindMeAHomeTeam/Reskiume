package com.findmeahometeam.reskiume

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform