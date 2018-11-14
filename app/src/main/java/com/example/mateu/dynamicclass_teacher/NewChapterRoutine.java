package com.example.mateu.dynamicclass_teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewChapterRoutine extends AppCompatActivity {

    DatabaseReference subjectDatabaseReference;
    DataSnapshot studentsSnapshot;
    String Database_Path = "Classes/";
    String subjectCode, chapterIndex;
    long[] mExerciseArray = new long[5];
    Integer[] exercisesToApplyArray = {0, 0, 0, 0, 0};
    List<String> memoryList = new ArrayList<>();
    String selectedStudent = "None";

    final int MAX_EX = 5;

    MaterialSpinner spinner1, spinner2, spinner3, spinner4, spinner5, studentsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chapter_routine);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewChapterRoutine.this.finish();
            }
        });

        Button mApplyButton = (Button) findViewById(R.id.applyButton);
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryToApplyExercises();
            }
        });

        Bundle b = getIntent().getExtras();
        mExerciseArray = b.getLongArray("numberOfExercises");
        subjectCode = b.getString("subjectCode");
        chapterIndex = b.getString("chapterIndex");

        subjectDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path + subjectCode);

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);
        spinner5 = findViewById(R.id.spinner5);
        studentsSpinner = findViewById(R.id.spinnerStudents);
        MaterialSpinner[] spinnerArray = {spinner1, spinner2, spinner3, spinner4, spinner5};


        for (int i = 0; i < spinnerArray.length; i++){
            List<String> stringArray =  new ArrayList<String>();

            for (int j = 0; (j <= mExerciseArray[i] && j <= MAX_EX) ; j++){
                stringArray.add(String.valueOf(j));
            }

            spinnerArray[i].setItems(stringArray);

            final int aux = i;
            spinnerArray[i].setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    exercisesToApplyArray[aux] = Integer.valueOf(item);
                }
            });

        }

        subjectDatabaseReference.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentsSnapshot = dataSnapshot;
                List<String> stringArray =  new ArrayList<String>();
                stringArray.add(" ");
                stringArray.add("Todos os Alunos");
                memoryList.add("None");
                memoryList.add("All");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Boolean isActive =(Boolean)postSnapshot.child("isActive").getValue();
                    if (isActive) {
                        memoryList.add(postSnapshot.getKey());
                        stringArray.add(postSnapshot.child("name").getValue().toString());
                    }
                }
                studentsSpinner.setItems(stringArray);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar a Turma", Toast.LENGTH_LONG).show();
            }
        });

        studentsSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedStudent = memoryList.get(position);
                //Toast.makeText(getApplicationContext(), "CLICOU NO" + memoryList.get(position), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void tryToApplyExercises(){
        int totalExercises = exercisesToApplyArray[0] + exercisesToApplyArray[1] + exercisesToApplyArray[2] + exercisesToApplyArray[3] + exercisesToApplyArray[4];
        if (selectedStudent.equals("none") || totalExercises == 0){
            Toast.makeText(getApplicationContext(), "Selecione alunos e exercicios", Toast.LENGTH_LONG).show();
        }else if(selectedStudent.equals("All")) {
            for (int i = 2; i < memoryList.size() - 1; i++) {
                applyExercises(memoryList.get(i), false);
            }
            applyExercises(memoryList.get(memoryList.size() - 1), true);
        }else{
            applyExercises(selectedStudent, true);
        }
        //DatabaseReference chapterReference = subjectDatabaseReference.child(chapterIndex);
    }

    public void applyExercises(String studentUID, final boolean shouldFinish){
        List<Integer> exercisesToApplyList = Arrays.asList(exercisesToApplyArray);
        DatabaseReference toDoReference = subjectDatabaseReference.child("Chapters").child(chapterIndex).child("Students").child(studentUID).child("ToDoExercises");
        toDoReference.setValue(exercisesToApplyList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AlertDialog alertDialog = new AlertDialog.Builder(NewChapterRoutine.this).create();
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (task.isSuccessful() && shouldFinish) {
                    alertDialog.setTitle("Sucesso");
                    alertDialog.setMessage("Exercicio aplicado com sucesso");
                    alertDialog.show();
                }else if(shouldFinish){
                    alertDialog.setTitle("Falha");
                    alertDialog.setMessage("Nao foi possivel aplicar o exercicio. Tente novamente mais tarde.");
                    alertDialog.show();
                }
            }
        });;
    }


}
