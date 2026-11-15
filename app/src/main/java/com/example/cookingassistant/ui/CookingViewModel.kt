package com.example.cookingassistant.ui

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cookingassistant.data.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class CookingViewModel(
    repository: RecipeRepository,
    recipeId: Int
) : ViewModel() {

    private val recipe = requireNotNull(repository.getById(recipeId)) {
        "Recipe not found: $recipeId"
    }

    init {
        require(recipe.steps.isNotEmpty()) { "Recipe has no steps: $recipeId" }
    }

    private val _uiState = MutableStateFlow(CookingUiState(recipe))
    val uiState = _uiState.asStateFlow()

    init {
        startTimerIfNeeded(0)
        viewModelScope.launch {
            while (true) {
                delay(1000.milliseconds)
                val now = SystemClock.elapsedRealtime()
                _uiState.update { state ->
                    state.copy(timers = state.timers.map { timer ->
                        timer.copy(
                            remainingSeconds = ((timer.endTimeMillis - now + 999)
                                .coerceAtLeast(0) / 1000).toInt()
                        )
                    })
                }
            }
        }
    }

    fun nextStep() {
        _uiState.update { state ->
            if (state.currentStepIndex < state.recipe.steps.lastIndex) {
                state.copy(currentStepIndex = state.currentStepIndex + 1)
            } else {
                state
            }
        }
        startTimerIfNeeded(_uiState.value.currentStepIndex)
    }

    fun previousStep() {
        _uiState.update { state ->
            if (state.currentStepIndex > 0) {
                state.copy(currentStepIndex = state.currentStepIndex - 1)
            } else {
                state
            }
        }
        startTimerIfNeeded(_uiState.value.currentStepIndex)
    }

    private fun startTimerIfNeeded(stepIndex: Int) {
        val seconds = recipe.steps[stepIndex].timerSeconds ?: return
        if (seconds <= 0 || _uiState.value.timers.any { it.stepIndex == stepIndex }) return
        startTimer(stepIndex, seconds)
    }

    fun restartCurrentTimer() {
        val stepIndex = _uiState.value.currentStepIndex
        val seconds = recipe.steps[stepIndex].timerSeconds ?: return
        if (seconds <= 0) return
        startTimer(stepIndex, seconds)
    }

    private fun startTimer(stepIndex: Int, seconds: Int) {
        val timer = CookingTimer(
            stepIndex = stepIndex,
            endTimeMillis = SystemClock.elapsedRealtime() + seconds * 1000L,
            remainingSeconds = seconds
        )
        _uiState.update { state ->
            state.copy(timers = state.timers.filterNot { it.stepIndex == stepIndex } + timer)
        }
        Log.d("CookingTimer", "Timer started for step ${stepIndex + 1}")
    }

    companion object {
        fun factory(repository: RecipeRepository, recipeId: Int) = viewModelFactory {
            initializer {
                CookingViewModel(repository, recipeId)
            }
        }
    }
}
