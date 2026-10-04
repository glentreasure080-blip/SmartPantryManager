# Smart Pantry Manager

A Java Android application that helps reduce food waste by tracking pantry ingredients and suggesting only recipes that can be made with ingredients currently available.

## Database
SQLite using `SQLiteOpenHelper` was chosen because the assignment requires persistent CRUD data and the app can operate completely offline. SQLite is also straightforward to demonstrate and explain in Android Studio.

## Features
- Add, view, edit and delete pantry items
- Ingredient quantity, unit and optional expiry date
- RecyclerView with custom adapters
- 20 seeded recipes
- Strict recipe matching based on ingredient name, unit and quantity
- Recipe detail screen
- Input validation
- Navigation between Home, Pantry and Suggested Recipes
- Data persists after the application is closed and reopened

## Setup
1. Open Android Studio.
2. Select **Open** and choose the `SmartPantryManager` folder.
3. Allow Gradle to sync.
4. Use an Android emulator or physical Android device with API 24+.
5. Run the `app` configuration.

## Strict Matching Rule
A recipe is suggested only if every required ingredient exists in the pantry, the pantry unit matches the recipe unit, and the pantry quantity is at least the required quantity.

## Suggested GitHub commit sequence
1. Create Android project
2. Add initial layouts
3. Create SQLite database
4. Implement pantry CRUD
5. Add RecyclerView adapter
6. Add input validation
7. Seed recipe data
8. Implement strict matching
9. Add recipe details
10. Test persistence and polish UI
