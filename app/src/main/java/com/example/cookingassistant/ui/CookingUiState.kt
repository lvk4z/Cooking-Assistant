package com.example.cookingassistant.ui

import com.example.cookingassistant.data.Recipe

data class CookingUiState(
    val recipe: Recipe,
    val currentStepIndex: Int = 0
)
