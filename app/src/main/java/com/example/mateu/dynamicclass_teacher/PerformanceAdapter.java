package com.example.mateu.dynamicclass_teacher;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerformanceAdapter {

    private String database_path;
    boolean isReady = false;
    private List<List<ChapterScore>> listChapterStudents = new ArrayList<>();
    private List<ChapterScore> listOfAllStudents = new ArrayList<>();
    private List<String> chaptersName = new ArrayList<>();
    private List<String> studentsName = new ArrayList<>();
    private List<String> studentsKey = new ArrayList<>();
    private List<Integer> defaultList = new ArrayList<>();

    public PerformanceAdapter(String subjectCode){
        this.database_path = "Classes/" + subjectCode;
        DatabaseReference subjectReference = FirebaseDatabase.getInstance().getReference(database_path);

        for (int i = 0; i < 5; i++){
            defaultList.add(0);
        }

        subjectReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot chaptersSnapshot = dataSnapshot.child("Chapters");
                for (DataSnapshot individualChapter : chaptersSnapshot.getChildren()){
                    String chapterName = individualChapter.child("name").getValue().toString();
                    String chapterIndex = individualChapter.getKey();
                    chaptersName.add(chapterIndex + " - " + chapterName);
                }
                DataSnapshot studentsSnapshot = dataSnapshot.child("Students");
                for (DataSnapshot individualStudent : studentsSnapshot.getChildren()){
                    String studentName = individualStudent.child("name").getValue().toString();
                    studentsName.add(String.valueOf(studentsName.size() + 1) + " - " + studentName);
                    studentsKey.add(individualStudent.getKey());
                }
                int count = 0;
                for (DataSnapshot individualChapter : chaptersSnapshot.getChildren()){
                    listChapterStudents.add(new ArrayList<ChapterScore>());
                    Integer[] allClassRight = {0, 0, 0, 0, 0};
                    Integer[] allClassWrong = {0, 0, 0, 0, 0};
                    for (String studentKey : studentsKey) {
                        if (individualChapter.child("Students").child(studentKey).exists()){
                            List<Integer> wrongAnswer = new ArrayList<>();
                            List<Integer> rightAnswer = new ArrayList<>();
                            DataSnapshot doneExercises = individualChapter.child("Students").child(studentKey).child("DoneExercises");
                            for (int i = 1; i <= 5; i++){
                                if (doneExercises.child(String.valueOf(i)).child("wrong").exists()) {
                                    wrongAnswer.add(Integer.valueOf(doneExercises.child(String.valueOf(i)).child("wrong").getValue().toString()));
                                    allClassWrong[i - 1] += Integer.valueOf(doneExercises.child(String.valueOf(i)).child("wrong").getValue().toString());
                                }else {
                                    wrongAnswer.add(0);
                                }
                                if (doneExercises.child(String.valueOf(i)).child("right").exists()) {
                                    rightAnswer.add(Integer.valueOf(doneExercises.child(String.valueOf(i)).child("right").getValue().toString()));
                                    allClassRight[i - 1] += Integer.valueOf(doneExercises.child(String.valueOf(i)).child("right").getValue().toString());
                                }else{
                                    rightAnswer.add(0);
                                }
                            }
                            listChapterStudents.get(count).add(new ChapterScore(rightAnswer, wrongAnswer));
                        }else{
                            listChapterStudents.get(count).add(new ChapterScore(defaultList, defaultList));
                        }
                    }
                    count++;
                    listOfAllStudents.add(new ChapterScore(Arrays.asList(allClassRight), Arrays.asList(allClassWrong)));
                }
                isReady = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public int[] getRightAndWrongForChapterStudent(int studentIndex, int chapterIndex){
        int rightAnswers, wrongAnswers;
        rightAnswers = listChapterStudents.get(chapterIndex).get(studentIndex).getAllRight();
        wrongAnswers = listChapterStudents.get(chapterIndex).get(studentIndex).getAllWrong();
        return new int[]{rightAnswers, wrongAnswers};
    }

    public int[] getRightAndWrongForChapterStudentAndDifficulty(int studentIndex, int chapterIndex, int difficulty){
        int rightAnswers, wrongAnswers;
        rightAnswers = listChapterStudents.get(chapterIndex).get(studentIndex).getRightOffDifficulty(difficulty);
        wrongAnswers = listChapterStudents.get(chapterIndex).get(studentIndex).getWrongOffDifficulty(difficulty);
        return new int[]{rightAnswers, wrongAnswers};
    }

    public int[] getRightAndWrongForChapterAllStudentsAndDifficulty(int chapterIndex, int difficulty){
        int rightAnswers, wrongAnswers;
        rightAnswers = listOfAllStudents.get(chapterIndex).getRightOffDifficulty(difficulty);
        wrongAnswers = listOfAllStudents.get(chapterIndex).getWrongOffDifficulty(difficulty);;
        return new int[]{rightAnswers, wrongAnswers};
    }

    public int[] getRightAndWrongForChapterAllStudents(int chapterIndex){
        int rightAnswers, wrongAnswers;
        rightAnswers = listOfAllStudents.get(chapterIndex).getAllRight();
        wrongAnswers = listOfAllStudents.get(chapterIndex).getAllWrong();;
        return new int[]{rightAnswers, wrongAnswers};
    }

    public List<Integer> getPercentageOfRightAnswersForAllChaptersStudentAndDIfficulty(int studentIndex, int difficulty){
        List<Integer> percentageList = new ArrayList<>();
        for (int i = 0; i < listChapterStudents.size(); i++){
            percentageList.add(listChapterStudents.get(i).get(studentIndex).getPercentageOffDifficulty(difficulty));
        }
        return percentageList;
    }

    public List<Integer> getPercentageOfRightAnswersForAllChaptersStudent(int studentIndex){
        List<Integer> percentageList = new ArrayList<>();
        for (int i = 0; i < listChapterStudents.size(); i++){
            percentageList.add(listChapterStudents.get(i).get(studentIndex).getAllPercentage());
        }
        return percentageList;
    }

    public List<Integer> getPercentageOfAllClassForDifficulty(int difficulty){
        List<Integer> percentageList = new ArrayList<>();
        for (int i = 0; i < listOfAllStudents.size(); i++){
            percentageList.add(listOfAllStudents.get(i).getPercentageOffDifficulty(difficulty));
        }
        return percentageList;
    }

    public List<Integer> getPercentageOfAllClass(){
        List<Integer> percentageList = new ArrayList<>();
        for (int i = 0; i < listOfAllStudents.size(); i++){
            percentageList.add(listOfAllStudents.get(i).getAllPercentage());
        }
        return percentageList;
    }

    public List<String> getListOfChaptersName(){
        return chaptersName;
    }

    public List<String> getListOfStudentsName(){
        return studentsName;
    }

}
