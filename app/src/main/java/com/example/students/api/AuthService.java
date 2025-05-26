// app/src/main/java/com/example/students/api/AuthService.java
package com.example.students.api;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class AuthService {
    private static final Gson gson = new Gson();

    /** Асинхронная регистрация в Supabase Auth */
    public static void signUpAsync(String email, String password, Callback cb) {
        // 1) Только email+password
        String json = gson.toJson(new Credentials(email, password));
        Request req = ApiClient.getInstance()
                .requestBuilder("/auth/v1/signup")
                .post(ApiClient.getInstance().body(json))
                .build();

        // 2) Логируем и проксируем ответ в cb
        ApiClient.getInstance().client().newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onFailure(call, e);
            }
            @Override public void onResponse(Call call, Response resp) throws IOException {
                String body = resp.body().string();
                System.out.println("SIGNUP code=" + resp.code() + " body=" + body);
                // Собираем новый Response с тем же code и тем же body
                Response proxy = resp.newBuilder()
                        .body(ResponseBody.create(body, resp.body().contentType()))
                        .message(body)
                        .build();
                cb.onResponse(call, proxy);
            }
        });
    }

    /** Асинхронный вход через Supabase Auth */
    public static void signInAsync(String email, String password, Callback cb) {
        String json = gson.toJson(new Credentials(email, password));
        Request req = ApiClient.getInstance()
                .requestBuilder("/auth/v1/token?grant_type=password")
                .post(ApiClient.getInstance().body(json))
                .build();

        ApiClient.getInstance().client().newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                cb.onFailure(call, e);
            }
            @Override public void onResponse(Call call, Response resp) throws IOException {
                String body = resp.body().string();
                System.out.println("SIGNIN code=" + resp.code() + " body=" + body);
                Response proxy = resp.newBuilder()
                        .body(ResponseBody.create(body, resp.body().contentType()))
                        .message(body)
                        .build();
                cb.onResponse(call, proxy);
            }
        });
    }

    /** Только эти два поля! */
    private static class Credentials {
        final String email, password;
        Credentials(String e, String p) { email = e; password = p; }
    }
}
