package com.example.achievementslibrary.utility;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static String apiKey;
    public static Retrofit getClient(Context context, String env) {
        String baseUrl = env.equals("dev")
                ? "http://10.0.2.2:3000/" // Emulator
                : "https://achievementapi.onrender.com/"; // Physical device (replace with your IP)


        // Retrieve the API key from metadata
        apiKey = metaDataUtil.getApiKey(context);
        if (apiKey == null) {
            throw new IllegalStateException("API key not found in metadata.");
        }

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
    public static String getApiKey(){
        return apiKey;
    }

}