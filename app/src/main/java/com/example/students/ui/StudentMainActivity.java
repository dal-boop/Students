// app/src/main/java/com/example/students/ui/StudentMainActivity.java
package com.example.students.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;  // вот этот импорт!
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.students.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StudentMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        BottomNavigationView nav = findViewById(R.id.studentBottomNav);

        // Устанавливаем стартовый фрагмент — Оценки
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.studentFragmentContainer, new GradesFragment())
                .commit();
        setTitle("Оценки");

        // Слушатель для кликов по нижней панели
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                String title;

                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    fragment = new HomeFragment();
                    title = "Домой";
                } else if (id == R.id.nav_grades) {
                    fragment = new GradesFragment();
                    title = "Оценки";
                } else if (id == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                    title = "Профиль";
                } else {
                    return false;
                }

                // Плавно заменяем фрагмент и меняем заголовок
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.studentFragmentContainer, fragment)
                        .commit();
                setTitle(title);
                return true;
            }
        });
    }
}
