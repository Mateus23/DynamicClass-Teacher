package com.example.mateu.dynamicclass_teacher;

import java.util.List;
import java.util.Map;

public class ChapterInfo {

    private String name;

    private String index;

    private String description;

    private Object Students;

    private List<Map<String, ExerciseInfo>> Exercises;

    private ChapterInfo() {

    }

    public ChapterInfo(String index, String name, String description, Object students){
        this.name = name;
        this.index = index;
        this.Students = students;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getStudents() {
        return Students;

    }

    public void setStudents(Object students) {
        Students = students;
    }

    public List<Map<String, ExerciseInfo>> getExercises() {
        return Exercises;
    }

    public void setExercises(List<Map<String, ExerciseInfo>> exercises) {
        Exercises = exercises;
    }

}
