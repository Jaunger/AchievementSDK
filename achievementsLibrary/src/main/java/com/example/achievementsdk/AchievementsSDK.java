// AchievementsSDK.java
package com.example.achievementsdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.achievementsdk.utility.ApiClient;
import com.example.achievementsdk.network.ApiKeyExtendedResponse;
import com.example.achievementsdk.utility.AchievementsApi;
import com.example.achievementsdk.ui.AchievementsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementsSDK {
    //TODO: remove needing to set user everytime and have loggout to exit user
    private static final String TAG = "AchievementsSDK";

    private static AchievementsSDK instance;

    private String apiKey;          // Developer's API Key
    private String baseUrl;         // Backend Base URL
    private String appId;           // <== NEW: store the app’s unique ID from the backend
    private String currentPlayerId; // The combined ID
    private boolean initialized;
    private String achievementListId; // Retrieved from API Key


    private AchievementsSDK() {}

    /**
     * Get the singleton instance of AchievementsSDK.
     */
    public static synchronized AchievementsSDK getInstance() {
        if (instance == null) {
            instance = new AchievementsSDK();
        }
        return instance;
    }

    /**
     * Initialize the SDK with API Key and Base URL.
     * This method retrieves the AchievementListId associated with the API Key.
     *
     * @param apiKey  Developer's API Key.
     * @param callback Callback to handle success or failure.
     */
    public void init(String apiKey, InitCallback callback) {
        // ...
        this.baseUrl = "http://10.0.2.2:3000/api/";
        AchievementsApi api = ApiClient.getClient(baseUrl).create(AchievementsApi.class);
        this.apiKey = apiKey;
        Call<ApiKeyExtendedResponse> call = api.getKeyData(apiKey); // e.g. getKeyData(...) returns {listId, appId}
        call.enqueue(new Callback<ApiKeyExtendedResponse>() {
            @Override
            public void onResponse(Call<ApiKeyExtendedResponse> call, Response<ApiKeyExtendedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // parse both
                    achievementListId = response.body().getListId();
                    appId = response.body().getAppId();               // <== store in AchievementsSDK
                    initialized = true;
                    callback.onSuccess();
                } else {
                    String errorMsg = "Initialization failed. Response Code: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiKeyExtendedResponse> call, Throwable t) {
                String errorMsg = "Initialization error: " + t.getMessage();
                Log.e(TAG, errorMsg);
                callback.onFailure(errorMsg);
            }
        });
    }
    /**
     * Check if the SDK is initialized.
     *
     * @return True if initialized, else false.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Get the current API Key.
     *
     * @return API Key string.
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Get the Base URL.
     *
     * @return Base URL string.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Get the AchievementListId.
     *
     * @return AchievementListId string.
     */
    public String getAchievementListId() {
        return achievementListId;
    }

    /**
     * Set the current Player ID.
     *
     * @param playerId Player ID string.
     */
    public void setCurrentPlayerId(String playerId) {
        this.currentPlayerId = playerId;
    }

    /**
     * Get the current Player ID.
     *
     * @return Player ID string.
     */
    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Show the Achievements UI.
     *
     * @param context Android Context.
     */
    public void showAchievementsUI(Context context) {
        if (!initialized) {
            Log.e(TAG, "SDK not initialized. Call init() first.");
            Toast.makeText(context, "SDK not initialized. Please call init() first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (achievementListId == null || achievementListId.isEmpty()) {
            Log.e(TAG, "AchievementListId is missing.");
            Toast.makeText(context, "AchievementListId is missing.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, AchievementsActivity.class);
        intent.putExtra("apiKey", this.apiKey);
        intent.putExtra("baseUrl", this.baseUrl);
        intent.putExtra("listId", this.achievementListId);
        intent.putExtra("playerId", appId + '_' + this.currentPlayerId);
        Log.d(TAG, "Launched AchievementsActivity with apiKey, baseUrl, and listId.");
        context.startActivity(intent);
    }

    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) { this.appId = appId; }

    /**
     * Callback interface for initialization.
     */
    public interface InitCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
