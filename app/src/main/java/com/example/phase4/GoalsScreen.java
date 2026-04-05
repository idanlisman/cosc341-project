package com.example.phase4;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phase4.adapter.GoalAdapter;
import com.example.phase4.model.Goal;

import java.util.List;

public class GoalsScreen extends Fragment {

    private List<Goal> goals;
    private GoalAdapter adapter;
    private TextView savingsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savingsText = view.findViewById(R.id.savings_amount);
        RecyclerView goalsList = view.findViewById(R.id.goals_list);
        goalsList.setLayoutManager(new LinearLayoutManager(getContext()));
        goals = Storage.get().getGoals();
        adapter = new GoalAdapter(goals);
        goalsList.setAdapter(adapter);
        adapter.setOnGoalClickListener((goal, position) -> showAddSavingsDialog(goal, position));
        view.findViewById(R.id.btn_add_goal).setOnClickListener(v -> showAddGoalDialog());

        updateSavings();
    }

    private void updateSavings() {
        double total = Storage.get().getTotalSavings();
        savingsText.setText(String.format("$ %,.0f", total));
    }

    private void showAddSavingsDialog(Goal goal, int position) {
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Amount to save");
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        double remaining = goal.target - goal.current;

        new AlertDialog.Builder(getContext())
                .setTitle("Save to " + goal.name)
                .setMessage(String.format("Progress: $%,.0f / $%,.0f\nRemaining: $%,.0f",
                        goal.current, goal.target, remaining))
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (val.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        double amount = Double.parseDouble(val);
                        if (amount <= 0) {
                            Toast.makeText(getContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        goal.current += amount;
                        Storage.get().save();
                        adapter.notifyItemChanged(position);
                        updateSavings();

                        if (goal.current >= goal.target) {
                            Toast.makeText(getContext(), "Congratulations! " + goal.name + " goal reached!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), String.format("$%,.0f saved to %s!", amount, goal.name), Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Goal");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText nameInput = new EditText(getContext());
        nameInput.setHint("Goal name (e.g. New Car)");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);

        EditText targetInput = new EditText(getContext());
        targetInput.setHint("Target amount (e.g. 5000)");
        targetInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(targetInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String targetStr = targetInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a goal name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (targetStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a target amount", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double target = Double.parseDouble(targetStr);
                if (target <= 0) {
                    Toast.makeText(getContext(), "Target must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                goals.add(new Goal(name, 0, target));
                Storage.get().save();
                adapter.notifyItemInserted(goals.size() - 1);
                Toast.makeText(getContext(), "Goal \"" + name + "\" added!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
