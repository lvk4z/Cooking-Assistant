package com.example.cookingassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cookingassistant.R
import com.example.cookingassistant.ui.CookingUiState
import com.example.cookingassistant.voice.TtsManager
import java.text.NumberFormat

@Composable
fun CookingScreen(
    state: CookingUiState,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = state.recipe.steps[state.currentStepIndex]
    val context = LocalContext.current
    var ttsManager by remember(context, state.recipe.id) {
        mutableStateOf<TtsManager?>(null)
    }

    DisposableEffect(context, state.recipe.id) {
        val manager = TtsManager(context)
        ttsManager = manager
        onDispose {
            manager.shutdown()
            ttsManager = null
        }
    }

    LaunchedEffect(ttsManager, state.recipe.id, state.currentStepIndex, step.text) {
        ttsManager?.speak(step.text)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        key(state.currentStepIndex) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.cooking_title, state.recipe.title)
                )
                Text(
                    text = stringResource(
                        R.string.cooking_step_progress,
                        state.currentStepIndex + 1,
                        state.recipe.steps.size
                    )
                )
                Text(
                    text = step.text,
                    style = MaterialTheme.typography.headlineLarge
                )
                step.timerSeconds?.let { seconds ->
                    val minutes = NumberFormat.getNumberInstance().apply {
                        maximumFractionDigits = 2
                    }.format(seconds / 60.0)
                    Text(
                        text = stringResource(R.string.cooking_step_time, minutes)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onPreviousStep,
                enabled = state.currentStepIndex > 0,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 72.dp)
            ) {
                Text(text = stringResource(R.string.cooking_previous_step))
            }
            Button(
                onClick = onNextStep,
                enabled = state.currentStepIndex < state.recipe.steps.lastIndex,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 72.dp)
            ) {
                Text(text = stringResource(R.string.cooking_next_step))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { ttsManager?.speak(step.text) },
                enabled = ttsManager != null,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 72.dp)
            ) {
                Text(text = stringResource(R.string.cooking_repeat_step))
            }
            Button(
                onClick = {
                    ttsManager?.stop()
                    onExit()
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 72.dp)
            ) {
                Text(text = stringResource(R.string.cooking_exit))
            }
        }
    }
}
