// Achievement.java
package com.example.achievementsdk.entity;

import com.google.gson.annotations.SerializedName;

public class Achievement {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type;

    @SerializedName("progressGoal")
    private Integer progressGoal; // Use Integer to handle null values

    @SerializedName("currentProgress")
    private Integer currentProgress; // Current progress for in-progress achievements

    @SerializedName("isHidden")
    private boolean isHidden;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public Integer getProgressGoal() { return progressGoal; }
    public Integer getCurrentProgress() { return currentProgress; }
    public boolean isHidden() { return isHidden; }
    public String getImageUrl() { return imageUrl; }
}