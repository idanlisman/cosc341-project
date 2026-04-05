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

import com.example.phase4.adapter.CategoryAdapter;
import com.example.phase4.model.Category;

import java.util.List;

public class BudgetScreen extends Fragment {

    private CategoryAdapter adapter;
    private TextView budgetRemaining, spentText, totalText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        budgetRemaining = view.findViewById(R.id.budget_remaining);
        spentText = view.findViewById(R.id.spent_text);
        totalText = view.findViewById(R.id.total_text);

        RecyclerView categoryList = view.findViewById(R.id.category_list);
        categoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Category> categories = Storage.get().getCategories();
        adapter = new CategoryAdapter(categories);
        categoryList.setAdapter(adapter);
        updateSummary();
        view.findViewById(R.id.btn_new_category).setOnClickListener(v -> showNewCategoryDialog());
        view.findViewById(R.id.btn_edit_budgets).setOnClickListener(v -> showEditBudgetsDialog());
    }

    private void updateSummary() {
        double spent = Storage.get().getTotalSpent();
        double total = Storage.get().getTotalBudget();
        double remaining = total - spent;

        budgetRemaining.setText(String.format("$ %,.0f", remaining));
        spentText.setText(String.format("Spent: $%,.0f", spent));
        totalText.setText(String.format("Total: $%,.0f", total));
    }

    private void showNewCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Category");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText nameInput = new EditText(getContext());
        nameInput.setHint("Category name");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);

        EditText budgetInput = new EditText(getContext());
        budgetInput.setHint("Monthly budget (e.g. 500)");
        budgetInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(budgetInput);

        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String budgetStr = budgetInput.getText().toString().trim();
            if (name.isEmpty() || budgetStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double budget = Double.parseDouble(budgetStr);
                if (budget <= 0) {
                    Toast.makeText(getContext(), "Budget must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                Storage.get().getCategories().add(new Category(name, R.drawable.ic_custom, 0, budget));
                Storage.get().save();
                adapter.notifyItemInserted(Storage.get().getCategories().size() - 1);
                updateSummary();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid budget amount", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditBudgetsDialog() {
        List<Category> categories = Storage.get().getCategories();
        if (categories.isEmpty()) {
            Toast.makeText(getContext(), "No categories to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            names[i] = c.name + "  ($" + (int) c.budget + ")";
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Budget Limit")
                .setItems(names, (dialog, which) -> showEditSingleBudget(which))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditSingleBudget(int index) {
        Category cat = Storage.get().getCategories().get(index);

        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf((int) cat.budget));
        input.setSelectAllOnFocus(true);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit " + cat.name + " Budget")
                .setMessage("Current budget: $" + (int) cat.budget + "\nCurrent spent: $" + (int) cat.spent)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (val.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        double newBudget = Double.parseDouble(val);
                        if (newBudget <= 0) {
                            Toast.makeText(getContext(), "Budget must be greater than 0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cat.budget = newBudget;
                        Storage.get().save();
                        adapter.notifyItemChanged(index);
                        updateSummary();
                        Toast.makeText(getContext(), cat.name + " budget updated to $" + (int) newBudget, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
