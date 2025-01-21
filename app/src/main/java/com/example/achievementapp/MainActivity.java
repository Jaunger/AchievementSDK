package com.example.achievementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.achievementsdk.AchievementsSDK;
import com.example.achievementsdk.utility.PlayerManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnShowAchievements;
    private Button btnCheckPlayer;
    private Button btnUpdateAchievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowAchievements    = findViewById(R.id.btnShowAchievements);
        btnCheckPlayer         = findViewById(R.id.btnCheckPlayer);
        btnUpdateAchievement   = findViewById(R.id.btnUpdateAchievement);

        // 1) Initialize the SDK (only needs apiKey + baseUrl)
        AchievementsSDK.getInstance().init(
                "BDIKA",              // same as in your seed data
                new AchievementsSDK.InitCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "SDK initialized successfully.");
                        Toast.makeText(MainActivity.this,
                                "SDK Initialized Successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "SDK initialization failed: " + error);
                        Toast.makeText(MainActivity.this,
                                "SDK Initialization Failed: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        // 2) Show Achievements
        btnShowAchievements.setOnClickListener(v -> {
            Log.d(TAG, "Show Achievements button clicked.");

            // Example raw username
            String rawUsername = "guest123";
            // The SDK can optionally store this final combined ID,
            // but for showing achievements, we only need to setCurrentPlayerId:
            AchievementsSDK.getInstance().setCurrentPlayerId(rawUsername);

            // Show Achievements UI
            AchievementsSDK.getInstance().showAchievementsUI(this);
        });

        // 3) Check or Create Player (using raw username)
        btnCheckPlayer.setOnClickListener(v -> {
            Log.d(TAG, "Check Player button clicked.");

            // We only pass "guest123" now
            String rawUsername = "guest123";

            // This new method merges 'appId + "_" + rawUsername' internally
            PlayerManager.createOrFetchPlayer(
                    rawUsername,
                    new PlayerManager.PlayerCallback() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d(TAG, "Player fetched/created: " + response);
                            Toast.makeText(MainActivity.this,
                                    "Player Response: " + response,
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error fetching/creating player: " + error);
                            Toast.makeText(MainActivity.this,
                                    "Error: " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        // 4) Update an Achievement's Progress (using raw username again)
        btnUpdateAchievement.setOnClickListener(v -> {
            Log.d(TAG, "Update Achievement button clicked.");

            // The user only provides raw username here
            String rawUsername  = "guest123";
            String achievementId= "678e7ff9f632f261378640d2";
            int delta = 5; // Increase progress by 5

            // We'll assume you have an equivalent
            // updateAchievementProgress(rawUsername, achievementId, delta, callback)
            // in your PlayerManager. Similar to createOrFetch, it merges IDs behind the scenes.
            PlayerManager.updateAchievementProgress(
                    rawUsername,
                    achievementId,
                    delta,
                    new PlayerManager.PlayerCallback() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d(TAG, "Achievement updated: " + response);
                            Toast.makeText(MainActivity.this,
                                    "Achievement progress updated!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Failed to update achievement: " + error);
                            Toast.makeText(MainActivity.this,
                                    "Error: " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }
}