package com.example.phase4.model;

public class Category {
    public String name;
    public transient int iconResId;
    public double spent;
    public double budget;

    public Category(String name, int iconResId, double spent, double budget) {
        this.name = name;
        this.iconResId = iconResId;
        this.spent = spent;
        this.budget = budget;
    }

    public int getProgressPercent() {
        if (budget <= 0) return 0;
        return Math.min((int) (spent / budget * 100), 100);
    }
}
