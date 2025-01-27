package com.example.achievementslibrary.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AchievementList {
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("achievements")
    private List<Achievement> achievements;

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Achievement> getAchievements() { return achievements; }
}
