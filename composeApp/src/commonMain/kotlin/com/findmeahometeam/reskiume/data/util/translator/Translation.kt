package com.findmeahometeam.reskiume.data.util.translator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

/**
 * A class containing the results of a translation request.
 *
 * @project https://github.com/therealbush/translator
 * @author bush, twistios
 * @modified Find me a home team
 */
data class Translation(

    /**
     * The original, untranslated text.
     */
    val sourceText: String,

    /**
     * The language translated to.
     */
    val targetLanguage: Language,

    /**
     * The raw data received from Google's API.
     */
    val rawData: String
) {
    /**
     * The data received from Google's API, as a [JsonArray].
     */
    private val jsonData: JsonArray = Json.parseToJsonElement(rawData).jsonArray

    /**
     * The language of the translated text. This is useful
     * if the source language was set to [Language.AUTO].
     */
    val sourceLanguage: Language = Language(language = jsonData[2].convertToString!!)

    /**
     * The result of the translation.
     */
    val translatedText = buildString {
        // For some reason every sentence/line is separated, so we need to join them back.
        jsonData[0].jsonArray.mapNotNull { it.jsonArray[0].convertToString }.forEach(::append)
    }

    override fun toString() =
        "Translation($sourceLanguage -> $targetLanguage, $sourceText -> $translatedText)"

    private val JsonElement.convertToString: String?
        get() = toString().removeSurrounding("\"").replace("\\n", "\n").takeIf { it != "null" }
}
