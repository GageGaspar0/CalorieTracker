package com.calorietracker.dashboard;

import com.calorietracker.model.CustomMeal;
import com.calorietracker.model.MealEntry;
import com.calorietracker.model.MealHistory;
import com.calorietracker.model.PredefinedMeal;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class Dashboard {

    private final MealHistory history;
    private int dailyGoal;

    public Dashboard(MealHistory history, int dailyCalorieGoal) {
        this.history = history;
        this.dailyGoal = dailyCalorieGoal;
    }

    public void setDailyGoal(int goal) {
        this.dailyGoal = goal;
    }

    public double getAverageCaloriesPerDay() {
        if (history.getMealEntries().isEmpty()) return 0;

        LocalDate first = history.getMealEntries().stream()
                .map(e -> e.getDateConsumed().toLocalDate())
                .min(LocalDate::compareTo).orElse(LocalDate.now());

        long days = ChronoUnit.DAYS.between(first, LocalDate.now()) + 1;
        int total = history.getMealEntries().stream().mapToInt(MealEntry::getCalories).sum();
        return (double) total / days;
    }

    public double getAverageCaloriesPerWeek() {
        if (history.getMealEntries().isEmpty()) return 0;

        LocalDate first = history.getMealEntries().stream()
                .map(e -> e.getDateConsumed().toLocalDate())
                .min(LocalDate::compareTo).orElse(LocalDate.now());

        long weeks = ChronoUnit.WEEKS.between(first, LocalDate.now()) + 1;
        return (getAverageCaloriesPerDay() * 7) / weeks;
    }

    public String getConsumptionTrend(int days) {
        LocalDate today = LocalDate.now();

        List<MealEntry> recent = history.getMealEntries().stream()
                .filter(e -> !e.getDateConsumed().toLocalDate().isBefore(today.minusDays(days)))
                .toList();

        List<MealEntry> past = history.getMealEntries().stream()
                .filter(e -> e.getDateConsumed().toLocalDate().isBefore(today.minusDays(days))
                        && !e.getDateConsumed().toLocalDate().isBefore(today.minusDays(days * 2L)))
                .toList();

        double recentAvg = recent.stream().mapToInt(MealEntry::getCalories).average().orElse(0);
        double pastAvg = past.stream().mapToInt(MealEntry::getCalories).average().orElse(0);

        if (recentAvg > pastAvg) return "↑ Increasing";
        if (recentAvg < pastAvg) return "↓ Decreasing";
        return "→ Stable";
    }

    public Map<DayOfWeek, Double> getAveragesByDayOfWeek() {
        Map<DayOfWeek, Double> map = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            double avg = history.getMealEntries().stream()
                    .filter(e -> e.getDateConsumed().getDayOfWeek() == d)
                    .mapToInt(MealEntry::getCalories)
                    .average().orElse(0);
            map.put(d, avg);
        }
        return map;
    }

    public int getOverGoalDays() {
        if (dailyGoal <= 0) return 0;

        return (int) history.getMealEntries().stream()
                .collect(Collectors.groupingBy(e -> e.getDateConsumed().toLocalDate(),
                        Collectors.summingInt(MealEntry::getCalories)))
                .values().stream().filter(total -> total > dailyGoal).count();
    }

    public double getAvgGoalOveragesPerWeek() {
        if (dailyGoal <= 0 || history.getMealEntries().isEmpty()) return 0;

        Map<YearWeek, Long> overPerWeek = history.getMealEntries().stream()
                .collect(Collectors.groupingBy(e -> YearWeek.from(e.getDateConsumed().toLocalDate()),
                        Collectors.collectingAndThen(Collectors.groupingBy(x -> x.getDateConsumed().toLocalDate(),
                                        Collectors.summingInt(MealEntry::getCalories)),
                                dayMap -> dayMap.values().stream().filter(total -> total > dailyGoal).count())));

        return overPerWeek.values().stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public String getWeekendDelta() {
        double weekend = avgCalories(d -> d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY);
        double weekday = avgCalories(d -> d.getDayOfWeek().getValue() < 6);

        if (weekday == 0) return "N/A";
        double pct = (weekend - weekday) / weekday * 100;
        return String.format("%+.0f%% %s on weekends", pct, pct >= 0 ? "more" : "less");
    }

    public List<TopMeal> getTopMeals() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfThisMonth = today.withDayOfMonth(1);
        LocalDate firstOfLastMonth = firstOfThisMonth.minusMonths(1);
        LocalDate threeMonthsAgo = firstOfThisMonth.minusMonths(3);

        Map<String, long[]> counts = new HashMap<>();
        for (MealEntry e : history.getMealEntries()) {
            String name = e.getMeal().getName();
            long[] arr = counts.computeIfAbsent(name, k -> new long[3]);
            LocalDate d = e.getDateConsumed().toLocalDate();

            if (!d.isBefore(firstOfThisMonth)) arr[0]++;
            else if (!d.isBefore(firstOfLastMonth)) arr[1]++;
            if (!d.isBefore(threeMonthsAgo)) arr[2]++;
        }

        return counts.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String,long[]>>comparingLong(x -> x.getValue()[2]).reversed())
                .limit(5)
                .map(e -> new TopMeal(e.getKey(), e.getValue()))
                .toList();
    }

    public SugarSource getTopSugarSource() {
        Map<String, Double> sugarTotals = new HashMap<>();

        history.getMealEntries().forEach(e -> {
            double g = 0;
            if (e.getMeal() instanceof PredefinedMeal p && p.getSugars() != null) g = p.getSugars();
            sugarTotals.merge(e.getMeal().getName(), g * e.getQuantity(), Double::sum);
        });

        return sugarTotals.isEmpty() ? null : sugarTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new SugarSource(e.getKey(), e.getValue()))
                .orElse(null);
    }

    public int getDaysOverGoal() {
        if (dailyGoal <= 0) return 0;

        return (int) history.getMealEntries().stream()
                .collect(Collectors.groupingBy(e -> e.getDateConsumed().toLocalDate(),
                        Collectors.summingInt(MealEntry::getCalories)))
                .values().stream().filter(total -> total > dailyGoal).count();
    }

    public String getFavoriteFoodGroup() {
        return history.getMealEntries().stream()
                .filter(e -> e.getMeal() instanceof PredefinedMeal)
                .map(e -> ((PredefinedMeal) e.getMeal()).getSource())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private double avgCalories(java.util.function.Predicate<LocalDate> pred) {
        return history.getMealEntries().stream()
                .filter(e -> pred.test(e.getDateConsumed().toLocalDate()))
                .mapToInt(MealEntry::getCalories)
                .average().orElse(0);
    }

    private int totalCalories(java.util.function.Predicate<LocalTime> pred) {
        return history.getMealEntries().stream()
                .filter(e -> pred.test(e.getDateConsumed().toLocalTime()))
                .mapToInt(MealEntry::getCalories).sum();
    }

    private static int pctDiff(int bigger, int smaller) {
        return (int)Math.round((bigger - smaller) * 100.0 / smaller);
    }

    public record SourceCount(String name, int times) {}

    public SourceCount getTopSource(String field) {
        if (history.getMealEntries().isEmpty()) return null;

        Map<String, Double> totals = new HashMap<>();
        Map<String, Integer> times = new HashMap<>();

        history.getMealEntries().forEach(e -> {
            double amount = switch (field) {
                case "cal" -> e.getMeal().getCalorieCount();
                case "fat" -> getMacro(e, Macro.FAT);
                case "pro" -> getMacro(e, Macro.PRO);
                case "carb" -> getMacro(e, Macro.CARB);
                case "sugar" -> getMacro(e, Macro.SUGAR);
                default -> 0;
            };
            totals.merge(e.getMeal().getName(), amount * e.getQuantity(), Double::sum);
            times.merge(e.getMeal().getName(), 1, Integer::sum);
        });

        return totals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new SourceCount(e.getKey(), times.get(e.getKey())))
                .orElse(null);
    }

    private enum Macro { FAT, PRO, CARB, SUGAR }

    private double getMacro(MealEntry e, Macro m) {
        if (e.getMeal() instanceof PredefinedMeal p) {
            return switch (m) {
                case FAT -> orZero(p.getFat());
                case PRO -> orZero(p.getProtein());
                case CARB -> orZero(p.getCarbs());
                case SUGAR -> orZero(p.getSugars());
            };
        }
        if (e.getMeal() instanceof CustomMeal c) {
            return switch (m) {
                case FAT -> orZero(c.getFat());
                case PRO -> orZero(c.getProtein());
                case CARB -> orZero(c.getCarbs());
                case SUGAR -> orZero(c.getSugars());
            };
        }
        return 0;
    }

    private static double orZero(Double d) { return d == null ? 0 : d; }

    public record TopMeal(String name, long[] counts) {}

    public record SugarSource(String name, double grams) {}

    private record YearWeek(int year, int week) {
        static YearWeek from(LocalDate d) {
            WeekFields wf = WeekFields.ISO;
            return new YearWeek(d.get(wf.weekBasedYear()), d.get(wf.weekOfWeekBasedYear()));
        }
    }
}