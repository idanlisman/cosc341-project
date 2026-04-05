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
import com.example.phase4.model.Goal;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private final List<Goal> goals;
    private OnGoalClickListener clickListener;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal, int position);
    }

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    public void setOnGoalClickListener(OnGoalClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        int percent = goal.getProgressPercent();

        holder.name.setText(goal.name);
        holder.percent.setText(percent + "%");
        holder.amounts.setText("$" + String.format("%,.0f", goal.current)
                + " / $" + String.format("%,.0f", goal.target));

        int blueColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.progress_blue);
        holder.percent.setTextColor(blueColor);

        holder.progress.setProgressDrawable(
                ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.progress_blue));
        holder.progress.setProgress(0);
        holder.progress.setProgress(percent);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onGoalClick(goal, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, percent, amounts;
        ProgressBar progress;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.goal_name);
            percent = itemView.findViewById(R.id.goal_percent);
            amounts = itemView.findViewById(R.id.goal_amounts);
            progress = itemView.findViewById(R.id.goal_progress);
        }
    }
}
