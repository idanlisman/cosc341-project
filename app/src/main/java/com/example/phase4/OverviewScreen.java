package com.example.phase4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.phase4.model.Category;
import com.example.phase4.model.DayExpense;

import java.util.List;

public class OverviewScreen extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView totalExpense = view.findViewById(R.id.total_expense_amount);
        totalExpense.setText(String.format("$ %,.0f", Storage.get().getTotalSpent()));

        TextView groceriesAmount = view.findViewById(R.id.groceries_amount);
        for (Category cat : Storage.get().getCategories()) {
            if (cat.name.equals("Groceries")) {
                groceriesAmount.setText(String.format("$ %,.0f", cat.spent));
                break;
            }
        }

        LinearLayout chartContainer = view.findViewById(R.id.chart_container);
        buildBarChart(chartContainer);
    }

    private void buildBarChart(LinearLayout container) {
        List<DayExpense> data = Storage.getWeeklyExpenses();

        double maxVal = 0;
        for (DayExpense d : data) {
            maxVal = Math.max(maxVal, Math.max(d.income, d.expense));
        }

        int maxBarHeight = (int) (170 * getResources().getDisplayMetrics().density);

        for (DayExpense day : data) {
            LinearLayout column = new LinearLayout(getContext());
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams colParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            colParams.setMargins(4, 0, 4, 0);
            column.setLayoutParams(colParams);

            LinearLayout barsRow = new LinearLayout(getContext());
            barsRow.setOrientation(LinearLayout.HORIZONTAL);
            barsRow.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams barsParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
            barsRow.setLayoutParams(barsParams);

            View incomeBar = new View(getContext());
            int incomeHeight = (int) (day.income / maxVal * maxBarHeight);
            LinearLayout.LayoutParams incomeParams = new LinearLayout.LayoutParams(0, incomeHeight, 1f);
            incomeParams.setMargins(1, 0, 1, 0);
            incomeBar.setLayoutParams(incomeParams);
            incomeBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.income_green));

            View expenseBar = new View(getContext());
            int expenseHeight = (int) (day.expense / maxVal * maxBarHeight);
            LinearLayout.LayoutParams expenseParams = new LinearLayout.LayoutParams(0, expenseHeight, 1f);
            expenseParams.setMargins(1, 0, 1, 0);
            expenseBar.setLayoutParams(expenseParams);
            expenseBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.expense_red));

            barsRow.addView(incomeBar);
            barsRow.addView(expenseBar);

            TextView label = new TextView(getContext());
            label.setText(day.day);
            label.setTextSize(11);
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
            label.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            labelParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
            label.setLayoutParams(labelParams);

            column.addView(barsRow);
            column.addView(label);
            container.addView(column);
        }
    }
}
