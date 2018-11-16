package com.example.mateu.dynamicclass_teacher;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterScore {

    private List<Map<String, Integer>> listOfScoreMap = new ArrayList<>();
    private int totalDifficulty;

    public ChapterScore(){

    }

    public ChapterScore(List<Integer> rightScore, List<Integer> wrongScore){
        totalDifficulty = rightScore.size();
        int allRight = 0, allWrong = 0;
        for (int i = 0; i < totalDifficulty; i++){
            allRight += rightScore.get(i);
            allWrong += wrongScore.get(i);
            listOfScoreMap.add(new HashMap<String, Integer>());
            listOfScoreMap.get(i).put("wrong", wrongScore.get(i));
            listOfScoreMap.get(i).put("right", rightScore.get(i));
            listOfScoreMap.get(i).put("total", rightScore.get(i) + wrongScore.get(i));
            listOfScoreMap.get(i).put("percentage", Math.round(100 * rightScore.get(i)/Math.max((rightScore.get(i) + wrongScore.get(i)), 1)));
        }
        listOfScoreMap.add(new HashMap<String, Integer>());
        listOfScoreMap.get(totalDifficulty).put("wrong", allWrong);
        listOfScoreMap.get(totalDifficulty).put("right", allRight);
        listOfScoreMap.get(totalDifficulty).put("total", allWrong + allRight);
        listOfScoreMap.get(totalDifficulty).put("percentage", Math.round(100 * allRight/(allWrong + allRight)));
    }

    public int getWrongOffDifficulty(int difficulty){
        return (int) listOfScoreMap.get(difficulty - 1).get("wrong");
    }

    public int getRightOffDifficulty(int difficulty){
        return (int) listOfScoreMap.get(difficulty - 1).get("right");
    }

    public int getPercentageOffDifficulty(int difficulty){
        return (int) listOfScoreMap.get(difficulty - 1).get("percentage");
    }

    public int getTotalOffDifficulty(int difficulty){
        return (int) listOfScoreMap.get(difficulty - 1).get("total");
    }

    public int getAllWrong(){
        return (int) listOfScoreMap.get(totalDifficulty).get("wrong");
    }

    public int getAllRight(){
        return (int) listOfScoreMap.get(totalDifficulty).get("right");
    }

    public int getAllTotal(){
        return (int) listOfScoreMap.get(totalDifficulty).get("total");
    }

    public int getAllPercentage(){
        return (int) listOfScoreMap.get(totalDifficulty).get("percentage");
    }

}
