package com.example.recipeplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeplanner.ui.RecipePlannerApp
import com.example.recipeplanner.ui.theme.RecipeTheme
import com.example.recipeplanner.viewmodel.RecipeViewModel
import com.example.recipeplanner.viewmodel.RecipeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: RecipeViewModel = viewModel(factory = RecipeViewModelFactory((application as RecipePlannerApplication).repository))
            val profile by vm.profile.collectAsState()
            RecipeTheme(profile?.darkMode ?: false) { RecipePlannerApp(vm) }
        }
    }
}
