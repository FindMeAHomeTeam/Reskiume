package com.findmeahometeam.reskiume.domain.repository.util.translator

import com.findmeahometeam.reskiume.data.util.translator.Language

interface Translator {

    suspend fun translate(
        textToTranslate: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): String
}
