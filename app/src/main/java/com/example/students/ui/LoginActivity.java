// app/src/main/java/com/example/students/ui/LoginActivity.java
package com.example.students.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.students.R;
import com.example.students.api.AdminService;
import com.example.students.api.AuthService;
import com.example.students.model.AuthResponse;
import com.example.students.model.User;
import com.example.students.util.Prefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText   etEmail, etPassword;
    private RadioGroup rgRole;
    private Prefs      prefs;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Students);
        setContentView(R.layout.activity_login);

        prefs      = new Prefs(this);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        rgRole     = findViewById(R.id.rgRole);

        findViewById(R.id.btnLogin).setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();

        // 1) Админ-логин
        if ("admin".equals(email) && "admin".equals(pass)) {
            prefs.saveRole("admin");
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            return;
        }

        // 2) Авторизация по e-mail через Supabase Auth
        AuthService.signInAsync(email, pass, new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this,
                                "Сетевая ошибка: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
            @Override public void onResponse(Call call, Response resp) throws IOException {
                String body = resp.body().string();
                int code = resp.code();

                // 400 или 401 — неверные данные или e-mail не подтверждён
                if (code == 400 || code == 401) {
                    runOnUiThread(() -> new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Не удалось войти")
                            .setMessage(
                                    "Код: " + code + "\n" + body +
                                            "\n\nЕсли вы только что зарегистрировались, перейдите по ссылке в письме для подтверждения e-mail."
                            )
                            .setPositiveButton("ОК", null)
                            .show()
                    );
                    return;
                }

                if (!resp.isSuccessful()) {
                    runOnUiThread(() -> new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Ошибка авторизации")
                            .setMessage("Код: " + code + "\n" + body)
                            .setPositiveButton("ОК", null)
                            .show()
                    );
                    return;
                }

                // 3) Парсим access_token
                AuthResponse auth = gson.fromJson(body, AuthResponse.class);
                prefs.saveToken(auth.accessToken);

                // 4) Получаем роль из таблицы users
                AdminService.fetchUserByEmail(email, new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this,
                                        "Не удалось загрузить профиль: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show()
                        );
                    }
                    @Override public void onResponse(Call call, Response r) throws IOException {
                        String b = r.body().string();
                        if (!r.isSuccessful()) {
                            runOnUiThread(() ->
                                    Toast.makeText(LoginActivity.this,
                                            "Ошибка профиля (" + r.code() + "):\n" + b,
                                            Toast.LENGTH_LONG).show()
                            );
                            return;
                        }
                        Type listType = new TypeToken<List<User>>(){}.getType();
                        List<User> users = gson.fromJson(b, listType);
                        if (users.isEmpty()) {
                            runOnUiThread(() ->
                                    Toast.makeText(LoginActivity.this,
                                            "Профиль не найден",
                                            Toast.LENGTH_LONG).show()
                            );
                            return;
                        }
                        User me = users.get(0);
                        prefs.saveRole(me.role);

                        runOnUiThread(() -> {
                            Intent intent;
                            switch (me.role) {
                                case "student":
                                    intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                                    break;
                                case "teacher":
                                    intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                                    break;
                                case "admin":
                                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this,
                                            "Неизвестная роль: " + me.role,
                                            Toast.LENGTH_LONG).show();
                                    return;
                            }
                            startActivity(intent);
                            finish();
                        });
                    }
                });
            }
        });
    }
}
