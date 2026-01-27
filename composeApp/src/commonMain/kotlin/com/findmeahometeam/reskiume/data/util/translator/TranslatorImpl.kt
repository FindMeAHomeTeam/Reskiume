package com.findmeahometeam.reskiume.data.util.translator

import com.findmeahometeam.reskiume.domain.repository.util.translator.Translator
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

/**
 * [A translator that uses Google's translate API.](https://github.com/therealbush/translator)
 *
 * @project https://github.com/therealbush/translator
 * @author bush, py-googletrans + contributors
 * @modified Find me a home team
 */
class TranslatorImpl(
    private val client: HttpClient
): Translator {

    init {
        client.config {
            install(UserAgent) {
                agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
            }
            HttpResponseValidator {
                // This is run for all responses
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        throw TranslationException("Error caught from HTTP request: ${response.status}")
                    }
                }
                // This is run only when an exception is thrown, including our custom ones
                handleResponseExceptionWithRequest { cause, _ ->
                    if (cause !is TranslationException) {
                        throw TranslationException("Exception caught from HTTP request", cause)
                    }
                }
            }
        }
    }

    /**
     * Translates the given string to the desired language.
     * ```
     * translate("Text to be translated", Language.RUSSIAN)
     * translate("Text to be translated", ALBANIAN, ENGLISH)
     * ```
     * @param textToTranslate   The text to be translated.
     * @param sourceLanguage The language of [textToTranslate]. By default, this is [Language.AUTO].
     * @param targetLanguage The language to translate [textToTranslate] to. By default, this is [Language.ENGLISH].
     *
     * @return A [Translation] containing the translated text and other related data.
     * @throws TranslationException If the HTTP request could not be completed.
     *
     * @see Translation
     */
    override suspend fun translate(
        textToTranslate: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): Translation {
        require(targetLanguage != Language.AUTO) {
            "The target language cannot be Language.AUTO!"
        }
        val response: HttpResponse =
            client.get("https://translate.googleapis.com/translate_a/single") {
                constantParameters()
                parameter("sl", sourceLanguage.code)
                parameter("tl", targetLanguage.code)
                parameter("hl", targetLanguage.code)
                parameter("q", textToTranslate)
            }
        return Translation(
            sourceText = textToTranslate,
            targetLanguage = targetLanguage,
            rawData = response.bodyAsText()
        )
    }
    private fun HttpRequestBuilder.constantParameters() {
        parameter("client", "gtx")

        // dt params: check out https://github.com/ssut/py-googletrans
        arrayOf("at", "bd", "ex", "ld", "md", "qca", "rw", "rm", "ss", "t").forEach { parameter("dt", it) }
        parameter("ie", "UTF-8")
        parameter("oe", "UTF-8")
        parameter("otf", 1)
        parameter("ssel", 0)
        parameter("tsel", 0)
        parameter("tk", "bushissocool")
    }

    /**
     * Indicates an exception/error relating to the translation's HTTP request.
     */
    class TranslationException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
