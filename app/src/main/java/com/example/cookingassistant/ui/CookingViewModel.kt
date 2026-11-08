package com.example.cookingassistant.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cookingassistant.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun nextStep() {
        _uiState.update { state ->
            if (state.currentStepIndex < state.recipe.steps.lastIndex) {
                state.copy(currentStepIndex = state.currentStepIndex + 1)
            } else {
                state
            }
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            if (state.currentStepIndex > 0) {
                state.copy(currentStepIndex = state.currentStepIndex - 1)
            } else {
                state
            }
        }
    }

    companion object {
        fun factory(repository: RecipeRepository, recipeId: Int) = viewModelFactory {
            initializer {
                CookingViewModel(repository, recipeId)
            }
        }
    }
}
