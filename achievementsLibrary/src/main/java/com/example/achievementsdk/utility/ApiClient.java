// ApiClient.java
package com.example.achievementsdk.utility;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String env) {
            String baseUrl = env.equals("dev")
                    ? "http://10.0.2.2:3000/" // Emulator
                    : "https://achievementapi.onrender.com/api/"; // Physical device (replace with your IP)

        // Create an OkHttpClient with the logging interceptor
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();

        if (retrofit == null || !baseUrl.equals(retrofit.baseUrl().toString())) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient) // Attach the custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}