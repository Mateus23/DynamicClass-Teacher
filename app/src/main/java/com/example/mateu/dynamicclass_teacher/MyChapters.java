package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

public class MyChapters extends AppCompatActivity {

    LinearLayout mScrollView;
    DatabaseReference databaseReference;
    String Database_Path = "Classes/";
    static String subjectCode, subjectName;
    TextView subjectTV;
    Button mCopySubject;
    ValueEventListener mListener;
    int numberOfTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chapters);

        mScrollView = findViewById(R.id.scrollViewLayout);

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        subjectName = b.getString("subjectName");
        Database_Path = Database_Path + subjectCode + "/Chapters";
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        subjectTV = findViewById(R.id.subjectTextView);
        subjectTV.setText(subjectName);

        Button mNewChapterButton = (Button) findViewById(R.id.newChapterButton);
        mNewChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNewChapterActivity(subjectCode);
            }
        });

        mCopySubject = findViewById(R.id.buttonCopy);
        mCopySubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCopySubjectPopup();
            }
        });

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyChapters.this.finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mScrollView.getChildCount() == 0) {
            numberOfTimes = numberOfTimes + 1;
            mListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 1;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Button b = new Button(MyChapters.this);
                        b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                        final String index = postSnapshot.child("index").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();

                        String buttonText = "Capitulo " + index + " - " + name;
                        b.setText(buttonText);
                        b.setId(count);
                        count = count + 1;

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toChapterActivity(subjectCode, index);
                            }
                        });

                        mScrollView.addView(b);
                    }
                    if (mScrollView.getChildCount() == 0) {
                        mCopySubject.setVisibility(View.VISIBLE);
                    }else{
                        mCopySubject.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void updateChapters(){
        if (mScrollView.getChildCount() == 0) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 1;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Button b = new Button(MyChapters.this);
                        b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                        final String index = postSnapshot.child("index").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();

                        String buttonText = "Capitulo " + index + " - " + name;
                        b.setText(buttonText);
                        b.setId(count);
                        count = count + 1;

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toChapterActivity(subjectCode, index);
                            }
                        });

                        mScrollView.addView(b);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (mScrollView.getChildCount() == 0) {
                mCopySubject.setVisibility(View.VISIBLE);
            }else{
                mCopySubject.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void toChapterActivity(String subjectCode, String chapterIndex){
        Intent intent = new Intent(this, ChapterActivity.class);
        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("chapterIndex", chapterIndex);
        startActivity(intent);
        databaseReference.removeEventListener(mListener);
    }

    public void toNewChapterActivity(String subjectCode){
        Intent intent = new Intent(this, NewChapterActivity.class);
        intent.putExtra("subjectCode", subjectCode);
        startActivity(intent);
        databaseReference.removeEventListener(mListener);
    }

    public void openCopySubjectPopup() {
        DialogFragment newFragment = new PopupCopySubject();
        newFragment.show(getSupportFragmentManager(), "CopySubject");
    }

    public static void copySubject(String copiedSubjectCode, DataSnapshot chaptersToCopySnapshot){
        DatabaseReference mSubjectReference = FirebaseDatabase.getInstance().getReference("Classes/" + subjectCode);
        for (DataSnapshot chapterSnapshot : chaptersToCopySnapshot.getChildren()){
            String key = chapterSnapshot.getKey();
            ChapterInfo copiedChapter = chapterSnapshot.getValue(ChapterInfo.class);
            mSubjectReference.child("Chapters").child(key).setValue(copiedChapter);
            mSubjectReference.child("Chapters").child(key).child("Students").setValue(null);
        }
    }
}
