package com.calorietracker.ui.controllers;

import com.calorietracker.dashboard.Dashboard;
import com.calorietracker.dashboard.Dashboard.SugarSource;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DashboardController {

    @FXML private Label goalLbl, avgDayLbl, avgWeekLbl, trendLbl;
    @FXML private Label overLimitLbl, overGoalWeeklyLbl, weekendLbl, morningLbl, sugarLbl;
    @FXML private Label calLbl, fatLbl, proLbl, carbLbl;
    @FXML private Label favGroupLbl;
    @FXML private ListView<String> topMealsList;

    private final Dashboard board = MainAppContext.getDashboard();

    @FXML public void initialize() { refresh(); }

    @FXML public void refresh() {

        int goal = MainAppContext.getUser().getDailyCalorieGoal();
        goalLbl.setText(goal > 0 ? goal + " kcal" : "Not set");

        avgDayLbl.setText((int) board.getAverageCaloriesPerDay() + " kcal");
        avgWeekLbl.setText((int) board.getAverageCaloriesPerWeek() + " kcal");
        trendLbl.setText(board.getConsumptionTrend(14));
        overLimitLbl.setText(String.valueOf(board.getOverGoalDays()));
        overLimitLbl.setText(String.valueOf(board.getOverGoalDays()));
        overGoalWeeklyLbl.setText(String.format("%.1f days / wk", board.getAvgGoalOveragesPerWeek()));
        favGroupLbl.setText(board.getFavoriteFoodGroup());

        weekendLbl.setText(board.getWeekendDelta());

        SugarSource src = board.getTopSugarSource();
        sugarLbl.setText(src == null ? "N/A" : String.format("%s (%.0f g)", src.name(), src.grams()));

        topMealsList.getItems().setAll(board.getTopMeals().stream()
                .map(t -> String.format("%s — %d / %d / %d", t.name(), t.counts()[0], t.counts()[1], t.counts()[2]))
                .toList());

        setSourceLabel(calLbl, board.getTopSource("cal"));
        setSourceLabel(fatLbl, board.getTopSource("fat"));
        setSourceLabel(proLbl, board.getTopSource("pro"));
        setSourceLabel(carbLbl, board.getTopSource("carb"));
        setSourceLabel(sugarLbl, board.getTopSource("sugar"));
    }

    private void setSourceLabel(Label lbl, Dashboard.SourceCount sc) {
        lbl.setText(sc == null ? "N/A" : String.format("%s  (%d×)", sc.name(), sc.times()));
    }

    @FXML void back() { MainAppContext.swapScene("MenuView.fxml"); }
}