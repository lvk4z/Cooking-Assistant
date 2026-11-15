package com.example.cookingassistant.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class SpeechManager(
    private val context: Context,
    private val onPartialText: (String) -> Unit,
    private val onFinalText: (String) -> Unit,
    private val onError: (Int) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var recognizer: SpeechRecognizer? = null
    private var active = false
    private var partialText = ""
    private var busyRetries = 0
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pl-PL")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    fun start() {
        if (active) return
        active = true
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer = speechRecognizer
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                if (active && recognizer === speechRecognizer) busyRetries = 0
            }
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onPartialResults(partialResults: Bundle?) {
                if (!active || recognizer !== speechRecognizer) return
                val text = getText(partialResults)
                if (text.isNotBlank()) {
                    partialText = text
                    onPartialText(text)
                }
            }

            override fun onResults(results: Bundle?) {
                if (!active || recognizer !== speechRecognizer) return
                finishText(getText(results))
                scheduleNext(0)
            }

            override fun onError(error: Int) {
                if (!active || recognizer !== speechRecognizer) return
                finishText("")
                if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT
                ) {
                    scheduleNext(0)
                } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY && busyRetries < 3) {
                    busyRetries++
                    Log.w("SpeechManager", "Recognizer busy; retry $busyRetries")
                    scheduleNext(1000)
                } else {
                    Log.w("SpeechManager", "Recognition error: $error")
                    stop()
                    this@SpeechManager.onError(error)
                }
            }
        })
        Log.d("SpeechManager", "Listening started")
        speechRecognizer.startListening(intent)
    }

    private fun getText(results: Bundle?): String {
        return results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull().orEmpty()
    }

    private fun finishText(text: String) {
        val finalText = text.ifBlank { partialText }
        partialText = ""
        if (finalText.isNotBlank()) onFinalText(finalText)
    }

    private fun scheduleNext(delayMillis: Long) {
        handler.postDelayed({
            if (active) recognizer?.startListening(intent)
        }, delayMillis)
    }

    fun stop() {
        active = false
        handler.removeCallbacksAndMessages(null)
        recognizer?.destroy()
        recognizer = null
        finishText("")
        busyRetries = 0
    }
}