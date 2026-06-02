# Recipe and Meal Planner

A warm, offline-first Android app for collecting recipes, planning weekly meals, building grocery lists, tracking pantry items, and keeping an eye on calories.

## Features

- Personalized onboarding with skill level, diet, and cooking goal
- 12 preloaded recipes across breakfast, lunch, dinner, snacks, and dessert
- Recipe search, filters, favorites, collections, and detail views
- Weekly planner with meal reminders and calorie summary
- Duplicate-aware grocery generation from planned recipes
- Grocery checklist, pantry tracker, profile editing, dark mode, and reset
- Premium, ad banner, and CSV export placeholders

## Tech Stack

- Kotlin, Jetpack Compose, and Material 3
- MVVM with Repository pattern
- Room database with Kotlin coroutines and Flow
- Gradle Kotlin DSL and Gradle wrapper
- Minimum SDK 26

## Run

1. Open the project in Android Studio Ladybug or newer.
2. Allow Gradle sync to finish.
3. Run the `app` configuration on an Android 8.0 (API 26) or newer emulator.

For Codespaces or command-line verification, install Android SDK 35 and run:

```bash
./gradlew assembleDebug
```

## Screenshots

Add screenshots here after running the app:

- Onboarding
- Dashboard
- Recipes
- Weekly planner
- Grocery and pantry
- Profile

## Future Monetization Ideas

- AI meal plan suggestions
- Nutrition analytics and forecasting
- Unlimited recipe collections
- Grocery list export and family sharing
- Optional ad-free premium plan
