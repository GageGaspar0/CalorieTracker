package com.calorietracker.ui.controllers;

import com.calorietracker.dashboard.Dashboard;
import com.calorietracker.model.MealHistory;
import com.calorietracker.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public final class MainAppContext {

    private static User user;
    private static MealHistory history;
    private static Dashboard dash;
    private static Scene scene;

    public static void setScene(Scene s) { scene = s; }
    public static Scene getScene() { return scene; }

    public static void init(User u, MealHistory h, Dashboard d) {
        user = u;
        history = h;
        dash = d;
    }

    public static User getUser() { return user; }
    public static MealHistory getHistory() { return history; }
    public static Dashboard getDashboard() { return dash; }

    public static void swapScene(String fxml) { swapScene(fxml, false); }
    public static void swapScene(String fxml, boolean dialog) {
        try {
            URL url = MainAppContext.class.getResource("/com/calorietracker/view/" + fxml);
            if (url == null) {
                System.err.println("FXML not found: " + fxml);
                return;
            }
            FXMLLoader fx = new FXMLLoader(url);
            if (dialog) {
                fx.load();
            } else {
                Stage stage = (Stage) scene.getWindow();
                if (stage == null) {
                    System.err.println("Stage is null");
                    return;
                }
                Scene newScene = new Scene(fx.load());
                stage.setScene(newScene);
                scene = newScene;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}