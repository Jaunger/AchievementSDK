package com.example.achievementslibrary.utility;

import com.example.achievementslibrary.AchievementsSDK;
import com.example.achievementslibrary.network.PlayerRequest;
import com.example.achievementslibrary.network.UpdateProgressRequest;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PlayerManager Utility Class
 * Handles player creation/fetching and progress updates.
 **/
public class PlayerManager {

    /**
     * Callback interface for player operations.
     */
    public interface PlayerCallback {

        /**
         * Called when the operation is successful.
         *
         * @param response The response string.
         */
        void onSuccess(String response);

        /**
         * Called when an error occurs during the operation.
         *
         * @param error The error message.
         */
        void onError(String error);
    }

    /**
     * Creates or fetches a player.
     *
     * @param rawUsername The raw username of the player.
     * @param callback    The callback to receive the result.
     */
    public static void createOrFetchPlayer( String rawUsername, PlayerCallback callback) {
        AchievementsSDK sdk = AchievementsSDK.getInstance();

        if (!sdk.isInitialized()) {
            callback.onError("SDK not initialized. Call init() first.");
            return;
        }

        String appIdString = sdk.getAppId();
        if (appIdString == null || appIdString.isEmpty()) {
            callback.onError("appId not found from the API key response.");
            return;
        }

        String finalPlayerId = appIdString + "_" + rawUsername;
        sdk.setCurrentPlayerId(finalPlayerId);

        String environment = sdk.getEnvironment();


        // Pass context to ApiClient:
        PlayerApi api = ApiClient.getClient(sdk.getContext(), environment).create(PlayerApi.class);
        PlayerRequest body = new PlayerRequest(finalPlayerId);

        Call<ResponseBody> call = api.createOrFetchPlayer(appIdString, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String respString = response.body().string();
                        callback.onSuccess(respString);
                    } catch (IOException e) {
                        callback.onError("Error reading response: " + e.getMessage());
                    }
                } else {
                    String errorMsg = "Failed to create/fetch player. Response Code: " + response.code();
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                callback.onError(errorMsg);
            }
        });
    }

    /**
     * Updates the progress of an achievement for a player.
     *
     * @param rawUserName  The raw username of the player.
     * @param achievementId The ID of the achievement.
     * @param delta        The change in progress.
     * @param callback     The callback to receive the result.
     */
    public static void updateAchievementProgress(
            String rawUserName,
            String achievementId,
            int delta,
            PlayerCallback callback
    ) {
        AchievementsSDK sdk = AchievementsSDK.getInstance();

        if (!sdk.isInitialized()) {
            callback.onError("SDK not initialized. Call init() first.");
            return;
        }

        String appIdString = sdk.getAppId();
        if (appIdString == null || appIdString.isEmpty()) {
            callback.onError("appId not found from the API key response.");
            return;
        }

        String finalPlayerId = appIdString + "_" + rawUserName;
        String environment = sdk.getEnvironment();

        // Pass context to ApiClient:
        PlayerApi api = ApiClient.getClient(sdk.getContext(), environment).create(PlayerApi.class);
        UpdateProgressRequest request = new UpdateProgressRequest(achievementId, delta);

        Call<ResponseBody> call = api.updateProgress(appIdString, finalPlayerId, request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String respString = response.body().string();
                        callback.onSuccess(respString);
                    } catch (IOException e) {
                        callback.onError("Error reading response: " + e.getMessage());
                    }
                } else {
                    String errorMsg = "Failed to update achievement progress. Response Code: " + response.code();
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                callback.onError(errorMsg);
            }
        });
    }


    /**
     * Deletes a player from the system.
     *
     * @param appId The ID of the application.
     * @param playerId The ID of the player to delete.
     * @param callback The callback to handle success or failure.
     */
    public static void deletePlayer(String appId, String playerId, PlayerCallback callback) {
        AchievementsSDK sdk = AchievementsSDK.getInstance();

        if (!sdk.isInitialized()) {
            callback.onError("SDK not initialized. Call init() first.");
            return;
        }

        String environment = sdk.getEnvironment();

        PlayerApi api = ApiClient.getClient(sdk.getContext(), environment).create(PlayerApi.class);

        api.deletePlayer(appId, playerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess("Player deleted successfully.");
                } else {
                    callback.onError("Failed to delete player. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Delete request failed: " + t.getMessage());
            }
        });
    }
}