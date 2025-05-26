package com.example.students.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiClient {
    private static final String BASE_URL = "https://jtypskinszqdtvychkuk.supabase.co";
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp0eXBza2luc3pxZHR2eWNoa3VrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc3Mzc2NjMsImV4cCI6MjA2MzMxMzY2M30.NSfND48xiybsKbmV2Ft4xPwJhiFLP7Ys8Pb6QWxqS5k";
    private static ApiClient instance;
    private OkHttpClient client;

    private ApiClient() {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .build();
    }

    public static ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    public OkHttpClient client() {
        return client;
    }

    public Request.Builder requestBuilder(String path) {
        return new Request.Builder()
                .url(BASE_URL + path)
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer " + ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public RequestBody body(String json) {
        return RequestBody.create(json, JSON);
    }
}
