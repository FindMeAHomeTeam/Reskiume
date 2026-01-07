package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.ui.util.StringProvider
import org.jetbrains.compose.resources.StringResource

class FakeStringProvider(
    private val text: String = ""
): StringProvider {
    override suspend fun getStringResource(resource: StringResource): String = text
}
