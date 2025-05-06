package com.calorietracker.model;

public class CustomMeal extends Meal {

    private String userNotes;
    private Double fat, protein, carbs, sugars;

    public CustomMeal(String name, int kcal, String notes, Double fat, Double protein, Double carbs, Double sugars) {
        super(name, kcal);
        this.userNotes = notes;
        this.fat = fat;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
    }

    public CustomMeal(String name, int kcal, String notes) {
        this(name, kcal, notes, null, null, null, null);
    }

    public String getUserNotes() { return userNotes; }
    public void setUserNotes(String n){ userNotes = n; }

    public Double getFat() { return fat; }
    public Double getProtein() { return protein; }
    public Double getCarbs() { return carbs; }
    public Double getSugars() { return sugars; }
}