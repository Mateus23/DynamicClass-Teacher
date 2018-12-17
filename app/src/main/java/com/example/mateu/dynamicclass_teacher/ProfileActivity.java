package com.example.mateu.dynamicclass_teacher;

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

public class ProfileActivity extends AppCompatActivity {

    String database_path = "Professores/";
    String userUID;
    DatabaseReference databaseReference;
    TextView nameView, emailView, phoneView, titleView, subjectIDView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle b = getIntent().getExtras();
        String typeOfUser = b.getString("typeOfUser");
        userUID = b.getString("userUID");
        String subjectIndex = b.getString("subjectIndex");

        nameView = findViewById(R.id.nameTextVIew);
        titleView = findViewById(R.id.typeOfUserTextView);
        emailView = findViewById(R.id.emailTextView);
        phoneView = findViewById(R.id.phoneTextView);
        subjectIDView = findViewById(R.id.subjectNumber);

        if (typeOfUser.equals("Students")) {
            database_path = "Students/" + userUID;
            titleView.setText("Aluno");
        }else{
            database_path = database_path + userUID;
            titleView.setText("Professor");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference(database_path);

        if (subjectIndex != null){
            subjectIDView.setVisibility(View.VISIBLE);
            subjectIDView.setText(subjectIDView.getText().toString() + " " + subjectIndex);
        }

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.finish();
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
                nameView.setText(nameView.getText().toString() + CryptographyAdapter.decryptText(dataSnapshot.child("completeName").getValue().toString(), userUID) );
                emailView.setText(emailView.getText().toString() + CryptographyAdapter.decryptText(dataSnapshot.child("email").getValue().toString(), userUID) );
                if (dataSnapshot.child("phone").exists()) {
                    phoneView.setText(phoneView.getText().toString() + CryptographyAdapter.decryptText(dataSnapshot.child("phone").getValue().toString(), userUID) );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar o usuario", Toast.LENGTH_LONG).show();
            }
        });
    }
}
