package com.calorietracker.ui.controllers;

import com.calorietracker.data.DataStore;
import com.calorietracker.model.CustomMeal;
import com.calorietracker.model.MealEntry;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

public class CustomMealDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private TextField nameField, calField;
    @FXML private TextField fatField, proField, carbField, sugarField;
    @FXML private TextArea notesArea;
    @FXML private Spinner<Double> qtySpinner;

    @FXML public void initialize() {
        qtySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.25, 20, 1, 0.25));
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setDialogPane(dialogPane);
        dlg.setTitle("Add Custom Meal");
        dlg.setResultConverter(b -> b.getButtonData() == ButtonBar.ButtonData.OK_DONE ? b : null);
        dlg.showAndWait().ifPresent(btn -> save());
    }

    private void save() {
        try {
            String name = nameField.getText().trim();
            int kcal = Integer.parseInt(calField.getText().trim());
            double qty = qtySpinner.getValue();
            String note = notesArea.getText().trim();
            Double fat = parseOptional(fatField.getText());
            Double pro = parseOptional(proField.getText());
            Double carb = parseOptional(carbField.getText());
            Double sug = parseOptional(sugarField.getText());
            CustomMeal cm = new CustomMeal(name, kcal, note, fat, pro, carb, sug);
            MealEntry entry = new MealEntry(LocalDateTime.now(), cm, qty);
            DataStore.get().addEntry(entry);
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "calories/macro must be int, double or leave blank").showAndWait();
        }
    }

    private static Double parseOptional(String txt) {
        txt = txt.trim();
        return txt.isEmpty() ? null : Double.valueOf(txt);
    }
}