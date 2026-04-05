package com.example.phase4.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phase4.R;
import com.example.phase4.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<Category> categories;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = categories.get(position);
        holder.name.setText(cat.name);
        holder.amounts.setText("$" + (int) cat.spent + " / $" + (int) cat.budget);

        int percent = cat.getProgressPercent();

        int drawableRes;
        if (percent >= 100) drawableRes = R.drawable.progress_red;
        else if (percent >= 50) drawableRes = R.drawable.progress_orange;
        else drawableRes = R.drawable.progress_green;

        holder.progress.setProgressDrawable(
                ContextCompat.getDrawable(holder.itemView.getContext(), drawableRes));
        holder.progress.setProgress(0);
        holder.progress.setProgress(percent);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, amounts;
        ProgressBar progress;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            amounts = itemView.findViewById(R.id.category_amounts);
            progress = itemView.findViewById(R.id.category_progress);
        }
    }
}
