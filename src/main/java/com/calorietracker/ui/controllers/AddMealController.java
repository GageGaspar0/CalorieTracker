package com.calorietracker.ui.controllers;

import com.calorietracker.data.DataStore;
import com.calorietracker.data.FoodLoader;
import com.calorietracker.model.MealEntry;
import com.calorietracker.model.MealHistory;
import com.calorietracker.model.PredefinedMeal;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddMealController {

    @FXML private TextField foodSearchField;
    @FXML private ComboBox<String> groupFilterBox;
    @FXML private Spinner<Double> qtySpinner;
    @FXML private ListView<MealEntry> todayListView;

    private List<PredefinedMeal> foods;
    private final MealHistory history = MainAppContext.getHistory();
    private AutoCompletionBinding<PredefinedMeal> auto;
    private PredefinedMeal lastSelected = null;

    @FXML
    public void initialize() {
        foods = FoodLoader.loadFoods();
        Set<String> groups = foods.stream().map(PredefinedMeal::getSource).collect(Collectors.toSet());
        groupFilterBox.getItems().add("All Groups");
        groupFilterBox.getItems().addAll(groups);
        groupFilterBox.getSelectionModel().selectFirst();
        qtySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.25, 20, 1, 0.25));
        auto = TextFields.bindAutoCompletion(
                foodSearchField,
                request -> foods.stream()
                        .filter(this::filterByGroup)
                        .filter(m -> m.getName().toLowerCase().contains(request.getUserText().toLowerCase()))
                        .limit(10)
                        .collect(Collectors.toList()),
                new StringConverter<>() {
                    @Override public String toString(PredefinedMeal m) { return m.getName(); }
                    @Override public PredefinedMeal fromString(String s) { return null; }
                });
        auto.setOnAutoCompleted(evt -> lastSelected = evt.getCompletion());
        refreshToday();
    }

    private boolean filterByGroup(PredefinedMeal m) {
        String sel = groupFilterBox.getValue();
        return sel == null || sel.equals("All Groups") || m.getSource().equals(sel);
    }

    @FXML
    private void onAddSelected() {
        PredefinedMeal selected = lastSelected;
        if (selected == null) return;
        double qty = qtySpinner.getValue();
        MealEntry entry = new MealEntry(LocalDateTime.now(), selected, qty);
        DataStore.get().addEntry(entry);
        lastSelected = null;
        foodSearchField.clear();
        qtySpinner.getValueFactory().setValue(1d);
        refreshToday();
    }

    @FXML
    private void onBack() { MainAppContext.swapScene("AddMealChoiceView.fxml"); }

    private void refreshToday() {
        todayListView.getItems().setAll(history.getMealsByDate(LocalDateTime.now().toLocalDate()));
    }

    @FXML
    private void back() { MainAppContext.swapScene("MenuView.fxml"); }
}