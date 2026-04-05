package com.example.phase4.model;

public class Goal {
    public String name;
    public double current;
    public double target;

    public Goal(String name, double current, double target) {
        this.name = name;
        this.current = current;
        this.target = target;
    }

    public int getProgressPercent() {
        if (target <= 0) return 0;
        return Math.min((int) (current / target * 100), 100);
    }
}
