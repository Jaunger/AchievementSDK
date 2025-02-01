package com.example.achievementapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achievementslibrary.AchievementsSDK;
import com.example.achievementslibrary.utility.PlayerManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private TextView textCurrentPlayer;
    private String currentPlayer = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCurrentPlayer = findViewById(R.id.textCurrentPlayer);
        Button btnCheckPlayer1 = findViewById(R.id.buttonPlayer1);
        Button btnCheckPlayer2 = findViewById(R.id.buttonPlayer2);
        Button btnMilestone = findViewById(R.id.buttonMilestoneAchievement);
        Button btnHidden = findViewById(R.id.buttonHiddenAchievement);
        Button btnProgress = findViewById(R.id.buttonProgressAchievement);
        Button btnShowAchievements = findViewById(R.id.buttonShowAchievements);


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
            String rawUsername = currentPlayer;

            // Set the current player ID in SDK
            AchievementsSDK.getInstance().setCurrentPlayerId(rawUsername);

            // Retrieve SDK instance
            AchievementsSDK sdk = AchievementsSDK.getInstance();

            // Get the DialogFragment
            DialogFragment achievementsDialog = sdk.getAchievementsDialogFragment();

            // Show the dialog
            achievementsDialog.show(getSupportFragmentManager(), "AchievementsDialog");
        });

        btnCheckPlayer1.setOnClickListener(v -> {
            Log.d(TAG, "Check Player1 button clicked.");

            createOrFetch("player1");

        });
        btnCheckPlayer2.setOnClickListener(v -> {
            Log.d(TAG, "Check Player2 button clicked.");

            createOrFetch("player2");


        });

        btnMilestone.setOnClickListener(v -> {
            Log.d(TAG, "Update Milestone Achievement button clicked.");

            String achievementId= "679e207d834f80fd5d304c6d";
            int delta = 1;

            updateProg(achievementId, delta);
        });
        btnProgress.setOnClickListener(v -> {
            Log.d(TAG, "Update Progress Achievement button clicked.");

            String achievementId= "679e207c834f80fd5d304c5b";
            int delta = 5;

            updateProg(achievementId, delta);
        });
        btnHidden.setOnClickListener(v -> {
            Log.d(TAG, "Update Hidden Achievement button clicked.");

            String achievementId= "679e207c834f80fd5d304c61";
            int delta = 1;

            updateProg(achievementId, delta);
        });

    }

    private void updateProg(String achievementId, int delta) {
        if (!isPlayerSelected())
            return;
        PlayerManager.updateAchievementProgress(
                currentPlayer,
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
    }

    private boolean createOrFetch(String rawUserName) {
        final Boolean[] check = {false};
        PlayerManager.createOrFetchPlayer(
                rawUserName,
                new PlayerManager.PlayerCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "Player fetched/created: " + response);
                                currentPlayer = rawUserName;
                                setCurrentPlayer(currentPlayer);
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
        return check[0];
    }

    private void setCurrentPlayer(String player) {
        currentPlayer = player;
        textCurrentPlayer.setText("Current Player: " + currentPlayer);
        Toast.makeText(this, player + " selected", Toast.LENGTH_SHORT).show();
    }
    private boolean isPlayerSelected() {
        if (currentPlayer.equals("None")) {
            Toast.makeText(this, "Select a player first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}