package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyExercises extends AppCompatActivity {

    DatabaseReference databaseReference;
    public static DataSnapshot chapterSnapshot;
    String Database_Path = "Classes/";
    String subjectCode;
    String chapterIndex;
    TextView chapterName;
    LinearLayout scrollLayout;
    public static String selectedDifficulty = "1";
    ArrayList<String> exercisesList = new ArrayList<String>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exercises);
        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        chapterIndex = b.getString("chapterIndex");

        Database_Path = Database_Path + subjectCode + "/Chapters/" + chapterIndex;

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        chapterName = findViewById(R.id.chapterName);
        scrollLayout = findViewById(R.id.scrollLayout);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExercises.this.finish();
            }
        });

        Button mButtonExercise = (Button) findViewById(R.id.newExerciseButton);
        mButtonExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNewExerciseEvent(subjectCode, chapterIndex);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chapterSnapshot = dataSnapshot;

                String name = chapterSnapshot.child("name").getValue().toString();
                String subjectTitle = "" + chapterIndex + " - " + name;
                chapterName.setText(subjectTitle);

                populateWithExercise(selectedDifficulty);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar o capitulo", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void populateWithExercise(String difficultyIndex){

        DataSnapshot difficultySnapshot = chapterSnapshot.child("/Exercises/" + difficultyIndex);
        int numberOfExerciseViews = scrollLayout.getChildCount();
        long longAux = difficultySnapshot.getChildrenCount();
        int numberOfExercises = Long.valueOf(longAux).intValue();

        if (numberOfExerciseViews > numberOfExercises){
            scrollLayout.removeViews(numberOfExercises,numberOfExerciseViews - numberOfExercises);
        }else if (numberOfExerciseViews < numberOfExercises){
            for (int i = 1; i <= (numberOfExercises - numberOfExerciseViews); i++){
                Button b = new Button(MyExercises.this);
                b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                scrollLayout.addView(b);
            }
        }

        int count = 0;

        exercisesList.clear();

        for (final DataSnapshot postSnapshot : difficultySnapshot.getChildren()) {
            final String exerciseName = postSnapshot.child("name").getValue().toString();
            final String exerciseKey = postSnapshot.getKey();

            View buttonView = scrollLayout.getChildAt(count);
            final Button b = (Button) buttonView;
            b.setText(exerciseName + "  " + exerciseKey);
            b.setId(count);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toExerciseActivity(b.getId());
                }
            });

            exercisesList.add(exerciseKey);

            count = count + 1;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        Log.d("RADIO", "Clicou no radiogroup");

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.difficultyButton1:
                if (checked)
                    populateWithExercise("1");
                selectedDifficulty = "1";
                break;
            case R.id.radioButton2:
                if (checked)
                    populateWithExercise("2");
                selectedDifficulty = "2";
                break;
            case R.id.radioButton3:
                if (checked)
                    populateWithExercise("3");
                selectedDifficulty = "3";
                break;
            case R.id.radioButton4:
                if (checked)
                    populateWithExercise("4");
                selectedDifficulty = "4";
                break;
            case R.id.radioButton5:
                if (checked)
                    populateWithExercise("5");
                selectedDifficulty = "5";
                break;

        }
    }

    public void buttonNewExerciseEvent(String subjectCode, String chapterIndex){
        Intent intent = new Intent(this, NewExercise.class);
        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("chapterIndex", chapterIndex);
        startActivity(intent);
    }

    public static DataSnapshot getExercisesSnapshot(){
        return chapterSnapshot.child("/Exercises/" + selectedDifficulty);
    }

    public void toExerciseActivity(int exerciseIndex){
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra("exerciseDifficulty", selectedDifficulty);
        intent.putExtra("exerciseList", exercisesList);
        intent.putExtra("exerciseIndex", exerciseIndex);
        //intent.putExtra("database_path", Database_Path);
        startActivity(intent);
    }
}
