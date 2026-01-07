package com.findmeahometeam.reskiume.ui.util

import org.jetbrains.compose.resources.StringResource

interface StringProvider {
    suspend fun getStringResource(resource: StringResource): String
}
