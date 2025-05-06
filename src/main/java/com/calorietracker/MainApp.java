package com.calorietracker;

import com.calorietracker.dashboard.Dashboard;
import com.calorietracker.data.DataStore;
import com.calorietracker.model.MealHistory;
import com.calorietracker.model.User;
import com.calorietracker.ui.controllers.MainAppContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        com.calorietracker.data.DataStore.bootstrap();
        User user = com.calorietracker.data.DataStore.get().user();
        MealHistory history = com.calorietracker.data.DataStore.get().history();
        Dashboard dash = new Dashboard(history, user.getDailyCalorieGoal());
        MainAppContext.init(user, history, dash);
        FXMLLoader fx = new FXMLLoader(getClass().getResource("/com/calorietracker/view/MenuView.fxml"));
        Scene scene = new Scene(fx.load());
        MainAppContext.setScene(scene);
        stage.setScene(scene);
        stage.setTitle("Calorie Tracker");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}