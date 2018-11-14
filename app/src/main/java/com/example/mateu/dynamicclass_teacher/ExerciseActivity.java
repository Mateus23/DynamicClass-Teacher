package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class ExerciseActivity extends AppCompatActivity {

    LinearLayout scrollLayout;
    Button backButton, nextButton, previousButton, deleteButton;
    TextView difficultyText, nameText;
    DataSnapshot exercisesDataSnapshot;
    String exerciseDifficulty, exerciseKey;
    ArrayList<String> exercisesKeyList;
    String exactAnswer = "Resposta: ";
    String[] rangeAnswer = {"Minimo: ", "Maximo: "};
    String[] alternativeAnswer = {"Resposta: ", "Alternativa: ", "Alternativa: ", "Alternativa: ", "Alternativa: "};
    int exerciseIndex;
    long numberOfAnswers;
    ImagePopup imagePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Bundle b = getIntent().getExtras();
        exerciseIndex = b.getInt("exerciseIndex");
        exerciseDifficulty = b.getString("exerciseDifficulty");
        exercisesKeyList = b.getStringArrayList("exerciseList");
        exercisesKeyList.trimToSize();
        exercisesDataSnapshot = MyExercises.getExercisesSnapshot();
        exerciseKey = exercisesKeyList.get(exerciseIndex);

        nextButton = (Button)findViewById(R.id.nextButton);
        previousButton = (Button)findViewById(R.id.previousButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        difficultyText = (TextView)findViewById(R.id.difficultyText);
        nameText = (TextView)findViewById(R.id.nameText);
        numberOfAnswers = exercisesDataSnapshot.child(exerciseKey).child("Answer").getChildrenCount();
        scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);
        imagePopup = new ImagePopup(ExerciseActivity.this);
        imagePopup.setBackgroundColor(Color.BLACK);
        imagePopup.setImageOnClickClose(true);
        imagePopup.setFullScreen(true);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNextExerise();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreviousExercise();
            }
        });

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseActivity.this.finish();
            }
        });

        if (exercisesDataSnapshot.child(exerciseKey).child("descriptionText").exists()){
            createDescriptionText();
        }
        if(exercisesDataSnapshot.child(exerciseKey).child("imageURL").exists()){
            createImage();
        }

        createAnswerText();

    }

    @Override
    public void onStart() {
        super.onStart();
        String name = exercisesDataSnapshot.child(exerciseKey).child("name").getValue().toString();
        difficultyText.setText("Dificuldade: " + exerciseDifficulty);
        nameText.setText(name);

    }

    public void createDescriptionText(){
        String descriptionText = exercisesDataSnapshot.child(exerciseKey).child("descriptionText").getValue().toString();

        TextView descriptionTextView = new TextView(ExerciseActivity.this);
        descriptionTextView.setText(descriptionText);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        descriptionTextView.setLayoutParams(lp);
        descriptionTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionTextView.setPadding(0, 50, 0, 0);
        descriptionTextView.setTextColor(Color.BLACK);
        scrollLayout.addView(descriptionTextView);
    }

    public void createImage(){
        //Loading image from Glide library.
        String imageURL = exercisesDataSnapshot.child(exerciseKey).child("imageURL").getValue().toString();
        ImageView exerciseImage = new ImageView(ExerciseActivity.this);
        Glide.with(ExerciseActivity.this).load(imageURL).into(exerciseImage);
        exerciseImage.setPadding(0, 50, 0, 0);
        imagePopup.initiatePopupWithGlide(imageURL);
        exerciseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePopup.viewPopup();
            }
        });
        scrollLayout.addView(exerciseImage);
    }

    public void createAnswerText(){ ;
        String answerText;
        DataSnapshot answerSnapshot = exercisesDataSnapshot.child(exerciseKey).child("Answer");
        if (numberOfAnswers == 1){
            answerText = exactAnswer + answerSnapshot.child("0").getValue().toString();
        }else if(numberOfAnswers == 2){
            answerText = rangeAnswer[0] + answerSnapshot.child("0").getValue().toString() + "\n";
            answerText = answerText + rangeAnswer[1] + answerSnapshot.child("0").getValue().toString();
        }else{
            answerText = "";
            for (int i = 0; i < numberOfAnswers; i++){
                answerText = answerText + alternativeAnswer[i] +  answerSnapshot.child(String.valueOf(i)).getValue().toString() + "\n";
            }
        }

        TextView answerTextView = new TextView(ExerciseActivity.this);
        answerTextView.setText(answerText);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        answerTextView.setLayoutParams(lp);
        answerTextView.setPadding(0, 50, 0, 0);
        answerTextView.setTextColor(Color.BLACK);
        answerTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollLayout.addView(answerTextView);
    }

    public void toExercise(int newExerciseIndex){
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra("exerciseDifficulty", exerciseDifficulty);
        intent.putExtra("exerciseList", exercisesKeyList);
        intent.putExtra("exerciseIndex", newExerciseIndex);
        ExerciseActivity.this.finish();
        startActivity(intent);
    }

    public void toNextExerise(){
        int newExerciseindex;
        if (exerciseIndex == exercisesKeyList.size() - 1){
            newExerciseindex = 0;
        }else{
            newExerciseindex = exerciseIndex + 1;
        }
        toExercise(newExerciseindex);
    }

    public void toPreviousExercise(){
        int newExerciseindex;
        if (exerciseIndex == 0){
            newExerciseindex = exercisesKeyList.size() - 1;
        }else{
            newExerciseindex = exerciseIndex - 1;
        }
        toExercise(newExerciseindex);

    }
}
