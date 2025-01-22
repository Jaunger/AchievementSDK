// AchievementsActivity.java
package com.example.achievementsdk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.achievementsdk.R;
import com.example.achievementsdk.entity.Achievement;
import com.example.achievementsdk.entity.AchievementList;
import com.example.achievementsdk.utility.AchievementsApi;
import com.example.achievementsdk.network.ErrorResponse;
import com.example.achievementsdk.utility.ApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
//TODO: add loading picture to glide and order integration
public class AchievementsActivity extends AppCompatActivity {

    private static final String TAG = "AchievementsActivity";

    private RecyclerView recyclerCompleted;
    private RecyclerView recyclerInProgress;
    private TextView textHiddenCount;

    private AchievementsAdapter adapterCompleted;
    private AchievementsAdapter adapterInProgress;

    private List<Achievement> completedAchievements = new ArrayList<>();
    private List<Achievement> inProgressAchievements = new ArrayList<>();

    private String apiKey;
    private String baseUrl;
    private String listId;
    private int hiddenCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        String playerId = getIntent().getStringExtra("playerId");
        Log.d(TAG, "Achievements for player: " + playerId);

        // Initialize views
        recyclerCompleted = findViewById(R.id.recyclerCompletedAchievements);
        recyclerInProgress = findViewById(R.id.recyclerInProgressAchievements);
        textHiddenCount = findViewById(R.id.textHiddenAchievements);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Retrieve Intent extras
        Intent intent = getIntent();
        apiKey = intent.getStringExtra("apiKey");
        baseUrl = intent.getStringExtra("baseUrl");
        listId = intent.getStringExtra("listId");

        // Validate Intent extras
        if (apiKey == null || apiKey.isEmpty() ||
                baseUrl == null || baseUrl.isEmpty() ||
                listId == null || listId.isEmpty()) {
            Log.e(TAG, "Missing required parameters: apiKey, baseUrl, or listId.");
            Toast.makeText(this, "Missing required parameters.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Fetching achievements for List ID: " + listId);

        // Fetch achievements from the server
        fetchAchievements();
    }

    /**
     * Setup RecyclerViews for Completed and In-Progress Achievements.
     */
    private void setupRecyclerViews() {
        // Completed Achievements RecyclerView
        adapterCompleted = new AchievementsAdapter(completedAchievements, this);
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompleted.setAdapter(adapterCompleted);

        // In-Progress Achievements RecyclerView
        adapterInProgress = new AchievementsAdapter(inProgressAchievements, this);
        recyclerInProgress.setLayoutManager(new LinearLayoutManager(this));
        recyclerInProgress.setAdapter(adapterInProgress);
    }

    /**
     * Fetch achievements from the server using AchievementListId and API Key.
     */
    private void fetchAchievements() {
        // 1) Validate inputs
        if (apiKey == null || apiKey.isEmpty() ||
                baseUrl == null || baseUrl.isEmpty() ||
                listId == null || listId.isEmpty()) {
            Log.e(TAG, "Missing required parameters: apiKey, baseUrl, or listId.");
            Toast.makeText(this, "Missing required parameters.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2) Also retrieve playerId
        String playerId = getIntent().getStringExtra("playerId");
        if (playerId == null || playerId.isEmpty()) {
            Log.e(TAG, "No playerId provided. The achievements may not reflect this player's progress.");
            // Optionally continue or return. For now, let's continue but warn.
        }

        Log.d(TAG, "Fetching achievements for list: " + listId + ", player: " + playerId);

        // 3) Create Retrofit instance & API
        Retrofit retrofit = ApiClient.getClient(baseUrl);
        AchievementsApi api = retrofit.create(AchievementsApi.class);

        // 4) Make the call to the player-specific endpoint
        //    Example: GET /lists/{listId}/players/{playerId}
        //    If your route is different, adjust accordingly.
        Call<AchievementList> call = api.getPlayerAchievementList(listId, playerId, apiKey);

        call.enqueue(new Callback<AchievementList>() {
            @Override
            public void onResponse(Call<AchievementList> call, Response<AchievementList> response) {
                Log.d(TAG, "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Achievement> achievements = response.body().getAchievements();
                    if (achievements != null && !achievements.isEmpty()) {
                        categorizeAchievements(achievements);
                        adapterCompleted.notifyDataSetChanged();
                        adapterInProgress.notifyDataSetChanged();
                        updateHiddenCount();
                        Log.d(TAG, "Achievements fetched. Completed: "
                                + completedAchievements.size()
                                + ", In-Progress: " + inProgressAchievements.size()
                                + ", Hidden: " + hiddenCount);
                    } else {
                        Log.e(TAG, "Achievements list is empty or null for this player.");
                        Toast.makeText(AchievementsActivity.this,
                                "No achievements found for this player.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to load achievements. Code: " + response.code();
                    Log.e(TAG, errorMsg);
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<AchievementList> call, Throwable t) {
                String errorMsg = "Error fetching achievements: " + t.getMessage();
                Log.e(TAG, errorMsg);
                Toast.makeText(AchievementsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Example helper to parse error body (similar to your existing logic)
    private void handleErrorResponse(Response<AchievementList> response) {
        if (response.errorBody() != null) {
            try {
                ErrorResponse errorResponse = new Gson()
                        .fromJson(response.errorBody().charStream(), ErrorResponse.class);
                String detailedError = (errorResponse != null && errorResponse.getError() != null)
                        ? errorResponse.getError() : "Unknown error";
                Toast.makeText(AchievementsActivity.this, detailedError, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error detail: " + detailedError);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing errorBody: " + e.getMessage());
            }
        } else {
            Toast.makeText(AchievementsActivity.this,
                    "Unknown error (" + response.code() + ")",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Categorize achievements into Completed, In-Progress, and Hidden.
     *
     * @param achievements List of all achievements.
     */
    private void categorizeAchievements(List<Achievement> achievements) {
        for (Achievement achievement : achievements) {
            if (achievement.isHidden()
                    && isNotCompleted(achievement)) {
                hiddenCount++;
                continue; // Skip adding to other lists
            }

            if (isNotCompleted(achievement)){
                // Assuming that if progressGoal is not null, it's an In-Progress achievement
                inProgressAchievements.add(achievement);
            } else {
                // If no progressGoal, consider it as Completed
                completedAchievements.add(achievement); //TODO: change this
            }
        }
    }

    /**
     * @param achievement
     * @return
     */
    private static boolean isNotCompleted(Achievement achievement) {
        return achievement.getProgressGoal() != null
                && achievement.getProgressGoal() > achievement.getCurrentProgress();
    }

    /**
     * Update the Hidden Achievements count in the UI.
     */
    private void updateHiddenCount() {
        String hiddenText = "Hidden Achievements: " + hiddenCount;
        textHiddenCount.setText(hiddenText);
    }
}