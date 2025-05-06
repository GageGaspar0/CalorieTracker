package com.calorietracker.data;

import com.calorietracker.model.PredefinedMeal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FoodLoader {

    private static final String XLSX_PATH = "/com/calorietracker/view/Data/FoodListSource.xlsx";
    private static List<PredefinedMeal> cached;

    public static List<PredefinedMeal> loadFoods() {
        if (cached != null) return cached;   // already loaded

        List<PredefinedMeal> list = new ArrayList<>();
        try (InputStream is = FoodLoader.class.getResourceAsStream(XLSX_PATH);
             XSSFWorkbook wb = new XSSFWorkbook(is)) {

            wb.getSheetAt(0).iterator().forEachRemaining(row -> {
                if (row.getRowNum() == 0) return;  // skip header
                list.add(rowToMeal(row));
            });
            cached = List.copyOf(list);
            return cached;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private static PredefinedMeal rowToMeal(Row r) {
        String name = r.getCell(1).getStringCellValue();
        String foodGroup = r.getCell(2).getStringCellValue();
        int calories = getNumeric(r, 3).intValue();
        Double fat = getNumeric(r, 4);
        Double protein = getNumeric(r, 5);
        Double carbs = getNumeric(r, 6);
        Double sugars = getNumeric(r, 7);
        return new PredefinedMeal(name, calories, foodGroup, fat, protein, carbs, sugars);
    }

    private static Double getNumeric(Row r, int idx) {
        Cell c = r.getCell(idx);
        if (c == null) return null;

        return switch (c.getCellType()) {
            case NUMERIC -> c.getNumericCellValue();
            case STRING -> {
                String s = c.getStringCellValue().trim();
                if (s.isEmpty() || s.equalsIgnoreCase("NULL")) yield null;
                try { yield Double.parseDouble(s); }
                catch (NumberFormatException ex) { yield null; }
            }
            default -> null;
        };
    }
}