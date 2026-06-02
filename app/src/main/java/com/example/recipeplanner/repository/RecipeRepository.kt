package com.example.recipeplanner.repository

import com.example.recipeplanner.data.local.RecipeDao
import com.example.recipeplanner.data.model.*

class RecipeRepository(private val dao: RecipeDao) {
    val profile = dao.profile()
    val recipes = dao.recipes()
    val plans = dao.mealPlans()
    val groceries = dao.groceryItems()
    val favorites = dao.favorites()
    val collections = dao.collections()
    val pantry = dao.pantryItems()
    fun ingredients(recipeId: Long) = dao.ingredients(recipeId)
    fun steps(recipeId: Long) = dao.steps(recipeId)

    suspend fun saveProfile(item: UserProfile) = dao.saveProfile(item)
    suspend fun saveRecipe(recipe: Recipe, ingredients: List<Ingredient>, steps: List<RecipeStep>) {
        val id = if (recipe.id == 0L) dao.addRecipe(recipe) else { dao.updateRecipe(recipe); recipe.id }
        dao.clearIngredients(id); dao.clearSteps(id)
        dao.addIngredients(ingredients.map { it.copy(recipeId = id) })
        dao.addSteps(steps.mapIndexed { index, step -> step.copy(recipeId = id, number = index + 1) })
    }
    suspend fun deleteRecipe(recipe: Recipe) { dao.clearIngredients(recipe.id); dao.clearSteps(recipe.id); dao.deleteRecipe(recipe) }
    suspend fun addPlan(item: MealPlan) = dao.addMealPlan(item)
    suspend fun deletePlan(item: MealPlan) = dao.deleteMealPlan(item)
    suspend fun addGrocery(item: GroceryItem) = dao.addGroceryItem(item)
    suspend fun updateGrocery(item: GroceryItem) = dao.updateGroceryItem(item)
    suspend fun deleteGrocery(item: GroceryItem) = dao.deleteGroceryItem(item)
    suspend fun clearPurchased() = dao.clearPurchased()
    suspend fun toggleFavorite(recipeId: Long, favorite: Boolean) = if (favorite) dao.unfavorite(FavoriteRecipe(recipeId)) else dao.favorite(FavoriteRecipe(recipeId))
    suspend fun addCollection(name: String) = dao.addCollection(RecipeCollection(name = name))
    suspend fun addPantry(item: PantryItem) = dao.addPantryItem(item)
    suspend fun deletePantry(item: PantryItem) = dao.deletePantryItem(item)

    suspend fun generateGroceries(plans: List<MealPlan>) {
        plans.flatMap { dao.ingredientList(it.recipeId) }
            .groupBy { "${it.name.lowercase()}|${it.unit.lowercase()}" }
            .map { (_, values) -> GroceryItem(name = values.first().name, quantity = values.sumOf { it.quantity }, unit = values.first().unit, category = "Other") }
            .forEach { dao.addGroceryItem(it) }
    }

    suspend fun seedSamples() {
        if (dao.recipeCount() > 0) return
        samples.forEach { sample ->
            saveRecipe(sample.recipe, sample.ingredients.map { Ingredient(recipeId = 0, name = it, quantity = 1.0, unit = "portion") },
                sample.steps.mapIndexed { index, it -> RecipeStep(recipeId = 0, number = index + 1, instruction = it) })
        }
    }

    suspend fun reset() {
        dao.clearProfile(); dao.clearMealPlans(); dao.clearGroceries(); dao.clearFavorites(); dao.clearCollections(); dao.clearPantry()
    }
}

private data class Sample(val recipe: Recipe, val ingredients: List<String>, val steps: List<String>)
private fun sample(name: String, category: String, calories: Int, ingredients: List<String>) =
    Sample(Recipe(name = name, description = "A simple and satisfying $name recipe.", category = category, diet = "No Preference", prepMinutes = 10, cookMinutes = 15, servings = 2, calories = calories, difficulty = "Beginner"),
        ingredients, listOf("Prepare the ingredients.", "Cook until flavors come together.", "Serve fresh and enjoy."))

private val samples = listOf(
    sample("Berry Oat Bowl", "Breakfast", 320, listOf("Oats", "Milk", "Berries")),
    sample("Veggie Omelette", "Breakfast", 290, listOf("Eggs", "Spinach", "Tomato")),
    sample("Banana Pancakes", "Breakfast", 360, listOf("Banana", "Flour", "Milk")),
    sample("Mediterranean Wrap", "Lunch", 430, listOf("Tortilla", "Chickpeas", "Cucumber")),
    sample("Quinoa Power Salad", "Lunch", 410, listOf("Quinoa", "Spinach", "Tomato")),
    sample("Grilled Paneer Bowl", "Lunch", 520, listOf("Paneer", "Rice", "Bell pepper")),
    sample("Tomato Basil Pasta", "Dinner", 560, listOf("Pasta", "Tomato", "Basil")),
    sample("Lemon Herb Chicken", "Dinner", 610, listOf("Chicken", "Lemon", "Potato")),
    sample("Vegetable Stir Fry", "Dinner", 470, listOf("Rice", "Broccoli", "Carrot")),
    sample("Hummus Snack Box", "Snacks", 220, listOf("Hummus", "Carrot", "Cucumber")),
    sample("Trail Mix Cups", "Snacks", 190, listOf("Almonds", "Raisins", "Seeds")),
    sample("Yogurt Berry Parfait", "Dessert", 260, listOf("Yogurt", "Berries", "Granola"))
)
