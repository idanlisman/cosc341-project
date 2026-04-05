package com.example.phase4;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.phase4.model.Category;
import com.example.phase4.model.Goal;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseScreen extends DialogFragment {

    private Spinner spinner;
    private EditText amountInput;
    private TextView selectionLabel;
    private int currentMode = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = view.findViewById(R.id.category_spinner);
        amountInput = view.findViewById(R.id.amount_input);
        selectionLabel = view.findViewById(R.id.selection_label);

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btn_submit).setOnClickListener(v -> submit());

        setupToggleTabs(view);
        updateSpinner();
    }

    private void setupToggleTabs(View view) {
        TextView toggleExpense = view.findViewById(R.id.toggle_expense);
        TextView toggleSaving = view.findViewById(R.id.toggle_saving);

        View.OnClickListener tabClick = v -> {
            toggleExpense.setBackground(null);
            toggleExpense.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
            toggleSaving.setBackground(null);
            toggleSaving.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));

            ((TextView) v).setBackgroundResource(R.drawable.bg_tab_selected);
            ((TextView) v).setTextColor(Color.WHITE);

            if (v == toggleExpense) {
                currentMode = 0;
                selectionLabel.setText("Select Category");
            } else {
                currentMode = 1;
                selectionLabel.setText("Select Goal");
            }
            amountInput.setText("");
            updateSpinner();
        };

        toggleExpense.setOnClickListener(tabClick);
        toggleSaving.setOnClickListener(tabClick);
    }

    private void updateSpinner() {
        List<String> names = new ArrayList<>();
        if (currentMode == 1) {
            for (Goal g : Storage.get().getGoals()) {
                names.add(g.name + " (" + g.getProgressPercent() + "%)");
            }
            if (names.isEmpty()) {
                names.add("No goals yet");
            }
        } else {
            for (Category c : Storage.get().getCategories()) {
                names.add(c.name);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, names);
        spinner.setAdapter(adapter);
    }

    private void submit() {
        String amountStr = amountInput.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = spinner.getSelectedItemPosition();

        if (currentMode == 1) {
            List<Goal> goals = Storage.get().getGoals();
            if (goals.isEmpty() || index < 0 || index >= goals.size()) {
                Toast.makeText(getContext(), "Please select a goal", Toast.LENGTH_SHORT).show();
                return;
            }
            Goal goal = goals.get(index);
            goal.current += amount;
            Storage.get().save();
            String msg = String.format("$%,.2f saved to %s!", amount, goal.name);
            if (goal.current >= goal.target) {
                msg = "Congratulations! " + goal.name + " goal reached!";
            }
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        } else {
            List<Category> categories = Storage.get().getCategories();
            if (index < 0 || index >= categories.size()) {
                Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            Category cat = categories.get(index);
            cat.spent += amount;
            Storage.get().save();
            String msg = String.format("$%,.2f expense added to %s", amount, cat.name);
            if (cat.spent > cat.budget) {
                msg += "\n\u26a0 Over budget!";
            } else if (cat.getProgressPercent() >= 80) {
                msg += "\n\u26a0 Approaching budget limit!";
            }
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        }

        dismiss();
    }
}
