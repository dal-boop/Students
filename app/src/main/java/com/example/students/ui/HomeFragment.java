// app/src/main/java/com/example/students/ui/HomeFragment.java
package com.example.students.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.students.R;

public class HomeFragment extends Fragment {
    private TextView tvGreeting, tvClass, tvAvgScore, tvSubjectCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater li, ViewGroup cg, Bundle b) {
        return li.inflate(R.layout.fragment_home, cg, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle b) {
        tvGreeting     = v.findViewById(R.id.tvGreeting);
        tvClass        = v.findViewById(R.id.tvClass);
        tvAvgScore     = v.findViewById(R.id.tvAvgScore);
        tvSubjectCount = v.findViewById(R.id.tvSubjectCount);

        // TODO: загрузить реальные данные из ViewModel/Repository
        tvGreeting.setText("Привет, Иван!");
        tvClass.setText("Класс 10 А");
        tvAvgScore.setText("4.3");
        tvSubjectCount.setText("7");
    }
}
