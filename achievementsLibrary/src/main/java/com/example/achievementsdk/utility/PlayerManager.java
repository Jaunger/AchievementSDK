package com.example.achievementsdk.utility;


import com.example.achievementsdk.AchievementsSDK;
import com.example.achievementsdk.network.PlayerRequest;
import com.example.achievementsdk.network.UpdateProgressRequest;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerManager {

    public interface PlayerCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * Create or fetch a Player for a given appId/playerId.
     * We pass the x-api-key in a header if your server requires it.
     */
// PlayerManager.java (in your SDK utility)

    public static void createOrFetchPlayer(String rawUsername, PlayerCallback callback) {
        AchievementsSDK sdk = AchievementsSDK.getInstance();

        // Make sure sdk is initialized
        if (!sdk.isInitialized()) {
            callback.onError("SDK not initialized. Call init() first.");
            return;
        }

        // Merge appId with the raw username
        String appIdString = sdk.getAppId();
        if (appIdString == null || appIdString.isEmpty()) {
            callback.onError("appId not found from the API key doc.");
            return;
        }

        // final playerId => "appId_rawUsername"
        String finalPlayerId = appIdString + "_" + rawUsername;
        sdk.setCurrentPlayerId(finalPlayerId); // store in the SDK for reference

        // Now call the backend: POST /apps/:appId/players
        //   with body { playerId: finalPlayerId }

        PlayerApi api = ApiClient.getClient(sdk.getBaseUrl()).create(PlayerApi.class);
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
                        callback.onError(e.getMessage());
                    }
                } else {
                    callback.onError("Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Update progress for a specific achievement.
     */

    public static void updateAchievementProgress(
            String rawUsername,
            String achievementId,
            int delta,
            PlayerCallback callback
    ) {
        AchievementsSDK sdk = AchievementsSDK.getInstance();

        // Check SDK init
        if (!sdk.isInitialized()) {
            callback.onError("SDK not initialized. Call init() first.");
            return;
        }

        String appIdString = sdk.getAppId();
        if (appIdString == null || appIdString.isEmpty()) {
            callback.onError("appId not found from the API key doc.");
            return;
        }

        // final playerId => "appId_rawUsername"
        String finalPlayerId = appIdString + "_" + rawUsername;
        // store in the SDK if desired
        sdk.setCurrentPlayerId(finalPlayerId);
        //TODO: update this
        // Now call your existing route: PATCH /apps/{appId}/players/{playerId}/progress
        // with body { achievementId, progressDelta }

        PlayerApi api = ApiClient.getClient(sdk.getBaseUrl()).create(PlayerApi.class);
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
                        callback.onError(e.getMessage());
                    }
                } else {
                    callback.onError("Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}