package com.example.achievementsdk.network;

// ErrorResponse.java
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("error")
    private String error;

    public String getError() {
        return error;
    }
}
