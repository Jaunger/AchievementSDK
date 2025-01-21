// ApiKeyResponse.java
package com.example.achievementsdk.network;

import com.google.gson.annotations.SerializedName;

public class ApiKeyResponse {
    @SerializedName("listId")
    private String listId;

    public String getListId() {
        return listId;
    }
}