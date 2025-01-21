package com.example.achievementsdk.network;

public class UpdateProgressRequest {
    private String achievementId;
    private int progressDelta;

    public UpdateProgressRequest(String achievementId, int progressDelta) {
        this.achievementId = achievementId;
        this.progressDelta = progressDelta;
    }
}
