package com.findmeahometeam.reskiume.ui.util

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class StringProviderImpl: StringProvider {
    override suspend fun getStringResource(resource: StringResource): String {
        return getString(resource)
    }
}
