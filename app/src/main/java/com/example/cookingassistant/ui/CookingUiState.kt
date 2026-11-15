package com.example.cookingassistant.ui

import com.example.cookingassistant.data.Recipe

data class CookingUiState(
    val recipe: Recipe,
    val currentStepIndex: Int = 0,
    val timers: List<CookingTimer> = emptyList()
)

data class CookingTimer(
    val stepIndex: Int,
    val endTimeMillis: Long,
    val remainingSeconds: Int
)
