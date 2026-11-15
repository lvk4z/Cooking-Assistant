package com.example.cookingassistant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.cookingassistant.data.RecipeRepository
import com.example.cookingassistant.ui.CookingViewModel
import com.example.cookingassistant.ui.screens.CookingScreen
import com.example.cookingassistant.ui.screens.RecipeListScreen

class MainActivity : ComponentActivity() {

    private data object RecipeListRoute

    private data class CookingRoute(val recipeId: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val repository = remember { RecipeRepository() }
            val recipes = remember { repository.getAll() }
            val backStack = remember { mutableStateListOf<Any>(RecipeListRoute) }

            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        },
                        entryProvider = { key ->
                            when (key) {
                                RecipeListRoute -> NavEntry(key) {
                                    RecipeListScreen(
                                        recipes = recipes,
                                        onRecipeClick = { id ->
                                            Log.d("CookingAssistant", "Open recipe: $id")
                                            backStack.add(CookingRoute(id))
                                        }
                                    )
                                }

                                is CookingRoute -> NavEntry(key) {
                                    val cookingViewModel: CookingViewModel = viewModel(
                                        factory = CookingViewModel.factory(
                                            repository,
                                            key.recipeId
                                        )
                                    )
                                    val state by cookingViewModel.uiState
                                        .collectAsStateWithLifecycle()
                                    CookingScreen(
                                        state = state,
                                        onNextStep = cookingViewModel::nextStep,
                                        onPreviousStep = cookingViewModel::previousStep,
                                        onRestartTimer = cookingViewModel::restartCurrentTimer,
                                        onExit = {
                                            Log.d("CookingAssistant", "Exit cooking")
                                            if (backStack.size > 1) {
                                                backStack.removeAt(backStack.lastIndex)
                                            }
                                        }
                                    )
                                }

                                else -> error("Unknown route: $key")
                            }
                        }
                    )
                }
            }
        }
    }
}
