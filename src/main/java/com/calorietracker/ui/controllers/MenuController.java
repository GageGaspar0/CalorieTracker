package com.calorietracker.ui.controllers;

import javafx.fxml.FXML;

public class MenuController {
    @FXML private void goAddMeal() { MainAppContext.swapScene("AddMealChoiceView.fxml"); }
    @FXML private void goHistory() { MainAppContext.swapScene("HistoryView.fxml"); }
    @FXML private void goDashboard() { MainAppContext.swapScene("DashboardView.fxml"); }
    @FXML private void goGoal() { MainAppContext.swapScene("GoalDialog.fxml", true); }
}