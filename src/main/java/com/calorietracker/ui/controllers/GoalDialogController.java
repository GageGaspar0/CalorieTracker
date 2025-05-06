package com.calorietracker.ui.controllers;

import com.calorietracker.data.DataStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GoalDialogController {
    @FXML private DialogPane dialogPane;
    @FXML private TextField goalField;
    @FXML public void initialize() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setDialogPane(dialogPane);
        dlg.setTitle("Set Calorie Goal");
        dlg.showAndWait().ifPresent(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    int goal = Integer.parseInt(goalField.getText().trim());
                    DataStore.get().setDailyGoal(goal);
                    MainAppContext.getDashboard().setDailyGoal(goal);
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Please enter a number").showAndWait();
                }
            }
        });
    }
}
