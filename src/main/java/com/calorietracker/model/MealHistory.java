package com.calorietracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MealHistory {

    private final List<MealEntry> entries = new ArrayList<>();

    public void addMealEntry(MealEntry entry) {
        entries.add(entry);
    }

    public List<MealEntry> getMealEntries() {
        return List.copyOf(entries);
    }

    public List<MealEntry> getMealsByDate(LocalDate date) {
        return entries.stream()
                .filter(e -> e.getDateConsumed().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
}