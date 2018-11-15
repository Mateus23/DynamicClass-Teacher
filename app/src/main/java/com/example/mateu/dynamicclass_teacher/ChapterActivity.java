package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChapterActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    DataSnapshot chapterSnapshot;
    String Database_Path = "Classes/";
    String subjectCode;
    String chapterIndex;
    TextView chapterName;
    TextView chapterDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        chapterIndex = b.getString("chapterIndex");
        Database_Path = Database_Path + subjectCode + "/Chapters/" + chapterIndex;

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        chapterName = findViewById(R.id.chapterName);
        chapterDescription = findViewById(R.id.descriptionText);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChapterActivity.this.finish();
            }
        });

        Button mButtonExercises = (Button) findViewById(R.id.buttonExercises);
        mButtonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonExercisesEvent(subjectCode, chapterIndex);
            }
        });

        Button mButtonApplyExercises = (Button) findViewById(R.id.buttonApplyExercises);
        mButtonApplyExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonApplyExercisesEvent();
            }
        });

        Button mButtonDelete = (Button) findViewById(R.id.buttonDelete);
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonDeletEvent();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chapterSnapshot = dataSnapshot;

                String name = chapterSnapshot.child("name").getValue().toString();
                String subjectTitle = "" + chapterIndex + " - " + name;

                Object descriptionObject = chapterSnapshot.child("description").getValue();
                String subjectDescriptionText;
                if (descriptionObject == null){
                    subjectDescriptionText = "";
                }else{
                    subjectDescriptionText = descriptionObject.toString();
                }

                chapterName.setText(subjectTitle);
                chapterDescription.setText(subjectDescriptionText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar o capitulo", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void buttonDeletEvent(){
        //TODO
    }

    public void buttonApplyExercisesEvent(){
        Intent intent = new Intent(this, NewChapterRoutine.class);
        long[] myArray = new long[5];

        for (int i = 1; i <= 5; i++){
            long aux = chapterSnapshot.child("Exercises").child(String.valueOf(i)).getChildrenCount();
            myArray[i - 1] = aux;
        }

        intent.putExtra("numberOfExercises", myArray);
        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("chapterIndex", chapterIndex);
        startActivity(intent);
    }

    public void buttonExercisesEvent(String subjectCode, String chapterIndex){
        Intent intent = new Intent(this, MyExercises.class);
        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("chapterIndex", chapterIndex);
        startActivity(intent);
    }
}
