package com.example.mateu.dynamicclass_teacher;

import java.util.List;

public class ExerciseInfo {

    public String name;
    public String difficulty;
    public String descriptionText;
    public String imageURL;
    public List<String> Answer;

    public ExerciseInfo(){

    }

    public ExerciseInfo(String name, String difficulty, String descriptionText, List<String> answer){
        this.name = name;
        this.difficulty = difficulty;
        this.descriptionText = descriptionText;
        this.Answer = answer;
    }

    public ExerciseInfo(String name, String difficulty, String descriptionText, String imageURL, List<String> answer){
        this.name = name;
        this.difficulty = difficulty;
        this.descriptionText = descriptionText;
        this.imageURL = imageURL;
        this.Answer = answer;
    }

}
