package com.example.achievementsdk.utility;

import com.example.achievementsdk.entity.AchievementList;
import com.example.achievementsdk.network.ApiKeyExtendedResponse;
import com.example.achievementsdk.network.ApiKeyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AchievementsApi {
    /**
     * Retrieve AchievementListId using the API Key.
     *
     * GET /api/apikeys?key=YOUR_API_KEY
     *
     * Expected Response:
     * {
     *   "listId": "ACHIEVEMENT_LIST_ID"
     * }
     */
    @GET("apikeys")
    Call<ApiKeyResponse> getAchievementListId(
            @Query("key") String apiKey
    );

    /**
     * Retrieve all Achievements for a specific AchievementListId.
     *
     * GET /api/achievementlists/{listId}/achievements
     *
     * Headers:
     * x-api-key: YOUR_API_KEY
     */
    @GET("lists/{listId}")
    Call<AchievementList> getAchievementList(
            @Path("listId") String listId,
            @Header("x-api-key") String apiKey
    );

    @GET("apikeys")
    Call<ApiKeyExtendedResponse> getKeyData(@Query("key") String apiKey);

    @GET("lists/{listId}/players/{playerId}")
    Call<AchievementList> getPlayerAchievementList(
            @Path("listId")   String listId,
            @Path("playerId") String playerId,
            @Header("x-api-key") String apiKey
    );
}