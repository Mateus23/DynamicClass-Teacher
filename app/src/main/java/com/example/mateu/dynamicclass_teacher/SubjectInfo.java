package com.example.mateu.dynamicclass_teacher;

public class SubjectInfo {

    public String name;

    public String code;

    public String password;

    public String description;

    public String teacher;


    public SubjectInfo() {

    }

    public SubjectInfo(String code, String name){
        this.name = name;
        this.code = code;
    }


    public SubjectInfo(String code, String password, String name, String description, String teacherId){
        this.name = name;
        this.code = code;
        this.password = password;
        this.description = description;
        this.teacher = teacherId;

    }
}
