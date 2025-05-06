package com.calorietracker.data;

import com.calorietracker.model.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public final class DataStore {

    private static final Path JSON_FILE = Paths.get("src/main/resources/com/calorietracker/view/Data/stored-data.json");
    private static DataStore INSTANCE;
    private final User user;
    private final MealHistory history;
    private final Object lock = new Object();

    public static void bootstrap() throws IOException {
        if (INSTANCE == null) INSTANCE = new DataStore();
    }

    public static DataStore get() { return INSTANCE; }

    private DataStore() throws IOException {
        if (Files.notExists(JSON_FILE)) {
            Files.createDirectories(JSON_FILE.getParent());
            Files.writeString(JSON_FILE, "{\n  \"user\": { \"name\": \"Guest\", \"goal\": 0 },\n  \"entries\": []\n}", CREATE, TRUNCATE_EXISTING);
        }
        String json = Files.readString(JSON_FILE);
        String name = extract(json, "\"name\":\"", "\"");
        int goal = parseIntSafe(json, "\"goal\":");
        user = new User(name, goal);
        history = new MealHistory();
        String block = entriesBlock(json);
        if (!block.isBlank()) {
            for (String raw : block.split("\\},\\s*\\{")) {
                String it = stripBraces(raw.trim());
                LocalDateTime ts = LocalDateTime.parse(extract(it, "\"ts\":\"", "\""));
                double qty = Double.parseDouble(extract(it, "\"qty\":", ","));
                String kind = extract(it, "\"kind\":\"", "\"");
                Meal meal;
                if ("pre".equals(kind)) {
                    String mName = extract(it, "\"name\":\"", "\"");
                    int cal = Integer.parseInt(extract(it, "\"cal\":", ","));
                    String src = extract(it, "\"src\":\"", "\"");
                    Double fat = parseDbl(it, "\"fat\":");
                    Double pro = parseDbl(it, "\"pro\":");
                    Double carb = parseDbl(it, "\"carb\":");
                    Double sug = parseDbl(it, "\"sugar\":");
                    meal = new PredefinedMeal(mName, cal, src, fat, pro, carb, sug);
                } else {
                    String mName = extract(it, "\"name\":\"", "\"");
                    int cal = Integer.parseInt(extract(it, "\"cal\":", ","));
                    String note = extract(it, "\"note\":\"", "\"");
                    Double fat = parseDbl(it, "\"fat\":");
                    Double pro = parseDbl(it, "\"pro\":");
                    Double carb = parseDbl(it, "\"carb\":");
                    Double sug = parseDbl(it, "\"sugar\":");
                    meal = new CustomMeal(mName, cal, note, fat, pro, carb, sug);
                }
                history.addMealEntry(new MealEntry(ts, meal, qty));
            }
        }
    }

    public User user() { return user; }
    public MealHistory history() { return history; }

    public void addEntry(MealEntry e) {
        synchronized (lock) {
            history.addMealEntry(e);
            flush();
        }
    }

    public void setDailyGoal(int goal) {
        synchronized (lock) {
            user.setDailyCalorieGoal(goal);
            flush();
        }
    }

    private void flush() {
        try { Files.writeString(JSON_FILE, toJson(), CREATE, TRUNCATE_EXISTING); }
        catch (IOException ex) { ex.printStackTrace(); }
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("{\n  \"user\": { ")
                .append("\"name\":\"").append(esc(user.getUserName())).append("\", ")
                .append("\"goal\":").append(user.getDailyCalorieGoal())
                .append(" },\n  \"entries\": [\n");
        List<MealEntry> list = history.getMealEntries();
        for (int i = 0; i < list.size(); i++) {
            MealEntry me = list.get(i);
            sb.append("    { \"ts\":\"").append(me.getDateConsumed())
                    .append("\", \"qty\":").append(me.getQuantity()).append(", ");
            appendMealJson(sb, me.getMeal()).append(" }");
            if (i < list.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ]\n}");
        return sb.toString();
    }

    private StringBuilder appendMealJson(StringBuilder sb, Meal m) {
        if (m instanceof PredefinedMeal p) {
            sb.append("\"meal\":{")
                    .append("\"kind\":\"pre\",")
                    .append("\"name\":\"").append(esc(p.getName())).append("\",")
                    .append("\"cal\":").append(p.getCalorieCount()).append(",")
                    .append("\"src\":\"").append(esc(p.getSource())).append("\",")
                    .append("\"fat\":").append(asNum(p.getFat())).append(",")
                    .append("\"pro\":").append(asNum(p.getProtein())).append(",")
                    .append("\"carb\":").append(asNum(p.getCarbs())).append(",")
                    .append("\"sugar\":").append(asNum(p.getSugars()))
                    .append("}");
        } else {
            CustomMeal c = (CustomMeal) m;
            sb.append("\"meal\":{")
                    .append("\"kind\":\"custom\",")
                    .append("\"name\":\"").append(esc(c.getName())).append("\",")
                    .append("\"cal\":").append(c.getCalorieCount()).append(",")
                    .append("\"note\":\"").append(esc(c.getUserNotes())).append("\",")
                    .append("\"fat\":").append(asNum(c.getFat())).append(",")
                    .append("\"pro\":").append(asNum(c.getProtein())).append(",")
                    .append("\"carb\":").append(asNum(c.getCarbs())).append(",")
                    .append("\"sugar\":").append(asNum(c.getSugars()))
                    .append("}");
        }
        return sb;
    }

    private static String esc(String s){ return s.replace("\"","\\\""); }
    private static String stripBraces(String s){
        return (s.startsWith("{") && s.endsWith("}")) ? s.substring(1, s.length()-1) : s;
    }
    private static String extract(String src,String after,String until){
        int i=src.indexOf(after); if(i<0)return "";
        int j=src.indexOf(until,i+after.length()); if(j<0)return "";
        return src.substring(i+after.length(),j);
    }
    private static Double parseDbl(String src,String key){
        int i=src.indexOf(key); if(i<0)return null;
        i+=key.length();
        int jComma=src.indexOf(',',i), jBrace=src.indexOf('}',i);
        int j=(jComma<0)?jBrace:(jBrace<0?jComma:Math.min(jComma,jBrace));
        String sub=(j<0?src.substring(i):src.substring(i,j)).replace("\"","").trim();
        return sub.isEmpty()||"null".equals(sub)?null:Double.parseDouble(sub);
    }
    private static String asNum(Double d){ return d==null?"null":d.toString(); }
    private static String entriesBlock(String src){
        int key=src.indexOf("\"entries\"");
        if(key<0)return "";
        int open=src.indexOf('[',key);
        if(open<0)return "";
        int level=0,i=open;
        while(++i<src.length()){
            char c=src.charAt(i);
            if(c=='[') level++;
            else if(c==']'){ if(level==0) break; level--; }
        }
        return (i>=src.length())?"":src.substring(open+1,i).trim();
    }
    private static int parseIntSafe(String src,String key){
        int i=src.indexOf(key);
        if(i<0)return 0;
        i+=key.length();
        StringBuilder d=new StringBuilder();
        while(i<src.length()){
            char c=src.charAt(i++);
            if(c==','||c=='}') break;
            d.append(c);
        }
        String s=d.toString().trim();
        return s.isEmpty()?0:Integer.parseInt(s);
    }

}