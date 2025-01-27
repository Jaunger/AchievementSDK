package com.example.achievementslibrary.utility;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoggingInterceptor implements Interceptor {
    private static final String TAG = "LoggingInterceptor";

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();

        Log.d(TAG, String.format("Sending request %s on %s\n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        Log.d(TAG, String.format("Received response for %s in %.1fms\n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        // Read the response body string
        String responseBodyString = response.body().string();
        Log.d(TAG, "Raw Response Body: " + responseBodyString);

        // Create a new response body because the original response body can only be consumed once
        ResponseBody newResponseBody = ResponseBody.create(response.body().contentType(), responseBodyString);
        return response.newBuilder().body(newResponseBody).build();
    }
}
