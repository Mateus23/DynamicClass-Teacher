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

public class SubjectActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String Database_Path = "Classes/";
    TextView subjectName;
    TextView subjectDescription;
    DataSnapshot subjectSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Bundle b = getIntent().getExtras();
        final String code = b.getString("subjectCode");
        Database_Path = Database_Path + code;

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        subjectName = findViewById(R.id.chapterName);
        subjectDescription = findViewById(R.id.descriptionText);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectActivity.this.finish();
            }
        });

        Button mButtonChapters = (Button) findViewById(R.id.buttonExercises);
        mButtonChapters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonChaptersEvent(code);
            }
        });

        Button mButtonStudents = (Button) findViewById(R.id.buttonStudents);
        mButtonStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStudentsEvent();
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
                subjectSnapshot = dataSnapshot;

                String name = subjectSnapshot.child("name").getValue().toString();
                String code = subjectSnapshot.child("code").getValue().toString();
                String subjectTitle = code + " - " + name;

                Object descriptionObject = subjectSnapshot.child("description").getValue();
                String subjectDescriptionText;
                if (descriptionObject == null){
                    subjectDescriptionText = "";
                }else{
                    subjectDescriptionText = descriptionObject.toString();
                }

                subjectName.setText(subjectTitle);
                subjectDescription.setText(subjectDescriptionText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar a Turma", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void buttonDeletEvent(){
        //TODO
    }

    public void buttonStudentsEvent(){
        //TODO
    }

    public void buttonChaptersEvent(String subjectCode){
        Intent intent = new Intent(this, MyChapters.class);
        intent.putExtra("subjectCode", subjectCode);
        startActivity(intent);
    }
}
