package com.example.smartestbite;

public class Meal {
    private String title;
    private String recipe;

    public Meal() {
        // Default constructor required for Firestore
    }

    public Meal(String title, String recipe) {
        this.title = title;
        this.recipe = recipe;
    }

    public String getTitle() {
        return title;
    }

    public String getRecipe() {
        return recipe;
    }
}
