package com.calorietracker.ui.controllers;

import javafx.fxml.FXML;

public class AddMealChoiceController {
    @FXML
    private void fromList() {
        MainAppContext.swapScene("AddMealView.fxml");
    }

    @FXML
    private void custom() {
        MainAppContext.swapScene("CustomMealDialog.fxml", true);
    }

    @FXML
    private void back() {
        MainAppContext.swapScene("MenuView.fxml");
    }
}