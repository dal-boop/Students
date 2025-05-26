package com.example.students.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String PREF = "student_manage_prefs";
    private SharedPreferences sp;
    public Prefs(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
    public void saveToken(String token) {
        sp.edit().putString("token", token).apply();
    }
    public String getToken() {
        return sp.getString("token", null);
    }
    public void saveRole(String role) {
        sp.edit().putString("role", role).apply();
    }
    public String getRole() {
        return sp.getString("role", null);
    }
    public void clear() {
        sp.edit().clear().apply();
    }
    public void saveEmail(String email) {
        sp.edit().putString("email", email).apply();
    }
    public String getEmail() {
        return sp.getString("email", null);
    }

}
