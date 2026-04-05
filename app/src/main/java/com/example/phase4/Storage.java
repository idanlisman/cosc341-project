package com.example.phase4;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.phase4.model.Category;
import com.example.phase4.model.DayExpense;
import com.example.phase4.model.Goal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static final String PREFS_NAME = "budtrack_data";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_GOALS = "goals";

    private static Storage instance;

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private List<Category> categories;
    private List<Goal> goals;

    private Storage(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        load();
    }

    public static Storage get(Context context) {
        if (instance == null) {
            instance = new Storage(context);
        }
        return instance;
    }

    public static Storage get() {
        return instance;
    }

    private void load() {
        String catJson = prefs.getString(KEY_CATEGORIES, null);
        String goalJson = prefs.getString(KEY_GOALS, null);

        if (catJson != null) {
            Type catType = new TypeToken<ArrayList<Category>>() {}.getType();
            categories = gson.fromJson(catJson, catType);
            resolveIcons();
        } else {
            categories = new ArrayList<>();
            categories.add(new Category("Groceries", R.drawable.ic_groceries, 400, 1500));
            categories.add(new Category("Housing", R.drawable.ic_housing, 2000, 3500));
            categories.add(new Category("Dining Out", R.drawable.ic_dining, 105, 500));
            categories.add(new Category("Entertainment", R.drawable.ic_entertainment, 230, 300));
            categories.add(new Category("Transport", R.drawable.ic_transport, 410, 500));
        }

        if (goalJson != null) {
            Type goalType = new TypeToken<ArrayList<Goal>>() {}.getType();
            goals = gson.fromJson(goalJson, goalType);
        } else {
            goals = new ArrayList<>();
            goals.add(new Goal("Vacation", 1890, 3000));
            goals.add(new Goal("New iPhone", 648, 1800));
        }

        save();
    }

    private void resolveIcons() {
        for (Category category : categories) {
            switch (category.name.toLowerCase()) {
                case "groceries": category.iconResId = R.drawable.ic_groceries; break;
                case "housing":   category.iconResId = R.drawable.ic_housing; break;
                case "dining out": category.iconResId = R.drawable.ic_dining; break;
                case "entertainment": category.iconResId = R.drawable.ic_entertainment; break;
                case "transport": category.iconResId = R.drawable.ic_transport; break;
                default: category.iconResId = R.drawable.ic_custom; break;
            }
        }
    }

    public void save() {
        prefs.edit().putString(KEY_CATEGORIES, gson.toJson(categories)).putString(KEY_GOALS, gson.toJson(goals)).apply();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public double getTotalSpent() {
        double total = 0;
        for (Category c : categories) total += c.spent;
        return total;
    }

    public double getTotalBudget() {
        double total = 0;
        for (Category c : categories) total += c.budget;
        return total;
    }

    public double getTotalSavings() {
        double total = 0;
        for (Goal g : goals) total += g.current;
        return total;
    }

    public static List<DayExpense> getWeeklyExpenses() {
        List<DayExpense> list = new ArrayList<>();
        list.add(new DayExpense("Mon", 3200, 2800));
        list.add(new DayExpense("Tue", 2900, 3100));
        list.add(new DayExpense("Wed", 3500, 2500));
        list.add(new DayExpense("Thu", 2800, 3400));
        list.add(new DayExpense("Fri", 3100, 2900));
        list.add(new DayExpense("Sat", 2600, 3600));
        list.add(new DayExpense("Sun", 3000, 2200));
        return list;
    }
}
