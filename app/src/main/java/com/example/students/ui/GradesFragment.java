// app/src/main/java/com/example/students/ui/GradesFragment.java
package com.example.students.ui;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.students.R;
import com.example.students.api.StudentService;
import com.example.students.model.*;
import com.example.students.util.Prefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class GradesFragment extends Fragment {
    private RecyclerView rv;
    private GradesAdapter adapter;
    private Prefs prefs;
    private final Gson gson = new Gson();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup cg, Bundle b) {
        return inf.inflate(R.layout.fragment_grades, cg, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle b) {
        prefs = new Prefs(requireContext());
        rv = v.findViewById(R.id.rvGrades);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GradesAdapter();
        rv.setAdapter(adapter);

        // 1) узнаём ID текущего пользователя по email
        String email = prefs.getEmail();
        StudentService.fetchUserByEmail(email, new Callback() {
            @Override public void onFailure(Call c, IOException e) { /* TODO: Toast */ }
            @Override public void onResponse(Call c, Response r) throws IOException {
                Type ut = new TypeToken<List<User>>(){}.getType();
                List<User> users = gson.fromJson(r.body().string(), ut);
                if (users.isEmpty()) return;
                String sid = users.get(0).id;

                // 2) получаем «сырые» оценки
                StudentService.fetchRawGrades(sid, new Callback() {
                    @Override public void onFailure(Call c2, IOException e2) { /* TODO */ }
                    @Override public void onResponse(Call c2, Response r2) throws IOException {
                        Type rt = new TypeToken<List<RawGrade>>(){}.getType();
                        List<RawGrade> raw = gson.fromJson(r2.body().string(), rt);
                        // 3) группируем по предмету
                        Map<String, List<Integer>> map = new LinkedHashMap<>();
                        for (RawGrade rg : raw) {
                            String subj = rg.subject.get("name");
                            map.computeIfAbsent(subj, k->new ArrayList<>())
                                    .add(rg.grade_value);
                        }
                        // 4) собираем GradeItem
                        List<GradeItem> out = new ArrayList<>();
                        for (Map.Entry<String,List<Integer>> en : map.entrySet()) {
                            List<Integer> gl = en.getValue();
                            int[] arr = gl.stream().mapToInt(i->i).toArray();
                            float avg = (float)gl.stream().mapToInt(i->i).average().orElse(0);
                            out.add(new GradeItem(en.getKey(), arr, avg));
                        }
                        // 5) показываем
                        requireActivity().runOnUiThread(() -> adapter.setData(out));
                    }
                });
            }
        });
    }
}
