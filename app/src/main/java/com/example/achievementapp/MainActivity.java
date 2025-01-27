package com.example.achievementapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.achievementslibrary.AchievementsSDK;
import com.example.achievementslibrary.utility.PlayerManager;

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


        // 1) Initialize the SDK (only needs apiKey + environment)
        AchievementsSDK.getInstance().init(
                this,  // pass 'Activity' or 'Application' context here
                // environment
                new AchievementsSDK.InitCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("MainActivity", "SDK initialized successfully.");
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("MainActivity", "SDK initialization failed: " + error);
                    }
                }
        );
        // 2) Show Achievements (Dialog-based now)
        btnShowAchievements.setOnClickListener(v -> {
            Log.d(TAG, "Show Achievements button clicked.");

            // Example raw username
            String rawUsername = "guest123";

            // Set the current player ID in SDK
            AchievementsSDK.getInstance().setCurrentPlayerId(rawUsername);

            // Retrieve SDK instance
            AchievementsSDK sdk = AchievementsSDK.getInstance();

            // Get the DialogFragment
            DialogFragment achievementsDialog = sdk.getAchievementsDialogFragment();

            // Show the dialog
            achievementsDialog.show(getSupportFragmentManager(), "AchievementsDialog");
        });

        // 3) Check or Create Player (using raw username)
        btnCheckPlayer.setOnClickListener(v -> {
            Log.d(TAG, "Check Player button clicked.");

            String rawUsername = "guest123";

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

            String achievementId= "67912df36d91ada157bba9ec";
            int delta = 5; // Increase progress by 5

            // We'll assume you have an equivalent
            // updateAchievementProgress(rawUsername, achievementId, delta, callback)
            // in your PlayerManager. Similar to createOrFetch, it merges IDs behind the scenes.
            PlayerManager.updateAchievementProgress(
                    "guest123",
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