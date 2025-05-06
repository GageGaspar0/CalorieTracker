package com.calorietracker.ui.controllers;

import com.calorietracker.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;

public class HistoryController {
    @FXML private DatePicker datePicker;
    @FXML private TableView<MealEntry> table;
    @FXML private TableColumn<MealEntry,String> dateCol, mealCol, groupCol, calCol, fatCol, proCol, carbCol, sugarCol;
    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @FXML public void initialize() {
        dateCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDateConsumed().toLocalDate().format(DF)));
        mealCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMeal().getName()));
        groupCol.setCellValueFactory(cd -> {
            Meal m = cd.getValue().getMeal();
            return new SimpleStringProperty((m instanceof PredefinedMeal p) ? p.getSource() : "Custom");
        });
        calCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getMeal().getCalorieCount())));
        fatCol.setCellValueFactory(cd -> new SimpleStringProperty(fmtMacro(getFat(cd))));
        proCol.setCellValueFactory(cd -> new SimpleStringProperty(fmtMacro(getPro(cd))));
        carbCol.setCellValueFactory(cd -> new SimpleStringProperty(fmtMacro(getCarb(cd))));
        sugarCol.setCellValueFactory(cd -> new SimpleStringProperty(fmtMacro(getSug(cd))));
        table.setItems(FXCollections.observableArrayList(MainAppContext.getHistory().getMealEntries()));
        datePicker.valueProperty().addListener((obs, o, n) ->
                table.setItems(FXCollections.observableArrayList(n == null
                        ? MainAppContext.getHistory().getMealEntries()
                        : MainAppContext.getHistory().getMealsByDate(n))));
    }
    private static Double getFat(TableColumn.CellDataFeatures<MealEntry,String> cd){
        Meal m = cd.getValue().getMeal();
        return (m instanceof PredefinedMeal p) ? p.getFat() : (m instanceof CustomMeal c) ? c.getFat() : null;
    }
    private static Double getPro(TableColumn.CellDataFeatures<MealEntry,String> cd){
        Meal m = cd.getValue().getMeal();
        return (m instanceof PredefinedMeal p) ? p.getProtein() : (m instanceof CustomMeal c) ? c.getProtein() : null;
    }
    private static Double getCarb(TableColumn.CellDataFeatures<MealEntry,String> cd){
        Meal m = cd.getValue().getMeal();
        return (m instanceof PredefinedMeal p) ? p.getCarbs() : (m instanceof CustomMeal c) ? c.getCarbs() : null;
    }
    private static Double getSug(TableColumn.CellDataFeatures<MealEntry,String> cd){
        Meal m = cd.getValue().getMeal();
        return (m instanceof PredefinedMeal p) ? p.getSugars() : (m instanceof CustomMeal c) ? c.getSugars() : null;
    }
    private static String fmtMacro(Double d){
        return d == null ? "-" : (d % 1 == 0 ? String.valueOf(d.intValue()) : String.format("%.1f", d));
    }
    @FXML private void back(){ MainAppContext.swapScene("MenuView.fxml"); }
}