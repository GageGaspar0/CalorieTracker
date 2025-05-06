package com.calorietracker.model;

public abstract class Meal {

    private String name;
    private int calorieCount;

    public Meal(String name, int calorieCount) {
        this.name = name;
        this.calorieCount = calorieCount;
    }

    public String getName() {
        return name;
    }

    public int getCalorieCount() {
        return calorieCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalorieCount(int cal) {
        this.calorieCount = cal;
    }

    @Override
    public String toString() {
        return name + " (" + calorieCount + " kcal)";
    }
}