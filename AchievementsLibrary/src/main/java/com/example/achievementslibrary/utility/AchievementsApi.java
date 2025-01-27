package com.example.achievementslibrary.utility;

import com.example.achievementslibrary.entity.AchievementList;
import com.example.achievementslibrary.network.ApiKeyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for interacting with the Achievements API.
 * <p>
 * This interface defines endpoints for retrieving achievement lists, API key data,
 * and player-specific achievement lists.
 * </p>
 */
public interface AchievementsApi {
    /**
     * Retrieves the achievement list for a given list ID.
     * <p>
     * This method fetches the details of all achievements associated with the specified `listId`.
     * The API key is required for authentication.
     * </p>
     *
     * @param listId The ID of the achievement list.
     * @param apiKey The developer's API key.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain the `AchievementList` object if successful.
     */
    @GET("api/lists/{listId}")
    Call<AchievementList> getAchievementList(
            @Path("listId") String listId,
            @Header("x-api-key") String apiKey
    );

    /**
     * Retrieves API key data, including the associated list ID and application ID.
     * <p>
     * This method is used to fetch metadata associated with the provided API key.
     * </p>
     *
     * @param apiKey The developer's API key.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain the `ApiKeyResponse` object if successful.
     */
    @GET("api/apikeys")
    Call<ApiKeyResponse> getKeyData(@Query("key") String apiKey);

    /**
     * Retrieves the achievement list for a specific player.
     * <p>
     * This method fetches the achievement list for the given `playerId`, including
     * the player's progress and completion status for each achievement.
     * The API key is required for authentication.
     * </p>
     *
     * @param listId  The ID of the achievement list.
     * @param playerId The ID of the player.
     * @param apiKey The developer's API key.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain the `AchievementList` object if successful.
     */

    @GET("api/lists/{listId}/players/{playerId}")
    Call<AchievementList> getPlayerAchievementList(
            @Path("listId") String listId,
            @Path("playerId") String playerId,
            @Header("x-api-key") String apiKey
    );
}