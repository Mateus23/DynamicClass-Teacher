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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewClassActivity extends AppCompatActivity {

    private AutoCompleteTextView mSubjectCode;
    private AutoCompleteTextView mPassword;
    private AutoCompleteTextView mName;
    private AutoCompleteTextView mDescription;
    private FirebaseAuth mAuth;

    // Creating DatabaseReference object.
    DatabaseReference databaseReference;
    DatabaseReference userDBReference;

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Classes";
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        userDBReference = FirebaseDatabase.getInstance().getReference("Professores/" + currentUser.getUid());

        // Set up the form.
        mSubjectCode = findViewById(R.id.subjectCode);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.chapterName);
        mDescription = findViewById(R.id.description);

        Button mCreateSubjectButton = (Button) findViewById(R.id.createSubjectButton);
        mCreateSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateSubject();
            }
        });

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewClassActivity.this.finish();
            }
        });
    }




    private void attemptCreateSubject() {
        // Reset errors.
        mSubjectCode.setError(null);
        mPassword.setError(null);
        mName.setError(null);
        mDescription.setError(null);

        // Store values at the time of the login attempt.
        final String code = mSubjectCode.getText().toString().toUpperCase();
        String name = mName.getText().toString();
        String description = mDescription.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(code)) {
            mSubjectCode.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (!cancel) {
            tryToCreate(code, password, name, description);
        }
    }

    private void tryToCreate(final String code, final String password, final String name, final String description){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(code)) {
                    mSubjectCode.setError("Codigo ja utilizado");
                }else{
                    createSubject(code, password, name, description, currentUser.getUid());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createSubject(final String code, final String password, final String name, final String description, String teacherId){
        SubjectInfo subjectInfo = new SubjectInfo(code, password, name, description, teacherId);
        databaseReference.child(code).setValue(subjectInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Criada com sucesso", Toast.LENGTH_LONG).show();
                        SubjectInfo teacherSubjectInfo = new SubjectInfo(code, name);
                        userDBReference.child("Classes/" + code).setValue(teacherSubjectInfo);
                        NewClassActivity.this.finish();
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
