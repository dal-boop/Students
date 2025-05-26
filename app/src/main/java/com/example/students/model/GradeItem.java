// app/src/main/java/com/example/students/model/GradeItem.java
package com.example.students.model;

public class GradeItem {
    public String subjectName;
    public int[] grades;     // например {2,3,4,5}
    public float average;    // например 3.5f

    public GradeItem(String subjectName, int[] grades, float average) {
        this.subjectName = subjectName;
        this.grades = grades;
        this.average = average;
    }
}
