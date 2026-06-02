package com.example.recipeplanner.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.recipeplanner.data.model.*
import com.example.recipeplanner.viewmodel.RecipeViewModel

private data class Tab(val route: String, val label: String, val icon: ImageVector)
private val tabs = listOf(Tab("home", "Home", Icons.Default.Home), Tab("recipes", "Recipes", Icons.Default.RestaurantMenu), Tab("planner", "Planner", Icons.Default.CalendarMonth), Tab("grocery", "Grocery", Icons.Default.ShoppingCart), Tab("profile", "Profile", Icons.Default.Person))
private val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
private val meals = listOf("Breakfast", "Lunch", "Dinner", "Snack")
private val diets = listOf("Vegetarian", "Non-Vegetarian", "Vegan", "Keto", "Low Carb", "High Protein", "Gluten Free", "No Preference")

@Composable fun RecipePlannerApp(vm: RecipeViewModel) {
    val profile by vm.profile.collectAsState()
    if (profile == null) Onboarding(vm::saveProfile) else MainApp(vm, profile!!)
}

@Composable private fun MainApp(vm: RecipeViewModel, profile: UserProfile) {
    val nav = rememberNavController(); val route = nav.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(bottomBar = { NavigationBar { tabs.forEach { tab -> NavigationBarItem(route == tab.route, { nav.navigate(tab.route) { launchSingleTop = true } }, { Icon(tab.icon, tab.label) }, label = { Text(tab.label) }) } } }) { padding ->
        NavHost(nav, "home", Modifier.padding(padding)) {
            composable("home") { Home(vm, profile) { nav.navigate(it) } }
            composable("recipes") { Recipes(vm) }
            composable("planner") { Planner(vm) }
            composable("grocery") { Grocery(vm) }
            composable("profile") { Profile(vm, profile) }
        }
    }
}

@Composable private fun Onboarding(save: (UserProfile) -> Unit) {
    var name by remember { mutableStateOf("") }; var skill by remember { mutableStateOf("Beginner") }; var diet by remember { mutableStateOf("No Preference") }; var goal by remember { mutableStateOf("Eat Healthy") }; var error by remember { mutableStateOf("") }
    FormPage("Welcome", "Let's personalize your kitchen.") {
        Field(name, { name = it }, "Your name")
        Choice("Cooking skill", skill, listOf("Beginner", "Intermediate", "Advanced")) { skill = it }
        Choice("Diet", diet, diets) { diet = it }
        Choice("Main goal", goal, listOf("Eat Healthy", "Save Money", "Meal Prep", "Lose Weight", "Quick Cooking", "Family Meals")) { goal = it }
        Error(error); Button({ if (name.isBlank()) error = "Name is required" else save(UserProfile(name = name.trim(), skill = skill, diet = diet, goal = goal)) }, Modifier.fillMaxWidth()) { Text("Start planning") }
    }
}

@Composable private fun Home(vm: RecipeViewModel, profile: UserProfile, navigate: (String) -> Unit) {
    val plans by vm.plans.collectAsState(); val recipes by vm.recipes.collectAsState(); val today = days.first(); val todayPlans = plans.filter { it.day == today }; val plannedCalories = plans.sumOf { plan -> recipes.firstOrNull { it.id == plan.recipeId }?.calories ?: 0 }
    Page("Hi, ${profile.name}", "Make something delicious today.") {
        Hero("Today's meals", if (todayPlans.isEmpty()) "Nothing planned yet" else todayPlans.joinToString { it.mealType })
        Hero("Recipe of the day", recipes.firstOrNull()?.name ?: "Loading recipes...")
        Text("Weekly progress: ${plans.size} / 28 meal slots")
        LinearProgressIndicator({ (plans.size / 28f).coerceIn(0f, 1f) }, Modifier.fillMaxWidth())
        Text("Weekly calorie summary: $plannedCalories kcal", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton({ navigate("recipes") }, Modifier.weight(1f)) { Text("Add recipe") }; OutlinedButton({ navigate("planner") }, Modifier.weight(1f)) { Text("Meal planner") } }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton({ navigate("grocery") }, Modifier.weight(1f)) { Text("Grocery list") }; OutlinedButton({ navigate("recipes") }, Modifier.weight(1f)) { Text("Favorites") } }
        AdBanner()
    }
}

@Composable private fun Recipes(vm: RecipeViewModel) {
    val recipes by vm.recipes.collectAsState(); val favorites by vm.favorites.collectAsState(); var query by remember { mutableStateOf("") }; var category by remember { mutableStateOf("All") }; var sort by remember { mutableStateOf("Newest") }; var editing by remember { mutableStateOf<Recipe?>(null) }; var detail by remember { mutableStateOf<Recipe?>(null) }; var showForm by remember { mutableStateOf(false) }
    val filtered = recipes.filter { (category == "All" || it.category == category) && (it.name.contains(query, true) || it.description.contains(query, true)) }.let { list -> when (sort) { "Quickest" -> list.sortedBy { it.totalMinutes }; "Lowest calories" -> list.sortedBy { it.calories }; "Favorites" -> list.sortedByDescending { r -> favorites.any { it.recipeId == r.id } }; else -> list } }
    Scaffold(floatingActionButton = { FloatingActionButton({ showForm = true }) { Icon(Icons.Default.Add, "Add recipe") } }) { padding ->
        Page("Recipes", "Search, collect, and cook", Modifier.padding(padding)) {
            Field(query, { query = it }, "Search recipes or ingredients")
            Choice("Category", category, listOf("All", "Breakfast", "Lunch", "Dinner", "Snacks", "Dessert", "Drinks", "Meal Prep", "Kids Friendly")) { category = it }
            Choice("Sort", sort, listOf("Newest", "Quickest", "Lowest calories", "Favorites")) { sort = it }
            if (filtered.isEmpty()) Text("No recipes found. Try another search or add your own.")
            filtered.forEach { recipe -> RecipeCard(recipe, favorites.any { it.recipeId == recipe.id }, { vm.toggleFavorite(recipe.id) }, { detail = recipe }) }
        }
    }
    if (showForm || editing != null) RecipeDialog(editing, vm::saveRecipe) { showForm = false; editing = null }
    detail?.let { RecipeDetail(it, vm, { detail = null }, { editing = it; detail = null }) }
}

@Composable private fun RecipeCard(recipe: Recipe, favorite: Boolean, toggle: () -> Unit, open: () -> Unit) {
    Card(onClick = open, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
        Row { Text(recipe.name, Modifier.weight(1f), fontWeight = FontWeight.Bold); IconButton(toggle) { Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite") } }
        Text("${recipe.category} • ${recipe.totalMinutes} min • ${recipe.calories} kcal"); Text(recipe.description)
    } }
}

@Composable private fun RecipeDetail(recipe: Recipe, vm: RecipeViewModel, close: () -> Unit, edit: () -> Unit) {
    val ingredients by vm.ingredients(recipe.id).collectAsState(initial = emptyList()); val steps by vm.steps(recipe.id).collectAsState(initial = emptyList())
    AlertDialog(onDismissRequest = close, text = { Column { Text(recipe.description); Text("${recipe.totalMinutes} min • ${recipe.servings} servings • ${recipe.calories} kcal"); Section("Ingredients"); ingredients.forEach { Text("☐ ${it.quantity} ${it.unit} ${it.name}") }; Section("Steps"); steps.forEach { Text("${it.number}. ${it.instruction}") } } },
        confirmButton = { TextButton(close) { Text("Close") } }, dismissButton = { Row { TextButton(edit) { Text("Edit") }; TextButton({ vm.toggleFavorite(recipe.id) }) { Text("Favorite") } } }, title = { Text(recipe.name) })
}

@Composable private fun Planner(vm: RecipeViewModel) {
    val recipes by vm.recipes.collectAsState(); val plans by vm.plans.collectAsState(); var addFor by remember { mutableStateOf<Pair<String, String>?>(null) }; val calories = plans.sumOf { p -> recipes.firstOrNull { it.id == p.recipeId }?.calories ?: 0 }
    Page("Weekly planner", "$calories kcal planned this week") {
        days.forEach { day -> Section(day); meals.forEach { meal -> val item = plans.firstOrNull { it.day == day && it.mealType == meal }; Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(meal, fontWeight = FontWeight.Bold); Text(item?.let { plan -> recipes.firstOrNull { it.id == plan.recipeId }?.name } ?: "Add a meal") }; TextButton({ addFor = day to meal }) { Text(if (item == null) "Add" else "Replace") }; item?.let { TextButton({ vm.deletePlan(it) }) { Text("Remove") } } } } } }
        AdBanner()
    }
    addFor?.let { (day, meal) -> MealDialog(day, meal, recipes, { recipe, reminder -> plans.firstOrNull { it.day == day && it.mealType == meal }?.let(vm::deletePlan); vm.addPlan(MealPlan(day = day, mealType = meal, recipeId = recipe.id, reminder = reminder)); addFor = null }, { addFor = null }) }
}

@Composable private fun MealDialog(day: String, meal: String, recipes: List<Recipe>, save: (Recipe, Boolean) -> Unit, close: () -> Unit) {
    var chosen by remember { mutableStateOf(recipes.firstOrNull()) }; var reminder by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = close, text = { Column { Choice("Recipe", chosen?.name ?: "Choose", recipes.map { it.name }) { name -> chosen = recipes.first { it.name == name } }; Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(reminder, { reminder = it }); Text("Meal reminder") } } }, confirmButton = { TextButton({ chosen?.let { save(it, reminder) } }) { Text("Save") } }, dismissButton = { TextButton(close) { Text("Cancel") } }, title = { Text("$day • $meal") })
}

@Composable private fun Grocery(vm: RecipeViewModel) {
    val items by vm.groceries.collectAsState(); val pantry by vm.pantry.collectAsState(); var name by remember { mutableStateOf("") }; var pantryName by remember { mutableStateOf("") }
    Page("Grocery and pantry", "Stay ready for the week") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button(vm::generateGroceries, Modifier.weight(1f)) { Text("Generate from plan") }; OutlinedButton(vm::clearPurchased, Modifier.weight(1f)) { Text("Clear purchased") } }
        Field(name, { name = it }, "Add grocery item")
        Button({ if (name.isNotBlank()) { vm.addGrocery(GroceryItem(name = name.trim(), quantity = 1.0, unit = "item", category = "Other")); name = "" } }, Modifier.fillMaxWidth()) { Text("Add grocery") }
        items.forEach { item -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Checkbox(item.purchased, { vm.updateGrocery(item.copy(purchased = it)) }); Text("${item.name} • ${item.quantity} ${item.unit}", Modifier.weight(1f)); IconButton({ vm.deleteGrocery(item) }) { Text("×") } } }
        if (items.isEmpty()) Text("Your grocery list is empty. Generate it from your weekly plan.")
        Section("Pantry tracker")
        Field(pantryName, { pantryName = it }, "Add pantry item")
        OutlinedButton({ if (pantryName.isNotBlank()) { vm.addPantry(PantryItem(name = pantryName.trim(), quantity = 1.0, unit = "item")); pantryName = "" } }, Modifier.fillMaxWidth()) { Text("Add pantry item") }
        pantry.forEach { item -> Row(Modifier.fillMaxWidth()) { Text("${item.name} • ${item.quantity} ${item.unit}", Modifier.weight(1f)); TextButton({ vm.deletePantry(item) }) { Text("Remove") } } }
    }
}

@Composable private fun Profile(vm: RecipeViewModel, profile: UserProfile) {
    val collections by vm.collections.collectAsState(); var name by remember(profile) { mutableStateOf(profile.name) }; var diet by remember(profile) { mutableStateOf(profile.diet) }; var skill by remember(profile) { mutableStateOf(profile.skill) }; var dark by remember(profile) { mutableStateOf(profile.darkMode) }; var collection by remember { mutableStateOf("") }; var status by remember { mutableStateOf("") }
    Page("Profile", "Preferences and collections") {
        Field(name, { name = it }, "Name"); Choice("Diet", diet, diets) { diet = it }; Choice("Skill", skill, listOf("Beginner", "Intermediate", "Advanced")) { skill = it }
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Dark mode", Modifier.weight(1f)); Switch(dark, { dark = it }) }
        Button({ if (name.isNotBlank()) { vm.saveProfile(profile.copy(name = name.trim(), diet = diet, skill = skill, darkMode = dark)); status = "Profile saved" } }, Modifier.fillMaxWidth()) { Text("Save profile") }
        OutlinedButton({ status = "CSV export is coming soon." }, Modifier.fillMaxWidth()) { Text("CSV export placeholder") }
        Section("Recipe collections"); Field(collection, { collection = it }, "New collection"); OutlinedButton({ if (collection.isNotBlank()) { vm.addCollection(collection.trim()); collection = "" } }, Modifier.fillMaxWidth()) { Text("Add collection") }; collections.forEach { Text("• ${it.name}") }
        Error(status); Premium(); TextButton(vm::reset, Modifier.fillMaxWidth()) { Text("Reset app data") }
    }
}

@Composable private fun RecipeDialog(existing: Recipe?, save: (Recipe, List<Ingredient>, List<RecipeStep>) -> Unit, close: () -> Unit) {
    var name by remember { mutableStateOf(existing?.name ?: "") }; var description by remember { mutableStateOf(existing?.description ?: "") }; var category by remember { mutableStateOf(existing?.category ?: "Breakfast") }; var ingredients by remember { mutableStateOf("") }; var steps by remember { mutableStateOf("") }; var calories by remember { mutableStateOf(existing?.calories?.toString() ?: "") }; var error by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = close, text = { Column { Field(name, { name = it }, "Recipe name"); Field(description, { description = it }, "Description"); Choice("Category", category, listOf("Breakfast", "Lunch", "Dinner", "Snacks", "Dessert", "Drinks", "Meal Prep", "Kids Friendly")) { category = it }; Field(ingredients, { ingredients = it }, "Ingredients, comma separated"); Field(steps, { steps = it }, "Steps, separated by ;"); Field(calories, { calories = it }, "Calories", true); Error(error) } },
        confirmButton = { TextButton({ val kcal = calories.toIntOrNull(); if (name.isBlank() || ingredients.isBlank() || steps.isBlank() || kcal == null || kcal < 0) error = "Complete required fields with valid values" else { val recipe = existing?.copy(name = name, description = description, category = category, calories = kcal) ?: Recipe(name = name, description = description, category = category, diet = "No Preference", prepMinutes = 10, cookMinutes = 15, servings = 2, calories = kcal, difficulty = "Beginner"); save(recipe, ingredients.split(",").map { Ingredient(recipeId = recipe.id, name = it.trim(), quantity = 1.0, unit = "portion") }, steps.split(";").mapIndexed { i, s -> RecipeStep(recipeId = recipe.id, number = i + 1, instruction = s.trim()) }); close() } }) { Text("Save") } },
        dismissButton = { TextButton(close) { Text("Cancel") } }, title = { Text(if (existing == null) "Add recipe" else "Edit recipe") })
}

@Composable private fun Premium() = Card { Column(Modifier.padding(16.dp)) { Text("Premium preview", fontWeight = FontWeight.Bold); listOf("AI meal plan suggestions", "Nutrition analytics", "Unlimited recipe collections", "Export grocery list", "Family meal planning", "No ads").forEach { Text("✓ $it") } } }
@Composable private fun AdBanner() = Card(Modifier.fillMaxWidth()) { Text("Ad banner placeholder", Modifier.padding(16.dp).align(Alignment.CenterHorizontally)) }
@Composable private fun Hero(title: String, value: String) = Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(title); Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) } }
@Composable private fun Section(text: String) = Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
@Composable private fun Error(text: String) { if (text.isNotEmpty()) Text(text, color = MaterialTheme.colorScheme.primary) }
@Composable private fun Field(value: String, change: (String) -> Unit, label: String, numeric: Boolean = false) = OutlinedTextField(value, change, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text))
@Composable private fun Choice(label: String, value: String, options: List<String>, select: (String) -> Unit) { var open by remember { mutableStateOf(false) }; Box { OutlinedButton({ open = true }, Modifier.fillMaxWidth()) { Text("$label: $value") }; DropdownMenu(open, { open = false }) { options.forEach { DropdownMenuItem({ Text(it) }, { select(it); open = false }) } } } }
@Composable private fun Page(title: String, subtitle: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) = LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }; item { Text(subtitle) }; item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) } }
@Composable private fun FormPage(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Card(Modifier.padding(20.dp).fillMaxWidth(), shape = RoundedCornerShape(24.dp)) { Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text(subtitle); content() } } }
