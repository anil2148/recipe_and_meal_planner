package com.example.recipeplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipeplanner.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM profiles WHERE id = 1") fun profile(): Flow<UserProfile?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveProfile(profile: UserProfile)
    @Query("SELECT * FROM recipes ORDER BY id DESC") fun recipes(): Flow<List<Recipe>>
    @Insert suspend fun addRecipe(recipe: Recipe): Long
    @Update suspend fun updateRecipe(recipe: Recipe)
    @Delete suspend fun deleteRecipe(recipe: Recipe)
    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId") fun ingredients(recipeId: Long): Flow<List<Ingredient>>
    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId") suspend fun ingredientList(recipeId: Long): List<Ingredient>
    @Insert suspend fun addIngredients(items: List<Ingredient>)
    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId") suspend fun clearIngredients(recipeId: Long)
    @Query("SELECT * FROM recipe_steps WHERE recipeId = :recipeId ORDER BY number") fun steps(recipeId: Long): Flow<List<RecipeStep>>
    @Insert suspend fun addSteps(items: List<RecipeStep>)
    @Query("DELETE FROM recipe_steps WHERE recipeId = :recipeId") suspend fun clearSteps(recipeId: Long)
    @Query("SELECT * FROM meal_plans ORDER BY id") fun mealPlans(): Flow<List<MealPlan>>
    @Insert suspend fun addMealPlan(item: MealPlan)
    @Delete suspend fun deleteMealPlan(item: MealPlan)
    @Query("SELECT * FROM grocery_items ORDER BY purchased, name") fun groceryItems(): Flow<List<GroceryItem>>
    @Insert suspend fun addGroceryItem(item: GroceryItem)
    @Update suspend fun updateGroceryItem(item: GroceryItem)
    @Delete suspend fun deleteGroceryItem(item: GroceryItem)
    @Query("DELETE FROM grocery_items WHERE purchased = 1") suspend fun clearPurchased()
    @Query("SELECT * FROM favorite_recipes") fun favorites(): Flow<List<FavoriteRecipe>>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun favorite(item: FavoriteRecipe)
    @Delete suspend fun unfavorite(item: FavoriteRecipe)
    @Query("SELECT * FROM collections ORDER BY name") fun collections(): Flow<List<RecipeCollection>>
    @Insert suspend fun addCollection(item: RecipeCollection)
    @Query("SELECT * FROM pantry_items ORDER BY name") fun pantryItems(): Flow<List<PantryItem>>
    @Insert suspend fun addPantryItem(item: PantryItem)
    @Delete suspend fun deletePantryItem(item: PantryItem)
    @Query("SELECT COUNT(*) FROM recipes") suspend fun recipeCount(): Int
    @Query("DELETE FROM profiles") suspend fun clearProfile()
    @Query("DELETE FROM meal_plans") suspend fun clearMealPlans()
    @Query("DELETE FROM grocery_items") suspend fun clearGroceries()
    @Query("DELETE FROM favorite_recipes") suspend fun clearFavorites()
    @Query("DELETE FROM collections") suspend fun clearCollections()
    @Query("DELETE FROM pantry_items") suspend fun clearPantry()
}
