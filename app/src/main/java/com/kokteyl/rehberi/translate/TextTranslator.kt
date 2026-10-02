package com.kokteyl.rehberi.translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Cihaz üzerinde İngilizce -> Türkçe çeviri (Google ML Kit).
 * İlk kullanımda yaklaşık 30 MB'lık çeviri modeli indirilir; sonrasında internetsiz çalışır.
 */
class TextTranslator {

    private val translator: Translator by lazy {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.TURKISH)
                .build()
        )
    }

    suspend fun translate(text: String, wifiOnly: Boolean = false): Result<String> =
        try {
            val conditions = DownloadConditions.Builder().apply { if (wifiOnly) requireWifi() }.build()
            translator.downloadModelIfNeeded(conditions).await()
            Result.success(translator.translate(text).await())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
