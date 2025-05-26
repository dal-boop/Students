package com.example.students.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("user")
    public User user;
}
