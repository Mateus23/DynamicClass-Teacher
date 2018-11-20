package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class SubjectStudentsActivity extends AppCompatActivity {

    String subjectCode;
    String database_path;
    DatabaseReference databaseReference;
    MaterialSpinner mySpinner;
    Boolean isReady = false;
    List<String> studentUID  = new ArrayList<>(), studentIndex = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_students);

        mySpinner = findViewById(R.id.studentsSpinner);

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectStudentsActivity.this.finish();
            }
        });

        Button mButtonProfile = (Button) findViewById(R.id.profileButton);
        mButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toProfileActivity();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        database_path = "Classes/" + subjectCode + "/Students";
        databaseReference = FirebaseDatabase.getInstance().getReference(database_path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> studentsName = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    studentsName.add(postSnapshot.child("index").getValue().toString() + " - " + postSnapshot.child("name").getValue().toString());
                    studentUID.add(postSnapshot.getKey());
                    studentIndex.add(postSnapshot.child("index").getValue().toString());
                }
                mySpinner.setItems(studentsName);
                isReady = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar a Turma", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void toProfileActivity(){
        int spinnerPosition = mySpinner.getSelectedIndex();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("userUID", studentUID.get(spinnerPosition));
        intent.putExtra("subjectIndex", studentIndex.get(spinnerPosition));
        intent.putExtra("typeOfUser", "Students");
        startActivity(intent);
    }
}
