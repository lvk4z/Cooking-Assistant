package com.example.cookingassistant.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.cookingassistant.R
import com.example.cookingassistant.voice.SpeechManager

@Composable
fun SpeechTranscription() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val available = remember(context) { SpeechRecognizer.isRecognitionAvailable(context) }
    var text by rememberSaveable { mutableStateOf("") }
    var partialText by remember { mutableStateOf("") }
    var permissionDenied by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<Int?>(null) }
    val manager = remember(context) {
        SpeechManager(
            context,
            onPartialText = { partialText = it },
            onFinalText = { result ->
                text = listOf(text, result.trim()).filter { it.isNotBlank() }.joinToString(" ")
                partialText = ""
            },
            onError = { error = it }
        )
    }

    fun startIfAllowed() {
        if (available && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionDenied = false
            error = null
            manager.start()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionDenied = !granted
        if (granted) startIfAllowed()
    }

    LaunchedEffect(available) {
        if (available) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startIfAllowed()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    DisposableEffect(lifecycleOwner, manager) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startIfAllowed()
                Lifecycle.Event.ON_STOP -> manager.stop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            manager.stop()
        }
    }

    val displayedText = listOf(text, partialText).filter { it.isNotBlank() }.joinToString(" ")
    Text(text = stringResource(R.string.speech_result, displayedText))
    if (!available) {
        Text(text = stringResource(R.string.speech_unavailable))
    } else if (permissionDenied) {
        Text(text = stringResource(R.string.speech_permission_denied))
    } else if (error != null) {
        Text(text = stringResource(R.string.speech_error, error!!))
    }
    if (available && (permissionDenied || error != null)) {
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startIfAllowed()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) {
            Text(text = stringResource(R.string.speech_retry))
        }
    }
}