package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MySubjects extends AppCompatActivity {

    LinearLayout mScrollView;
    DatabaseReference databaseReference;
    String Database_Path = "Professores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subjects);
        mScrollView = findViewById(R.id.scrollViewLayout);
        Bundle b = getIntent().getExtras();
        String id = b.getString("id");
        Database_Path = Database_Path + "/" + id + "/Classes";
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySubjects.this.finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mScrollView.getChildCount() == 0) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 1;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Button b = new Button(MySubjects.this);
                        b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                        final String code = postSnapshot.child("code").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();

                        String buttonText = code + " - " + name;
                        b.setText(buttonText);
                        b.setId(count);
                        count = count + 1;

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toSubjectActivity(code);
                            }
                        });

                        mScrollView.addView(b);
                    }
                    if (mScrollView.getChildCount() == 0){
                        findViewById(R.id.noSubjectsText).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.noSubjectsText).setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void toSubjectActivity(String subjectCode){
        Intent intent = new Intent(this, SubjectActivity.class);
        intent.putExtra("subjectCode", subjectCode);
        startActivity(intent);
    }

}
