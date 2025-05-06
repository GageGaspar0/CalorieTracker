package com.calorietracker.model;

import java.time.LocalDateTime;

public class MealEntry {

    private final LocalDateTime dateConsumed;
    private final Meal meal;
    private final double quantity;

    public MealEntry(LocalDateTime dateConsumed, Meal meal, double quantity) {
        this.dateConsumed = dateConsumed;
        this.meal = meal;
        this.quantity = quantity;
    }

    public LocalDateTime getDateConsumed() { return dateConsumed; }
    public Meal getMeal() { return meal; }
    public double getQuantity() { return quantity; }

    public int getCalories() {
        return (int) Math.round(meal.getCalorieCount() * quantity);
    }

    @Override public String toString() {
        return "%s x %.2f (%d kcal)".formatted(meal.getName(), quantity, getCalories());
    }
}