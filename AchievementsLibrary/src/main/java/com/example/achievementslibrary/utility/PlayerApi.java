package com.example.achievementslibrary.utility;

import com.example.achievementslibrary.network.PlayerRequest;
import com.example.achievementslibrary.network.UpdateProgressRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit interface for interacting with the Player API.
 * <p>
 * This interface defines endpoints for managing player data, including creating or fetching players,
 * updating achievement progress, and retrieving player details.
 * </p>
 */
public interface PlayerApi {

    /**
     * Creates or fetches a player for a given application.
     * <p>
     * If a player with the specified `playerId` within the `PlayerRequest` body already exists,
     * their data is retrieved. Otherwise, a new player is created.
     * </p>
     *
     * @param appId The ID of the application.
     * @param body  The `PlayerRequest` object containing the player's ID.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain the player data if successful.
     */
    @POST("api/players/{appId}/players")
    Call<ResponseBody> createOrFetchPlayer(
            @Path("appId") String appId,
            @Body PlayerRequest body
    );

    /**
     * Updates the progress of a specific achievement for a given player.
     * <p>
     * The `UpdateProgressRequest` body contains the `achievementId` and the `progressDelta`
     * to be applied to the player's progress for that achievement.
     * </p>
     *
     * @param appId    The ID of the application.
     * @param playerId The ID of the player.
     * @param body     The `UpdateProgressRequest` object containing the achievement ID and progress delta.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain the updated player data if successful.
     */
    @PATCH("api/players/{appId}/players/{playerId}/progress")
    Call<ResponseBody> updateProgress(
            @Path("appId") String appId,
            @Path("playerId") String playerId,
            @Body UpdateProgressRequest body
    );

    /**
     * Retrieves the details of a specific player.
     *
     * @param appId    The ID of the application.
     * @param playerId The ID of the player.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain the player's details if successful.
     */
    @GET("api/players/{appId}/players/{playerId}")
    Call<ResponseBody> getPlayer(
            @Path("appId") String appId,
            @Path("playerId") String playerId
    );

    /**
     * Deletes a player from the system.
     * <p>
     * This endpoint removes a player associated with the given `appId` and `playerId`.
     * </p>
     *
     * @param appId The ID of the application.
     * @param playerId The ID of the player to be deleted.
     * @return A `Call` object representing the asynchronous API request.
     *         The response body will contain a success message if successful.
     */
    @DELETE("api/players/{appId}/{pId}")
    Call<ResponseBody> deletePlayer(
            @Path("appId") String appId,
            @Path("pId") String playerId
    );

}