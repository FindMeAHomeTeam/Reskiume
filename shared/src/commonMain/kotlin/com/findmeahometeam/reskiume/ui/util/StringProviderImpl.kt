package com.findmeahometeam.reskiume.ui.util

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class StringProviderImpl : StringProvider {

    override suspend fun getStringResource(
        resource: StringResource,
        vararg formatArgs: Any
    ): String {
        return if (formatArgs.isEmpty()) {
            getString(resource)
        } else {
            getString(resource, *formatArgs)
        }
    }
}
