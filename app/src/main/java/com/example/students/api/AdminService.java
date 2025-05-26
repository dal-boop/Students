// app/src/main/java/com/example/students/api/AdminService.java
package com.example.students.api;

import android.os.Build;

import com.example.students.model.ClassItem;
import com.example.students.model.SubjectItem;
import com.example.students.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {
    private static final Gson gson = new Gson();
    private static final String BASE = "/rest/v1";

    private static OkHttpClient client() {
        return ApiClient.getInstance().client();
    }

    private static Request.Builder rb(String path) {
        return ApiClient.getInstance().requestBuilder(BASE + path)
                .addHeader("Prefer", "return=representation");
    }

    // ==== FETCH ====
    public static void fetchClasses(Callback cb) {
        Request r = rb("/classes?select=id,name").get().build();
        client().newCall(r).enqueue(cb);
    }
    public static void fetchSubjects(Callback cb) {
        Request r = rb("/subjects?select=id,name,class_id").get().build();
        client().newCall(r).enqueue(cb);
    }
    public static void fetchUsersByRole(String role, Callback cb) {
        Request r = rb("/users?select=id,full_name,email,role,class_id&role=eq." + role)
                .get().build();
        client().newCall(r).enqueue(cb);
    }

    // ==== CREATE ====
    public static void createClass(String name, Callback cb) {
        Map<String,String> b = new HashMap<>(); b.put("name", name);
        Request r = rb("/classes")
                .post(ApiClient.getInstance().body(gson.toJson(b)))
                .build();
        client().newCall(r).enqueue(cb);
    }
    public static void createSubject(String name, String classId, Callback cb) {
        Map<String,Object> b = new HashMap<>();
        b.put("name", name);
        b.put("class_id", classId);
        Request r = rb("/subjects")
                .post(ApiClient.getInstance().body(gson.toJson(b)))
                .build();
        client().newCall(r).enqueue(cb);
    }
    public static void createUser(String email, String passHash, String fullName,
                                  String role, String classId, Callback cb) {
        Map<String,Object> b = new HashMap<>();
        b.put("email", email);
        b.put("password_hash", passHash);
        b.put("full_name", fullName);
        b.put("role", role);
        if (classId != null) b.put("class_id", classId);
        Request r = rb("/users")
                .post(ApiClient.getInstance().body(gson.toJson(b)))
                .build();
        client().newCall(r).enqueue(cb);
    }

    // ==== UPDATE ====
    public static void updateClass(String id, String newName, Callback cb) {
        Map<String,String> b = new HashMap<>(); b.put("name", newName);
        Request r = rb("/classes?id=eq." + id)
                .patch(ApiClient.getInstance().body(gson.toJson(b)))
                .build();
        client().newCall(r).enqueue(cb);
    }
    public static void updateSubject(String id, String newName, String classId, Callback cb) {
        Map<String,Object> b = new HashMap<>();
        b.put("name", newName);
        b.put("class_id", classId);
        Request r = rb("/subjects?id=eq." + id)
                .patch(ApiClient.getInstance().body(gson.toJson(b)))
                .build();
        client().newCall(r).enqueue(cb);
    }
    public static void updateUser(String id, String fullName, String email,
                                  String role, String classId, Callback cb) {
        Map<String,Object> b = new HashMap<>();
        b.put("full_name", fullName);
        b.put("email", email);
        b.put("role", role);
        b.put("class_id", classId); // null если нужен null
        Request r = rb("/users?id=eq." + id)
                .patch(ApiClient.getInstance().body(gson.toJson(b)))
                .build();
        client().newCall(r).enqueue(cb);
    }

    // ==== DELETE ====
    public static void deleteClass(String id, Callback cb) {
        Request r = rb("/classes?id=eq." + id)
                .delete()
                .build();
        client().newCall(r).enqueue(cb);
    }
    public static void deleteSubject(String id, Callback cb) {
        Request r = rb("/subjects?id=eq." + id)
                .delete()
                .build();
        client().newCall(r).enqueue(cb);
    }
    public static void deleteUser(String id, Callback cb) {
        Request r = rb("/users?id=eq." + id)
                .delete()
                .build();
        client().newCall(r).enqueue(cb);
    }

    public static void fetchUserByEmail(String email, Callback cb) {
        String encoded = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        }
        String path = "/rest/v1/users?select=id,email,full_name,role,class_id"
                + "&email=eq." + encoded;
        Request req = ApiClient.getInstance()
                .requestBuilder(path)
                .get()
                .build();
        ApiClient.getInstance().client().newCall(req).enqueue(cb);
    }
}
