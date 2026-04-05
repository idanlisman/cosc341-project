package com.example.phase4;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView tabOverview, tabBudget, tabGoals;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Storage.get(this);
        setContentView(R.layout.activity_main);

        tabOverview = findViewById(R.id.tab_overview);
        tabBudget = findViewById(R.id.tab_budget);
        tabGoals = findViewById(R.id.tab_goals);
        tabOverview.setOnClickListener(v -> selectTab(0));
        tabBudget.setOnClickListener(v -> selectTab(1));
        tabGoals.setOnClickListener(v -> selectTab(2));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectTab(currentTab);
                return true;
            } else if (id == R.id.nav_add) {
                AddExpenseScreen dialog = new AddExpenseScreen();
                dialog.show(getSupportFragmentManager(), "add_expense");
                getSupportFragmentManager().registerFragmentLifecycleCallbacks(
                        new FragmentManager.FragmentLifecycleCallbacks() {
                            @Override
                            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                                if (f == dialog) {
                                    selectTab(currentTab); // Refresh to show updated data
                                    fm.unregisterFragmentLifecycleCallbacks(this);
                                }
                            }
                        }, false);
                return false;
            }
            return false;
        });

        if (savedInstanceState == null) {
            selectTab(0);
        }
    }

    private void selectTab(int tab) {
        currentTab = tab;
        tabOverview.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabOverview.setTextColor(getResources().getColor(R.color.text_primary));
        tabBudget.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabBudget.setTextColor(getResources().getColor(R.color.text_primary));
        tabGoals.setBackgroundResource(R.drawable.bg_tab_unselected);
        tabGoals.setTextColor(getResources().getColor(R.color.text_primary));

        Fragment fragment;
        switch (tab) {
            case 1:
                tabBudget.setBackgroundResource(R.drawable.bg_tab_selected);
                tabBudget.setTextColor(Color.WHITE);
                fragment = new BudgetScreen();
                break;
            case 2:
                tabGoals.setBackgroundResource(R.drawable.bg_tab_selected);
                tabGoals.setTextColor(Color.WHITE);
                fragment = new GoalsScreen();
                break;
            default:
                tabOverview.setBackgroundResource(R.drawable.bg_tab_selected);
                tabOverview.setTextColor(Color.WHITE);
                fragment = new OverviewScreen();
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
