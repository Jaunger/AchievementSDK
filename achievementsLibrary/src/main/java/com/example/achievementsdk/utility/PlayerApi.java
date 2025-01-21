package com.example.achievementsdk.utility;

import com.example.achievementsdk.entity.AchievementList;
import com.example.achievementsdk.network.PlayerRequest;
import com.example.achievementsdk.network.UpdateProgressRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface PlayerApi {

    // POST /apps/{appId}/players => create or fetch the player
    @POST("apps/{appId}/players")
    Call<ResponseBody> createOrFetchPlayer(
            @Path("appId") String appId,
            @Body PlayerRequest body
    );

    // PATCH /apps/{appId}/players/{pId}/progress => update progress
    @PATCH("apps/{appId}/players/{pId}/progress")
    Call<ResponseBody> updateProgress(
            @Path("appId") String appId,
            @Path("pId") String playerId,
            @Body UpdateProgressRequest body
    );

    // GET /apps/{appId}/players/{pId}
    @GET("apps/{appId}/players/{pId}")
    Call<ResponseBody> getPlayer(
            @Path("appId") String appId,
            @Path("pId") String playerId
    );



    // ...
}

