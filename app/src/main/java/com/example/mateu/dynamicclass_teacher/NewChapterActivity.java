package com.example.mateu.dynamicclass_teacher;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewChapterActivity extends AppCompatActivity {

    private AutoCompleteTextView mChapterName;
    private AutoCompleteTextView mChapterIndex;
    private AutoCompleteTextView mDescription;

    DataSnapshot subjectSnapshot;
    long numberOfChapters;
    String subjectCode;

    // Creating DatabaseReference object.
    DatabaseReference databaseReference;

    // Root Database Name for Firebase Database.
    String Database_Path = "Classes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chapter);

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        Database_Path = Database_Path + subjectCode;
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        // Set up the form.
        mChapterName = findViewById(R.id.chapterName);
        mChapterIndex = findViewById(R.id.chapterIndex);
        mDescription = findViewById(R.id.description);

        Button mCreateChapterButton = (Button) findViewById(R.id.createChapterButton);
        mCreateChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateChapter();
            }
        });

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewChapterActivity.this.finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectSnapshot = dataSnapshot;
                numberOfChapters = subjectSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void attemptCreateChapter() {
        // Reset errors.
        mChapterIndex.setError(null);
        mChapterName.setError(null);
        mDescription.setError(null);

        // Store values at the time of the login attempt.
        String index = mChapterIndex.getText().toString().toUpperCase();
        String description = mDescription.getText().toString();
        String name = mChapterName.getText().toString();
        int indexValue = Integer.valueOf(index);

        boolean cancel = false;

        if (TextUtils.isEmpty(index)) {
            mChapterIndex.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (subjectSnapshot.child("Chapters").hasChild(index)){
            mChapterIndex.setError("Capitulo ja criado com esse numero");
            cancel = true;
        }

        if (indexValue > subjectSnapshot.child("Chapters").getChildrenCount() + 5){
            mChapterIndex.setError("Favor preencher capitulos anteriores antes");
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(name)) {
            mChapterName.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (!cancel) {
            createChapter(index, name, description);
        }
    }


    public void createChapter(final String index, final String name, final String description){
        Object students = subjectSnapshot.child("Students").getValue();
        ChapterInfo chapterInfo = new ChapterInfo(index, name, description, students);
        databaseReference.child("Chapters").child(index).setValue(chapterInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Criada com sucesso", Toast.LENGTH_LONG).show();
                        NewChapterActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Criacao de classe falhou", Toast.LENGTH_LONG).show();
                    }
                });
    }


}
