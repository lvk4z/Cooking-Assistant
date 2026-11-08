package com.example.cookingassistant.data

data class Recipe(
    val id: Int,
    val title: String,
    val steps: List<Step>
)
