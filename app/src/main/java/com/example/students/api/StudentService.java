// app/src/main/java/com/example/students/api/StudentService.java
package com.example.students.api;

import com.example.students.model.RawGrade;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class StudentService {
    private static final Gson gson = new Gson();

    /** Получить «сырые» оценки с вложенным subject(name) */
    public static void fetchRawGrades(String studentId, Callback cb) {
        String path = "/rest/v1/grades"
                + "?select=grade_value,subject(name)"
                + "&student_id=eq." + studentId;
        Request req = ApiClient.getInstance()
                .requestBuilder(path)
                .get()
                .build();
        ApiClient.getInstance().client().newCall(req).enqueue(cb);
    }
}
