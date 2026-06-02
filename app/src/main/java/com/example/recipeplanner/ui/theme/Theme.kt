package com.example.recipeplanner.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(primary = Color(0xFFEF6C00), secondary = Color(0xFF558B2F), background = Color(0xFFFFFBF5), surface = Color(0xFFFFFBF5))
private val Dark = darkColorScheme(primary = Color(0xFFFFB74D), secondary = Color(0xFFAED581))
@Composable fun RecipeTheme(dark: Boolean, content: @Composable () -> Unit) = MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
