package com.example.recipeplanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfile(@PrimaryKey val id: Int = 1, val name: String, val skill: String, val diet: String, val goal: String, val darkMode: Boolean = false)

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val description: String, val category: String, val diet: String,
    val prepMinutes: Int, val cookMinutes: Int, val servings: Int, val calories: Int,
    val difficulty: String, val imagePlaceholder: String = "Recipe image"
) { val totalMinutes get() = prepMinutes + cookMinutes }

@Entity(tableName = "ingredients")
data class Ingredient(@PrimaryKey(autoGenerate = true) val id: Long = 0, val recipeId: Long, val name: String, val quantity: Double, val unit: String)

@Entity(tableName = "recipe_steps")
data class RecipeStep(@PrimaryKey(autoGenerate = true) val id: Long = 0, val recipeId: Long, val number: Int, val instruction: String)

@Entity(tableName = "meal_plans")
data class MealPlan(@PrimaryKey(autoGenerate = true) val id: Long = 0, val day: String, val mealType: String, val recipeId: Long, val reminder: Boolean = false)

@Entity(tableName = "grocery_items")
data class GroceryItem(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val quantity: Double, val unit: String, val category: String, val purchased: Boolean = false)

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(@PrimaryKey val recipeId: Long)

@Entity(tableName = "collections")
data class RecipeCollection(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String)

@Entity(tableName = "collection_recipes", primaryKeys = ["collectionId", "recipeId"])
data class CollectionRecipe(val collectionId: Long, val recipeId: Long)

@Entity(tableName = "pantry_items")
data class PantryItem(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val quantity: Double, val unit: String)

data class RecipeBundle(val recipe: Recipe, val ingredients: List<Ingredient>, val steps: List<RecipeStep>)
