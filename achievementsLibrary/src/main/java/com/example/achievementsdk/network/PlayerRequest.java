package com.example.achievementsdk.network;

public class PlayerRequest {
    private String playerId; // The ID (guest or login) the dev sets

    public PlayerRequest(String playerId) {
        this.playerId = playerId;
    }
}
