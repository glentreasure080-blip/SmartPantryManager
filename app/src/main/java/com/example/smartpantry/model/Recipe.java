package com.example.smartpantry.model;

public class Recipe {
    private int id;
    private String name;
    private String steps;

    public Recipe(int id, String name, String steps) {
        this.id = id; this.name = name; this.steps = steps;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
}
