package com.calorietracker.model;

public class User {

    private String userName;
    private int dailyCalorieGoal;

    public User(String userName, int dailyCalorieGoal) {
        this.userName = userName;
        this.dailyCalorieGoal = dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(int goal) {
        this.dailyCalorieGoal = goal;
    }

    public int getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }
}