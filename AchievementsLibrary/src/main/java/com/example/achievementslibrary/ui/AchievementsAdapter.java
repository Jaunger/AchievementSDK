package com.example.achievementslibrary.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.achievementslibrary.R;
import com.example.achievementslibrary.entity.Achievement;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private List<Achievement> achievements;
    private Context context;

    public AchievementsAdapter(List<Achievement> achievements, Context context) {
        this.achievements = achievements;
        this.context = context;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.title.setText(achievement.getTitle());
        holder.description.setText(achievement.getDescription());

        Glide.with(context)
                .load(achievement.getImageUrl())
                .placeholder(R.drawable.ic_achievements_placeholder)
                .error(R.drawable.ic_achievements_placeholder)
                .into(holder.icon);

        if (achievement.getProgressGoal()!= null && achievement.getProgressGoal() > 1) {
            holder.layoutProgress.setVisibility(View.VISIBLE);
            int currentProgress = achievement.getCurrentProgress() != null ? achievement.getCurrentProgress() : 0;
            int goal = achievement.getProgressGoal();
            holder.progressBar.setMax(goal);
            holder.progressBar.setProgress(currentProgress);
            holder.textProgress.setText(Math.min(currentProgress, goal) + "/" + goal);
        } else {
            holder.layoutProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        LinearLayout layoutProgress;
        ProgressBar progressBar;
        TextView textProgress;

        AchievementViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imageAchievementIcon);
            title = itemView.findViewById(R.id.textAchievementTitle);
            description = itemView.findViewById(R.id.textAchievementDescription);
            layoutProgress = itemView.findViewById(R.id.layoutProgress);
            progressBar = itemView.findViewById(R.id.progressBarAchievement);
            textProgress = itemView.findViewById(R.id.textProgress);
        }
    }
}