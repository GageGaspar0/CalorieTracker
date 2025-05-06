package com.calorietracker.model;

public class PredefinedMeal extends Meal {

    private String source;
    private Double fat, protein, carbs, sugars;

    public PredefinedMeal(String name, int calorieCount, String source, Double fat, Double protein, Double carbs, Double sugars) {
        super(name, calorieCount);
        this.source = source;
        this.fat = fat;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String src) {
        this.source = src;
    }

    public Double getFat() { return fat; }
    public Double getProtein() { return protein; }
    public Double getCarbs() { return carbs; }
    public Double getSugars() { return sugars; }
}