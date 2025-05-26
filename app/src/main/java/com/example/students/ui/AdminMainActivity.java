// app/src/main/java/com/example/students/ui/AdminMainActivity.java
package com.example.students.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.students.R;
import com.example.students.api.AdminService;
import com.example.students.api.AuthService;
import com.example.students.model.ClassItem;
import com.example.students.model.SubjectItem;
import com.example.students.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AdminMainActivity extends AppCompatActivity {

    private ExpandableListView expList;
    private List<String> groups;
    private Map<String, List<String>> children;
    private final Gson gson = new Gson();

    private List<ClassItem> classes  = new ArrayList<>();
    private List<SubjectItem> subjects = new ArrayList<>();
    private List<User> students = new ArrayList<>();
    private List<User> teachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_main);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        buildMenu();
        expList = findViewById(R.id.expListView);
        expList.setAdapter(new AdminExpandableAdapter(this, groups, children));

        // ─── автo-раскрытие всех групп и скрытие стрелки
        for (int i = 0; i < groups.size(); i++) {
            expList.expandGroup(i);
        }
        expList.setGroupIndicator(null);
        // ───────────────────────────────────────────────────────

        expList.setOnChildClickListener((p, v, gp, cp, id) -> {
            handle(groups.get(gp), children.get(groups.get(gp)).get(cp));
            return true;
        });

        // Предзагрузка для редактирования/удаления
        loadClasses(null);
        loadSubjects(null);
        loadUsers("student", null);
        loadUsers("teacher", null);
    }

    private void buildMenu() {
        groups = Arrays.asList("Классы","Предметы","Ученики","Учителя");
        children = new LinkedHashMap<>();
        for (String g : groups) {
            children.put(g, Arrays.asList("Добавить","Редактировать","Удалить"));
        }
    }

    private void handle(String group, String action) {
        switch(group) {
            case "Классы":
                if (action.equals("Добавить"))     showAddClass();
                if (action.equals("Редактировать"))loadClasses(this::showEditClass);
                if (action.equals("Удалить"))      loadClasses(this::showDeleteClass);
                break;
            case "Предметы":
                if (action.equals("Добавить"))     showAddSubject();
                if (action.equals("Редактировать"))loadSubjects(this::showEditSubject);
                if (action.equals("Удалить"))      loadSubjects(this::showDeleteSubject);
                break;
            case "Ученики":
                if (action.equals("Добавить")) {
                    if (classes.isEmpty()) loadClasses(this::showAddStudent);
                    else                  showAddStudent();
                }
                if (action.equals("Редактировать")) loadUsers("student", this::showEditStudent);
                if (action.equals("Удалить"))       loadUsers("student", this::showDeleteStudent);
                break;
            case "Учителя":
                if (action.equals("Добавить"))     showAddTeacher();
                if (action.equals("Редактировать"))loadUsers("teacher", this::showEditTeacher);
                if (action.equals("Удалить"))      loadUsers("teacher", this::showDeleteTeacher);
                break;
        }
    }

    // ─── CLASSES ─────────────────────────────────────────

    private void showAddClass() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);
        EditText et = v.findViewById(R.id.etClassName);
        new AlertDialog.Builder(this)
                .setTitle("Новый класс")
                .setView(v)
                .setPositiveButton("OK", (d,w) -> {
                    String name = et.getText().toString().trim();
                    if (name.isEmpty()) return;
                    for (ClassItem c: classes) {
                        if (c.name.equalsIgnoreCase(name)) {
                            t("Класс \"" + name + "\" уже существует");
                            return;
                        }
                    }
                    AdminService.createClass(name, cb("Класс создан", this::refreshClasses));
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditClass() {
        String[] names = classes.stream().map(c->c.name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Редактировать класс")
                .setItems(names, (d,i) -> {
                    ClassItem sel = classes.get(i);
                    View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);
                    EditText et = v.findViewById(R.id.etClassName);
                    et.setText(sel.name);
                    new AlertDialog.Builder(this)
                            .setTitle("Сохранить класс")
                            .setView(v)
                            .setPositiveButton("OK", (d2,w2) -> {
                                String nn = et.getText().toString().trim();
                                if (nn.isEmpty()) return;
                                for (ClassItem c: classes) {
                                    if (c.name.equalsIgnoreCase(nn) && !c.id.equals(sel.id)) {
                                        t("Класс \"" + nn + "\" уже существует");
                                        return;
                                    }
                                }
                                AdminService.updateClass(sel.id, nn, cb("Класс обновлён", this::refreshClasses));
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                })
                .show();
    }

    private void showDeleteClass() {
        String[] names = classes.stream().map(c->c.name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Удалить класс")
                .setItems(names, (d,i) -> {
                    ClassItem sel = classes.get(i);
                    new AlertDialog.Builder(this)
                            .setTitle("Удалить класс?")
                            .setMessage(sel.name)
                            .setPositiveButton("Да", (d2,w2) ->
                                    AdminService.deleteClass(sel.id, cb("Класс удалён", this::refreshClasses))
                            )
                            .setNegativeButton("Нет", null)
                            .show();
                })
                .show();
    }

    private void refreshClasses() {
        loadClasses(null);
    }

    private void loadClasses(Runnable cb) {
        AdminService.fetchClasses(new Callback() {
            @Override public void onFailure(Call c, IOException e) { t("Err: " + e.getMessage()); }
            @Override public void onResponse(Call c, Response r) throws IOException {
                classes = gson.fromJson(r.body().string(),
                        new TypeToken<List<ClassItem>>(){}.getType());
                if (cb!=null) runOnUiThread(cb);
            }
        });
    }

    // ─── SUBJECTS ────────────────────────────────────────

    private void showAddSubject() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null);
        EditText et = v.findViewById(R.id.etSubjectName);
        Spinner sp = v.findViewById(R.id.spinnerClasses);
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getClassNames()));
        new AlertDialog.Builder(this)
                .setTitle("Новый предмет")
                .setView(v)
                .setPositiveButton("OK", (d,w) -> {
                    String name = et.getText().toString().trim();
                    String cid  = classes.get(sp.getSelectedItemPosition()).id;
                    if (name.isEmpty()) return;
                    for (SubjectItem s: subjects) {
                        if (s.name.equalsIgnoreCase(name) && s.class_id.equals(cid)) {
                            t("Предмет \"" + name + "\" уже есть в этом классе");
                            return;
                        }
                    }
                    AdminService.createSubject(name, cid, cb("Предмет создан", this::refreshSubjects));
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditSubject() {
        String[] names = subjects.stream().map(s->s.name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Редактировать предмет")
                .setItems(names, (d,i) -> {
                    SubjectItem sel = subjects.get(i);
                    View vv = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null);
                    EditText et = vv.findViewById(R.id.etSubjectName);
                    Spinner sp = vv.findViewById(R.id.spinnerClasses);
                    et.setText(sel.name);
                    sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getClassNames()));
                    sp.setSelection(getClassNames().indexOf(
                            classes.stream()
                                    .filter(c->c.id.equals(sel.class_id))
                                    .findFirst().map(c->c.name).orElse("")
                    ));
                    new AlertDialog.Builder(this)
                            .setTitle("Сохранить")
                            .setView(vv)
                            .setPositiveButton("OK", (d2,w2) -> {
                                String nn = et.getText().toString().trim();
                                String nc = classes.get(sp.getSelectedItemPosition()).id;
                                if (nn.isEmpty()) return;
                                for (SubjectItem s: subjects) {
                                    if (s.name.equalsIgnoreCase(nn)
                                            && s.class_id.equals(nc)
                                            && !s.id.equals(sel.id)) {
                                        t("Этот предмет уже существует в этом классе");
                                        return;
                                    }
                                }
                                AdminService.updateSubject(sel.id, nn, nc, cb("Предмет обновлён", this::refreshSubjects));
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                })
                .show();
    }

    private void showDeleteSubject() {
        String[] names = subjects.stream().map(s->s.name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Удалить предмет")
                .setItems(names, (d,i) -> {
                    SubjectItem sel = subjects.get(i);
                    new AlertDialog.Builder(this)
                            .setTitle("Удалить предмет?")
                            .setMessage(sel.name)
                            .setPositiveButton("Да", (d2,w2) ->
                                    AdminService.deleteSubject(sel.id, cb("Предмет удалён", this::refreshSubjects))
                            )
                            .setNegativeButton("Нет", null)
                            .show();
                })
                .show();
    }

    private void refreshSubjects() {
        loadSubjects(null);
    }

    private void loadSubjects(Runnable cb) {
        AdminService.fetchSubjects(new Callback() {
            @Override public void onFailure(Call c, IOException e) { t("Err: " + e.getMessage()); }
            @Override public void onResponse(Call c, Response r) throws IOException {
                subjects = gson.fromJson(r.body().string(),
                        new TypeToken<List<SubjectItem>>(){}.getType());
                if (cb!=null) runOnUiThread(cb);
            }
        });
    }

    // ─── STUDENTS ─────────────────────────────────────────────

    private void showAddStudent() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        EditText etN = v.findViewById(R.id.etStudentName),
                etE = v.findViewById(R.id.etStudentEmail),
                etP = v.findViewById(R.id.etStudentPassword);
        Spinner sp  = v.findViewById(R.id.spinnerStudentClass);
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getClassNames()));

        new AlertDialog.Builder(this)
                .setTitle("Новый ученик")
                .setView(v)
                .setPositiveButton("OK", (d,w) -> {
                    String name  = etN.getText().toString().trim();
                    String email = etE.getText().toString().trim();
                    String pass  = etP.getText().toString().trim();
                    String cid   = classes.get(sp.getSelectedItemPosition()).id;
                    if (name.isEmpty()||email.isEmpty()||pass.isEmpty()) return;

                    if (pass.length() < 6) {
                        t("Пароль должен состоять минимум из 6 символов");
                        return;
                    }
                    for (User u : students) {
                        if (u.email.equalsIgnoreCase(email)) {
                            t("Ученик с этим email уже существует");
                            return;
                        }
                    }

                    AuthService.signUpAsync(email, pass, new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> t("Сетевой сбой: " + e.getMessage()));
                        }
                        @Override public void onResponse(Call call, Response resp) throws IOException {
                            String body = resp.body().string();
                            if (resp.code() == 422) {
                                runOnUiThread(() -> t("Email уже зарегистрирован или пароль слишком короткий"));
                                return;
                            }
                            if (resp.code() == 400) {
                                runOnUiThread(() -> t("Bad Request: " + body));
                                return;
                            }
                            if (!resp.isSuccessful()) {
                                runOnUiThread(() -> t("Ошибка регистрации: " + resp.code()));
                                return;
                            }
                            String hash = sha256(pass);
                            AdminService.createUser(
                                    email, hash, name, "student", cid,
                                    cb("Ученик создан", () -> loadUsers("student", null))
                            );
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();  // ← вот сюда!
    }

    private void showEditStudent() {
        String[] names = students.stream().map(u->u.full_name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Редактировать ученика")
                .setItems(names, (d,i) -> {
                    User sel = students.get(i);
                    View vv = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
                    EditText etN = vv.findViewById(R.id.etStudentName),
                            etE = vv.findViewById(R.id.etStudentEmail);
                    Spinner sp  = vv.findViewById(R.id.spinnerStudentClass);
                    etN.setText(sel.full_name);
                    etE.setText(sel.email);
                    sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getClassNames()));
                    sp.setSelection(getClassNames().indexOf(
                            classes.stream()
                                    .filter(c->c.id.equals(sel.class_id))
                                    .findFirst().map(c->c.name).orElse("")
                    ));
                    new AlertDialog.Builder(this)
                            .setTitle("Сохранить")
                            .setView(vv)
                            .setPositiveButton("OK", (d2,w2) -> {
                                String nn   = etN.getText().toString().trim();
                                String ee   = etE.getText().toString().trim();
                                String cid2 = classes.get(sp.getSelectedItemPosition()).id;
                                AdminService.updateUser(sel.id, nn, ee, "student", cid2,
                                        cb("Ученик обновлён", () -> loadUsers("student", null))
                                );
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                })
                .show();
    }

    private void showDeleteStudent() {
        String[] names = students.stream().map(u->u.full_name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Удалить ученика")
                .setItems(names, (d,i) -> {
                    User sel = students.get(i);
                    new AlertDialog.Builder(this)
                            .setTitle("Удалить ученика?")
                            .setMessage(sel.full_name)
                            .setPositiveButton("Да", (d2,w2) ->
                                    AdminService.deleteUser(sel.id, cb("Ученик удалён", () -> loadUsers("student", null)))
                            )
                            .setNegativeButton("Нет", null)
                            .show();
                })
                .show();
    }

    // ─── TEACHERS ─────────────────────────────────────────────────

    private void showAddTeacher() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_teacher, null);
        EditText etName  = v.findViewById(R.id.etTeacherName);
        EditText etEmail = v.findViewById(R.id.etTeacherEmail);
        EditText etPass  = v.findViewById(R.id.etTeacherPassword);

        new AlertDialog.Builder(this)
                .setTitle("Новый учитель")
                .setView(v)
                .setPositiveButton("OK", (d, w) -> {
                    String name  = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String pass  = etPass.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) return;
                    if (pass.length() < 6) {
                        t("Пароль должен быть не менее 6 символов");
                        return;
                    }
                    for (User u : teachers) {
                        if (u.email.equalsIgnoreCase(email)) {
                            t("Учитель с таким email уже существует");
                            return;
                        }
                    }

                    AuthService.signUpAsync(email, pass, new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> t("Сетевой сбой: " + e.getMessage()));
                        }
                        @Override public void onResponse(Call call, Response resp) throws IOException {
                            String body = resp.body().string();
                            if (resp.code() == 422) {
                                runOnUiThread(() -> t("Email уже зарегистрирован или пароль слишком слабый"));
                                return;
                            }
                            if (resp.code() == 400) {
                                runOnUiThread(() -> t("Bad Request: " + body));
                                return;
                            }
                            if (!resp.isSuccessful()) {
                                runOnUiThread(() -> t("Ошибка регистрации: " + resp.code()));
                                return;
                            }
                            String hash = sha256(pass);
                            AdminService.createUser(
                                    email, hash, name, "teacher", null,
                                    cb("Учитель создан", () -> loadUsers("teacher", null))
                            );
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();  // ← и сюда!
    }

    private void showEditTeacher() {
        String[] names = teachers.stream().map(u->u.full_name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Редактировать учителя")
                .setItems(names, (d,i) -> {
                    User sel = teachers.get(i);
                    View vv = LayoutInflater.from(this).inflate(R.layout.dialog_add_teacher, null);
                    EditText etN = vv.findViewById(R.id.etTeacherName),
                            etE = vv.findViewById(R.id.etTeacherEmail);
                    etN.setText(sel.full_name);
                    etE.setText(sel.email);
                    new AlertDialog.Builder(this)
                            .setTitle("Сохранить")
                            .setView(vv)
                            .setPositiveButton("OK", (d2,w2) -> {
                                String nn = etN.getText().toString().trim();
                                String ee = etE.getText().toString().trim();
                                AdminService.updateUser(sel.id, nn, ee, "teacher", null,
                                        cb("Учитель обновлён", () -> loadUsers("teacher", null))
                                );
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                })
                .show();
    }

    private void showDeleteTeacher() {
        String[] names = teachers.stream().map(u->u.full_name).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Удалить учителя")
                .setItems(names, (d,i) -> {
                    User sel = teachers.get(i);
                    new AlertDialog.Builder(this)
                            .setTitle("Удалить учителя?")
                            .setMessage(sel.full_name)
                            .setPositiveButton("Да", (d2,w2) ->
                                    AdminService.deleteUser(sel.id, cb("Учитель удалён", () -> loadUsers("teacher", null)))
                            )
                            .setNegativeButton("Нет", null)
                            .show();
                })
                .show();
    }

    private void loadUsers(String role, Runnable cb) {
        AdminService.fetchUsersByRole(role, new Callback() {
            @Override public void onFailure(Call c, IOException e) { t("Err: " + e.getMessage()); }
            @Override public void onResponse(Call c, Response r) throws IOException {
                Type t = new TypeToken<List<User>>(){}.getType();
                List<User> list = gson.fromJson(r.body().string(), t);
                if (role.equals("student")) students = list; else teachers = list;
                if (cb!=null) runOnUiThread(cb);
            }
        });
    }

    // ────────────────────────────────────────────────────────

    private Callback cb(String msg, Runnable reload) {
        return new Callback() {
            @Override public void onFailure(Call c, IOException e) { t(e.getMessage()); }
            @Override public void onResponse(Call c, Response r) {
                runOnUiThread(() -> {
                    if (r.isSuccessful()) {
                        t(msg);
                        if (reload!=null) reload.run();
                    } else {
                        t("Err: " + r.code());
                    }
                });
            }
        };
    }

    private void t(String s) {
        runOnUiThread(() -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());
    }

    private List<String> getClassNames() {
        List<String> L = new ArrayList<>();
        for (ClassItem c: classes) L.add(c.name);
        return L;
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b: h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
