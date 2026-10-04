package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, ingredient_name TEXT NOT NULL, required_quantity REAL NOT NULL, unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    public long insertPantry(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put("name", item.getName()); v.put("quantity", item.getQuantity());
        v.put("unit", item.getUnit()); v.put("expiry_date", item.getExpiryDate());
        return getWritableDatabase().insert("pantry", null, v);
    }

    public int updatePantry(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put("name", item.getName()); v.put("quantity", item.getQuantity());
        v.put("unit", item.getUnit()); v.put("expiry_date", item.getExpiryDate());
        return getWritableDatabase().update("pantry", v, "id=?", new String[]{String.valueOf(item.getId())});
    }

    public int deletePantry(int id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantry(int id) {
        Cursor c = getReadableDatabase().query("pantry", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (c.moveToFirst()) return new PantryItem(c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")), c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit")), c.getString(c.getColumnIndexOrThrow("expiry_date")));
            return null;
        } finally { c.close(); }
    }

    public List<PantryItem> getAllPantry() {
        List<PantryItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE ASC");
        try {
            while (c.moveToNext()) list.add(new PantryItem(c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")), c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit")), c.getString(c.getColumnIndexOrThrow("expiry_date"))));
        } finally { c.close(); }
        return list;
    }

    public List<Recipe> getSuggestedRecipes() {
        Map<String, PantryItem> pantry = new HashMap<>();
        for (PantryItem p : getAllPantry()) pantry.put(normalize(p.getName()), p);

        List<Recipe> suggestions = new ArrayList<>();
        Cursor recipes = getReadableDatabase().query("recipes", null, null, null, null, null, "name COLLATE NOCASE ASC");
        try {
            while (recipes.moveToNext()) {
                int id = recipes.getInt(recipes.getColumnIndexOrThrow("id"));
                String name = recipes.getString(recipes.getColumnIndexOrThrow("name"));
                String steps = recipes.getString(recipes.getColumnIndexOrThrow("steps"));
                if (canMakeRecipe(id, pantry)) suggestions.add(new Recipe(id, name, steps));
            }
        } finally { recipes.close(); }
        return suggestions;
    }

    private boolean canMakeRecipe(int recipeId, Map<String, PantryItem> pantry) {
        Cursor c = getReadableDatabase().query("recipe_ingredients", null, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        try {
            while (c.moveToNext()) {
                String requiredName = normalize(c.getString(c.getColumnIndexOrThrow("ingredient_name")));
                double requiredQty = c.getDouble(c.getColumnIndexOrThrow("required_quantity"));
                String requiredUnit = normalize(c.getString(c.getColumnIndexOrThrow("unit")));
                PantryItem available = pantry.get(requiredName);
                if (available == null) return false;
                if (!normalize(available.getUnit()).equals(requiredUnit)) return false;
                if (available.getQuantity() + 1e-9 < requiredQty) return false;
            }
            return true;
        } finally { c.close(); }
    }

    public List<RecipeIngredient> getIngredients(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("recipe_ingredients", null, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, "id ASC");
        try {
            while (c.moveToNext()) list.add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow("ingredient_name")),
                    c.getDouble(c.getColumnIndexOrThrow("required_quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit"))));
        } finally { c.close(); }
        return list;
    }

    private String normalize(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT); }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Cheese Omelette", "Beat the eggs. Cook in a pan, add cheese, fold and serve.",
                new String[][]{{"Eggs","2","pieces"},{"Cheese","50","g"}});
        addRecipe(db, "Pancakes", "Mix flour, milk and eggs. Cook small portions in a lightly greased pan.",
                new String[][]{{"Flour","150","g"},{"Milk","250","ml"},{"Eggs","1","pieces"}});
        addRecipe(db, "French Toast", "Dip bread in beaten egg and milk. Fry both sides until golden.",
                new String[][]{{"Bread","2","slices"},{"Eggs","1","pieces"},{"Milk","50","ml"}});
        addRecipe(db, "Vegetable Fried Rice", "Stir-fry vegetables, add cooked rice and season.",
                new String[][]{{"Rice","200","g"},{"Carrots","50","g"},{"Peas","50","g"}});
        addRecipe(db, "Tomato Pasta", "Cook pasta. Simmer tomatoes with garlic, combine and serve.",
                new String[][]{{"Pasta","200","g"},{"Tomatoes","150","g"},{"Garlic","10","g"}});
        addRecipe(db, "Tuna Sandwich", "Mix tuna with mayonnaise. Place between bread with lettuce.",
                new String[][]{{"Bread","2","slices"},{"Tuna","100","g"},{"Mayonnaise","30","g"}});
        addRecipe(db, "Chicken Pasta", "Cook pasta and chicken. Combine with sauce and heat through.",
                new String[][]{{"Pasta","200","g"},{"Chicken","150","g"},{"Tomato Sauce","100","g"}});
        addRecipe(db, "Scrambled Eggs on Toast", "Scramble eggs gently and serve on toasted bread.",
                new String[][]{{"Eggs","2","pieces"},{"Bread","2","slices"}});
        addRecipe(db, "Grilled Cheese Sandwich", "Fill bread with cheese and grill until crisp and melted.",
                new String[][]{{"Bread","2","slices"},{"Cheese","60","g"}});
        addRecipe(db, "Egg Fried Rice", "Stir-fry rice, add beaten eggs and cook until set.",
                new String[][]{{"Rice","200","g"},{"Eggs","2","pieces"}});
        addRecipe(db, "Garlic Pasta", "Cook pasta and toss with garlic and oil.",
                new String[][]{{"Pasta","200","g"},{"Garlic","10","g"},{"Olive Oil","20","ml"}});
        addRecipe(db, "Chicken Sandwich", "Cook chicken, slice it and place in bread with lettuce.",
                new String[][]{{"Bread","2","slices"},{"Chicken","120","g"},{"Lettuce","30","g"}});
        addRecipe(db, "Vegetable Omelette", "Beat eggs, add vegetables and cook until set.",
                new String[][]{{"Eggs","2","pieces"},{"Onions","30","g"},{"Peppers","30","g"}});
        addRecipe(db, "Banana Pancakes", "Mash banana and mix with flour, milk and egg. Cook in a pan.",
                new String[][]{{"Bananas","1","pieces"},{"Flour","100","g"},{"Milk","150","ml"},{"Eggs","1","pieces"}});
        addRecipe(db, "Potato Omelette", "Cook potatoes and combine with beaten eggs. Cook until firm.",
                new String[][]{{"Potatoes","150","g"},{"Eggs","2","pieces"}});
        addRecipe(db, "Tuna Pasta", "Cook pasta and mix with tuna and mayonnaise.",
                new String[][]{{"Pasta","200","g"},{"Tuna","100","g"},{"Mayonnaise","30","g"}});
        addRecipe(db, "Cheese Pasta", "Cook pasta and stir through cheese until melted.",
                new String[][]{{"Pasta","200","g"},{"Cheese","80","g"}});
        addRecipe(db, "Chicken Fried Rice", "Stir-fry chicken and vegetables, add cooked rice.",
                new String[][]{{"Rice","200","g"},{"Chicken","120","g"},{"Peas","50","g"}});
        addRecipe(db, "Vegetable Sandwich", "Layer vegetables in bread and serve fresh.",
                new String[][]{{"Bread","2","slices"},{"Lettuce","30","g"},{"Tomatoes","50","g"}});
        addRecipe(db, "Tomato Cheese Toast", "Toast bread with tomato and cheese until the cheese melts.",
                new String[][]{{"Bread","2","slices"},{"Tomatoes","50","g"},{"Cheese","50","g"}});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String[][] ingredients) {
        ContentValues r = new ContentValues();
        r.put("name", name); r.put("steps", steps);
        long id = db.insert("recipes", null, r);
        for (String[] x : ingredients) {
            ContentValues i = new ContentValues();
            i.put("recipe_id", id); i.put("ingredient_name", x[0]);
            i.put("required_quantity", Double.parseDouble(x[1])); i.put("unit", x[2]);
            db.insert("recipe_ingredients", null, i);
        }
    }
}
