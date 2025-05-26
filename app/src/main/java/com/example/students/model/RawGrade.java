// app/src/main/java/com/example/students/model/RawGrade.java
package com.example.students.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class RawGrade {
    @SerializedName("grade_value") public int grade_value;
    public Map<String,String> subject;
    // subject.get("name") — это название предмета
}
