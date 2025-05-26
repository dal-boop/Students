// app/src/main/java/com/example/students/ui/ProfileFragment.java
package com.example.students.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.students.R;
import com.example.students.util.Prefs;

public class ProfileFragment extends Fragment {
    private TextView tvName, tvEmail, tvClass;
    private Prefs prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater li, ViewGroup cg, Bundle b) {
        prefs = new Prefs(requireContext());
        return li.inflate(R.layout.fragment_profile, cg, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle b) {
        tvName  = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvClass = v.findViewById(R.id.tvClassInfo);

        // TODO: загрузить данные пользователя из Prefs/Repository
        tvName.setText("Иван Иванов");
        tvEmail.setText("ivanov@mail.ru");
        tvClass.setText("Класс 10 А");

        v.findViewById(R.id.btnChangePassword).setOnClickListener(x -> {
            // TODO: диалог смены пароля
        });
        v.findViewById(R.id.btnLogout).setOnClickListener(x -> {
            prefs.clear(); // метод очистки токена/роли
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
