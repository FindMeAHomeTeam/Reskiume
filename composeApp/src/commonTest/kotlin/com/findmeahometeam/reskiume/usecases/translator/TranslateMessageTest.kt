package com.findmeahometeam.reskiume.usecases.translator

import com.findmeahometeam.reskiume.data.util.translator.Language
import com.findmeahometeam.reskiume.domain.repository.util.translator.Translator
import com.findmeahometeam.reskiume.domain.usecases.util.translator.TranslateMessage
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TranslateMessageTest {

    private val translator: Translator = mock {
        everySuspend {
            translate(
                textToTranslate = "Hola",
                sourceLanguage = Language.AUTO,
                targetLanguage = Language.ENGLISH
            )
        } returns "Hello"
    }

    private val translateMessage = TranslateMessage(translator)

    @Test
    fun `given a message_when the app translates it_then translate is called`() =
        runTest {
            translateMessage("Hola")
            verifySuspend {
                translator.translate(
                    textToTranslate = "Hola",
                    sourceLanguage = Language.AUTO,
                    targetLanguage = Language.ENGLISH
                )
            }
        }
}
