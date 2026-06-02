package com.example.recipeplanner

import android.app.Application
import com.example.recipeplanner.data.local.RecipeDatabase
import com.example.recipeplanner.repository.RecipeRepository

class RecipePlannerApplication : Application() {
    val repository by lazy { RecipeRepository(RecipeDatabase.get(this).dao()) }
}
