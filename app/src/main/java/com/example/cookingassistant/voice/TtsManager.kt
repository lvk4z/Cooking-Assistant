package com.example.cookingassistant.voice

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private var isInitializing = true
    private var isReady = false
    private var isShutdown = false
    private var pendingText: String? = null

    private val tts = TextToSpeech(context.applicationContext) { status ->
        handler.post {
            if (!isShutdown) {
                initialize(status)
            }
        }
    }

    private fun initialize(status: Int) {
        isInitializing = false
        if (status != TextToSpeech.SUCCESS) {
            pendingText = null
            Log.e("TtsManager", "TTS initialization failed")
            return
        }

        val languageStatus = tts.setLanguage(Locale.forLanguageTag("pl-PL"))
        if (languageStatus < TextToSpeech.LANG_AVAILABLE) {
            pendingText = null
            Log.w("TtsManager", "Polish voice unavailable: $languageStatus")
            return
        }

        isReady = true
        Log.d("TtsManager", "TTS ready")
        val text = pendingText
        pendingText = null
        if (text != null) {
            speak(text)
        }
    }

    fun speak(text: String) {
        if (isShutdown) {
            Log.w("TtsManager", "TTS already shut down")
            return
        }
        if (isInitializing) {
            if (pendingText == null) {
                Log.d("TtsManager", "TTS not ready; waiting")
            }
            pendingText = text
            return
        }
        if (!isReady) {
            Log.w("TtsManager", "TTS unavailable")
            return
        }

        val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "cooking_step")
        if (result == TextToSpeech.ERROR) {
            Log.e("TtsManager", "TTS speak failed")
        }
    }

    fun stop() {
        pendingText = null
        if (isReady && !isShutdown && tts.stop() == TextToSpeech.ERROR) {
            Log.w("TtsManager", "TTS stop failed")
        }
    }

    fun shutdown() {
        if (isShutdown) {
            return
        }
        stop()
        isShutdown = true
        isReady = false
        isInitializing = false
        handler.removeCallbacksAndMessages(null)
        tts.shutdown()
    }
}
