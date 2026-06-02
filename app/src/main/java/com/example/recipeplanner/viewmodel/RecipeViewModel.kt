package com.example.recipeplanner.viewmodel

import androidx.lifecycle.*
import com.example.recipeplanner.data.model.*
import com.example.recipeplanner.repository.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(private val repo: RecipeRepository) : ViewModel() {
    val profile = repo.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val recipes = repo.recipes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val plans = repo.plans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val groceries = repo.groceries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favorites = repo.favorites.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val collections = repo.collections.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val pantry = repo.pantry.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    init { viewModelScope.launch { repo.seedSamples() } }
    fun ingredients(id: Long) = repo.ingredients(id)
    fun steps(id: Long) = repo.steps(id)
    fun saveProfile(item: UserProfile) = viewModelScope.launch { repo.saveProfile(item) }
    fun saveRecipe(recipe: Recipe, ingredients: List<Ingredient>, steps: List<RecipeStep>) = viewModelScope.launch { repo.saveRecipe(recipe, ingredients, steps) }
    fun deleteRecipe(item: Recipe) = viewModelScope.launch { repo.deleteRecipe(item) }
    fun addPlan(item: MealPlan) = viewModelScope.launch { repo.addPlan(item) }
    fun deletePlan(item: MealPlan) = viewModelScope.launch { repo.deletePlan(item) }
    fun addGrocery(item: GroceryItem) = viewModelScope.launch { repo.addGrocery(item) }
    fun updateGrocery(item: GroceryItem) = viewModelScope.launch { repo.updateGrocery(item) }
    fun deleteGrocery(item: GroceryItem) = viewModelScope.launch { repo.deleteGrocery(item) }
    fun clearPurchased() = viewModelScope.launch { repo.clearPurchased() }
    fun generateGroceries() = viewModelScope.launch { repo.generateGroceries(plans.value) }
    fun toggleFavorite(id: Long) = viewModelScope.launch { repo.toggleFavorite(id, favorites.value.any { it.recipeId == id }) }
    fun addCollection(name: String) = viewModelScope.launch { repo.addCollection(name) }
    fun addPantry(item: PantryItem) = viewModelScope.launch { repo.addPantry(item) }
    fun deletePantry(item: PantryItem) = viewModelScope.launch { repo.deletePantry(item) }
    fun reset() = viewModelScope.launch { repo.reset() }
}
class RecipeViewModelFactory(private val repo: RecipeRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = RecipeViewModel(repo) as T
}
