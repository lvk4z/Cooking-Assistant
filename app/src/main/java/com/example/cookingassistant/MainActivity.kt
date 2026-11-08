package com.example.cookingassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.cookingassistant.data.RecipeRepository
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
                                            backStack.add(CookingRoute(id))
                                        }
                                    )
                                }

                                is CookingRoute -> NavEntry(key) {
                                    val recipe = requireNotNull(
                                        repository.getById(key.recipeId)
                                    ) { "Recipe not found: ${key.recipeId}" }
                                    Text(
                                        text = stringResource(
                                            R.string.cooking_title,
                                            recipe.title
                                        ),
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.headlineSmall
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
