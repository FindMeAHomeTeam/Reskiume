package com.findmeahometeam.reskiume.domain.usecases.util.translator

import com.findmeahometeam.reskiume.data.util.translator.Language
import com.findmeahometeam.reskiume.data.util.translator.Translation
import com.findmeahometeam.reskiume.domain.repository.util.translator.Translator

class TranslateMessage(private val translator: Translator) {
    suspend operator fun invoke(
        textToTranslate: String,
        sourceLanguage: Language = Language.AUTO,
        targetLanguage: Language = Language.ENGLISH
    ): String {
        val translation: Translation = translator.translate(textToTranslate, sourceLanguage, targetLanguage)
        return translation.translatedText
    }
}