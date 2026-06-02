package com.example.recipeplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeplanner.data.model.*

@Database(
    entities = [UserProfile::class, Recipe::class, Ingredient::class, RecipeStep::class, MealPlan::class,
        GroceryItem::class, FavoriteRecipe::class, RecipeCollection::class, CollectionRecipe::class, PantryItem::class],
    version = 1, exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun dao(): RecipeDao
    companion object {
        @Volatile private var instance: RecipeDatabase? = null
        fun get(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, RecipeDatabase::class.java, "recipe-planner.db").build().also { instance = it }
        }
    }
}
