package com.example.mateu.dynamicclass_teacher;

public class ChapterInfo {

    public String name;

    public String index;

    public String description;

    public Object Students;

    public ChapterInfo() {

    }

    public ChapterInfo(String index, String name, String description, Object students){
        this.name = name;
        this.index = index;
        this.Students = students;
        this.description = description;
    }

}
