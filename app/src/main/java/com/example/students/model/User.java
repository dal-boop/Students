// app/src/main/java/com/example/students/model/User.java
package com.example.students.model;

public class User {
    public String id;
    public String email;
    public String full_name;   // <-- именно так приходит из БД
    public String role;
    public String class_id;

    // если вы хотите подтягивать сразу имя класса через nested select
    public ClassItem classes;  // Supabase вернёт объект { "name": "10А" }
}
