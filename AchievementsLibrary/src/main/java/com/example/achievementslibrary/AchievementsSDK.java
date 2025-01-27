package com.example.achievementslibrary;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import com.example.achievementslibrary.network.ApiKeyResponse;
import com.example.achievementslibrary.ui.AchievementsDialogFragment;
import com.example.achievementslibrary.utility.AchievementsApi;
import com.example.achievementslibrary.utility.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * AchievementsSDK Singleton Class
 * Manages initialization and provides methods to interact with Achievements.
 */
public class AchievementsSDK {
    private static final String TAG = "AchievementsSDK";

    private static AchievementsSDK instance;

    private String apiKey;
    private String environment ="prod";
    private String appId;
    private String currentPlayerId;
    private boolean initialized;
    private String achievementListId;
    private Context appContext; // Store the application context

    private AchievementsSDK() {}

    public static synchronized AchievementsSDK getInstance() {
        if (instance == null) {
            instance = new AchievementsSDK();
        }
        return instance;
    }

    /**
     * Initialize the SDK.
     * This method retrieves the AchievementListId and AppId associated with the API Key.
     *
     * @param context  The application context.
     * @param callback Callback to handle success or failure of initialization.
     */
    public void init(Context context, InitCallback callback) {
        if (initialized) {
            callback.onSuccess();
            return;
        }

        this.appContext = context.getApplicationContext();

        Retrofit retrofit = ApiClient.getClient(appContext, this.environment);
        AchievementsApi api = retrofit.create(AchievementsApi.class);

        apiKey = ApiClient.getApiKey();

        Call<ApiKeyResponse> call = api.getKeyData(apiKey);
        call.enqueue(new Callback<ApiKeyResponse>() {
            @Override
            public void onResponse(Call<ApiKeyResponse> call, Response<ApiKeyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    achievementListId = response.body().getListId();
                    appId = response.body().getAppId();
                    initialized = true;
                    Log.d(TAG, "SDK initialized with List ID: " + achievementListId + " / App ID: " + appId);
                    callback.onSuccess();
                } else {
                    String errorMsg = "Initialization failed. Code: " + response.code() + ", Message: " + response.message();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiKeyResponse> call, Throwable t) {
                String errorMsg = "Initialization error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onFailure(errorMsg);
            }
        });
    }

    /**
     * Return whether the SDK has been successfully initialized.
     *
     * @return True if the SDK is initialized, false otherwise.
     */
    public boolean isInitialized() {
        return initialized;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAchievementListId() {
        return achievementListId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * Get the current Player ID (i.e., "appId_username" or any naming you decide).
     */
    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Set the current Player ID (just the "username" part or any unique string).
     * The actual combined ID could be "appId_username" in your code that does player creation.
     */
    public void setCurrentPlayerId(String playerId) {
        this.currentPlayerId = playerId;
    }

    /**
     * Return the current environment ("dev" or "prod").
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Create and return a new AchievementsDialogFragment instance
     * so the host app can display it as a floating dialog.
     *
     * @return AchievementsDialogFragment properly initialized with all required arguments.
     */
    public DialogFragment getAchievementsDialogFragment() {
        if (!initialized) {
            Log.e(TAG, "SDK not initialized. Call init() first.");
            return new DialogFragment(); // Alternatively, throw an exception
        }
        if (achievementListId == null || achievementListId.isEmpty()) {
            Log.e(TAG, "AchievementListId is missing. Check if API key call succeeded.");
            return new DialogFragment();
        }

        String finalPlayerId = "";
        if (currentPlayerId != null && !currentPlayerId.isEmpty()) {
            finalPlayerId = appId + "_" + currentPlayerId;
        }

        return AchievementsDialogFragment.newInstance(
                apiKey,
                achievementListId,
                finalPlayerId);
    }

    public void testConnectivity(TestCallback callback) {
        if (!initialized) {
            callback.onFailure("SDK not initialized.");
            return;
        }

        AchievementsApi api = ApiClient.getClient(appContext, environment).create(AchievementsApi.class);

        Call<ApiKeyResponse> call = api.getKeyData(apiKey);
        call.enqueue(new Callback<ApiKeyResponse>() {
            @Override
            public void onResponse(Call<ApiKeyResponse> call, Response<ApiKeyResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Connectivity Test Passed.");
                } else {
                    String errorMsg = "Connectivity Test Failed. Code: " + response.code() +
                            " Message: " + response.message();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiKeyResponse> call, Throwable t) {
                String errorMsg = "Connectivity Test Error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onFailure(errorMsg);
            }
        });
    }

    /**
     * Callback interface for SDK initialization.
     */
    public interface InitCallback {
        /**
         * Called when the SDK is successfully initialized.
         */
        void onSuccess();
        /**
         * Called when an error occurs during initialization.
         *
         * @param error The error message.
         */
        void onFailure(String error);
    }

    public interface TestCallback {
        /**
         * Called when the connectivity test is successful.
         *
         * @param message The success message.
         */
        void onSuccess(String message);
        /**
         * Called when an error occurs during the connectivity test.
         *
         * @param error The error message.
         */
        void onFailure(String error);
    }

    public Context getContext() {
        return appContext;
    }
}