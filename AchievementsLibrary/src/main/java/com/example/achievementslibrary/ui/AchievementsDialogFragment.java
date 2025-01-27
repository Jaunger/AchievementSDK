package com.example.achievementslibrary.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.achievementslibrary.R;
import com.example.achievementslibrary.entity.Achievement;
import com.example.achievementslibrary.entity.AchievementList;
import com.example.achievementslibrary.network.ErrorResponse;
import com.example.achievementslibrary.utility.AchievementsApi;
import com.example.achievementslibrary.utility.ApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A {@link DialogFragment} that displays a list of achievements in a floating window.
 * <p>
 * This fragment presents achievements categorized as "Completed" and "In Progress,"
 * along with a count of hidden achievements. It fetches achievement data from the server
 * using the provided API key, list ID, and player ID.
 * </p>
 * <p>
 * The dialog can be dismissed by clicking an "X" button in the top right corner.
 * </p>
 */
public class AchievementsDialogFragment extends DialogFragment {

    private static final String TAG = "AchievementsFragment";

    private static final String ARG_API_KEY    = "arg_api_key";
    private static final String ARG_LIST_ID    = "arg_list_id";
    private static final String ARG_PLAYER_ID  = "arg_player_id";

    private RecyclerView recyclerCompleted;
    private RecyclerView recyclerInProgress;
    private TextView textHiddenCount;
    private ImageButton buttonClose; // X button

    private AchievementsAdapter adapterCompleted;
    private AchievementsAdapter adapterInProgress;

    private List<Achievement> completedAchievements = new ArrayList<>();
    private List<Achievement> inProgressAchievements = new ArrayList<>();
    private int hiddenCount = 0;

    private String apiKey;
    private String listId;
    private String playerId;
    private String enviroment = "prod";

    /**
     * Constructor applying the custom dialog theme.
     */
    public AchievementsDialogFragment() {
        // Apply your custom dialog theme
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @param apiKey      The developer's API key.
     * @param listId      The ID of the achievement list.
     * @param playerId    The ID of the player.
     * @return A new instance of fragment AchievementsDialogFragment.
     */
    public static AchievementsDialogFragment newInstance(String apiKey, String listId, String playerId) {
        AchievementsDialogFragment fragment = new AchievementsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_API_KEY, apiKey);
        args.putString(ARG_LIST_ID, listId);
        args.putString(ARG_PLAYER_ID, playerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            apiKey     = getArguments().getString(ARG_API_KEY);
            listId     = getArguments().getString(ARG_LIST_ID);
            playerId   = getArguments().getString(ARG_PLAYER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);

        setupRecyclerViews();

        buttonClose.setOnClickListener(v -> dismiss());

        if (apiKey == null || apiKey.isEmpty() || listId == null || listId.isEmpty()) {
            Log.e(TAG, "Missing required parameters: apiKey or listId.");
            Toast.makeText(requireContext(), "Missing required parameters.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        fetchAchievements();
    }

    private void findViews(@NonNull View view) {
        recyclerCompleted   = view.findViewById(R.id.recyclerCompletedAchievements);
        recyclerInProgress  = view.findViewById(R.id.recyclerInProgressAchievements);
        textHiddenCount     = view.findViewById(R.id.textHiddenAchievements);
        buttonClose          = view.findViewById(R.id.button_close);
    }

    private void setupRecyclerViews() {
        adapterCompleted = new AchievementsAdapter(completedAchievements, requireContext());
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCompleted.setAdapter(adapterCompleted);

        adapterInProgress = new AchievementsAdapter(inProgressAchievements, requireContext());
        recyclerInProgress.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerInProgress.setAdapter(adapterInProgress);
    }

    /**
     * Fetches achievements from the server.
     * <p>
     * This method uses the provided API key, list ID, and player ID to retrieve
     * achievement data from the server. It then categorizes and sorts the achievements
     * for display in the UI.
     * </p>
     */
    private void fetchAchievements() {
        Log.d(TAG, "Fetching achievements for listId=" + listId + ", playerId=" + playerId);

        Retrofit retrofit = ApiClient.getClient(requireContext(),enviroment);
        AchievementsApi api = retrofit.create(AchievementsApi.class);

        Call<AchievementList> call = api.getPlayerAchievementList(listId, playerId, apiKey);
        call.enqueue(new Callback<AchievementList>() {
            @Override
            public void onResponse(Call<AchievementList> call, Response<AchievementList> response) {
                Log.d(TAG, "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Achievement> achievements = response.body().getAchievements();
                    if (achievements != null && !achievements.isEmpty()) {
                        categorizeAndSortAchievements(achievements);
                    } else {
                        Log.w(TAG, "Achievements list is empty or null.");
                        Toast.makeText(requireContext(),
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
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<AchievementList> response) {
        if (response.errorBody() != null) {
            try {
                ErrorResponse errorResponse = new Gson().fromJson(
                        response.errorBody().charStream(),
                        ErrorResponse.class
                );
                String detailedError = (errorResponse != null && errorResponse.getError() != null)
                        ? errorResponse.getError()
                        : "Unknown error";

                Toast.makeText(requireContext(), detailedError, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error detail: " + detailedError);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing errorBody: " + e.getMessage());
                Toast.makeText(requireContext(), "Error parsing error response.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(requireContext(),
                    "Unknown error (" + response.code() + ")",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Categorizes and sorts achievements.
     * <p>
     * This method separates achievements into "Completed" and "In Progress" lists,
     * calculates the number of hidden achievements, and sorts both lists by their order.
     * It then updates the UI with the categorized and sorted data.
     * </p>
     *
     * @param achievements The list of achievements to categorize and sort.
     */
    private void categorizeAndSortAchievements(List<Achievement> achievements) {
        // Clear old data
        completedAchievements.clear();
        inProgressAchievements.clear();
        hiddenCount = 0;

        for (Achievement achievement : achievements) {
            if (achievement.isHidden() && isNotCompleted(achievement)) {
                hiddenCount++;
                continue;
            }

            if (isNotCompleted(achievement)) {
                inProgressAchievements.add(achievement);
            } else {
                completedAchievements.add(achievement);
            }
        }

        // Sort both lists by 'order'
        Comparator<Achievement> orderComparator = (a1, a2) -> {
            if (a1 == null && a2 == null) return 0;
            if (a1 == null) return -1;
            if (a2 == null) return 1;
            return Integer.compare(
                    a1.getOrder() != null ? a1.getOrder() : 0,
                    a2.getOrder() != null ? a2.getOrder() : 0
            );
        };
        Collections.sort(completedAchievements, orderComparator);
        Collections.sort(inProgressAchievements, orderComparator);

        // Notify adapters
        adapterCompleted.notifyDataSetChanged();
        adapterInProgress.notifyDataSetChanged();

        // Update hidden achievements count
        updateHiddenCount();

        Log.d(TAG, "Achievements fetched and sorted. " +
                "Completed: " + completedAchievements.size() +
                ", In-Progress: " + inProgressAchievements.size() +
                ", Hidden: " + hiddenCount);
    }

    /**
     * Checks if an achievement is not completed.
     *
     * @param achievement The achievement to check.
     * @return True if the achievement is not completed, false otherwise.
     */
    private static boolean isNotCompleted(Achievement achievement) {
        if (achievement.getProgressGoal() == null) return false;
        if (achievement.getCurrentProgress() == null) return true;
        return achievement.getCurrentProgress() < achievement.getProgressGoal();
    }

    private void updateHiddenCount() {
        String hiddenText = "Hidden Achievements: " + hiddenCount;
        textHiddenCount.setText(hiddenText);
    }

    /**
     * Override onStart to set dialog width and height
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set the dialog to 90% of the screen width
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }
}