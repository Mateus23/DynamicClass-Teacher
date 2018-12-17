package com.example.mateu.dynamicclass_teacher;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NewExercise extends AppCompatActivity {

    // Folder path for Firebase Storage.
    String Storage_Path = "Classes/";


    // Root Database Name for Firebase Database.
    String Database_Path = "Classes/";

    String subjectCode;
    String chapterIndex;

    Button selectButton;
    Button resetButton;
    Button createExerciseButton;

    EditText nameText, exerciseText;
    EditText answer11, answer21, answer22, answer31, answer32, answer33, answer34, answer35;

    LinearLayout answerLayout1, answerLayout2, answerLayout3;

    RadioButton difficultyButton1, difficultyButton2, difficultyButton3, difficultyButton4, difficultyButton5;
    RadioButton answerButton1, answerButton2, answerButton3;

    ImageView selectImageDisplay;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // Image request code for onActivityResult() .
    final int Image_Request_Code = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        chapterIndex = b.getString("chapterIndex");

        Database_Path = Database_Path + subjectCode + "/Chapters/" + chapterIndex;
        Storage_Path = Storage_Path + subjectCode + "/Chapters/" + chapterIndex;

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        //Assign ID'S to button.
        resetButton = (Button)findViewById(R.id.resetButton);
        selectButton = (Button)findViewById(R.id.selectButton);
        createExerciseButton = (Button)findViewById(R.id.createExerciseButton);

        difficultyButton1 = (RadioButton)findViewById(R.id.difficultyButton1);
        difficultyButton2 = (RadioButton)findViewById(R.id.difficultyButton2);
        difficultyButton3 = (RadioButton)findViewById(R.id.difficultyButton3);
        difficultyButton4 = (RadioButton)findViewById(R.id.difficultyButton4);
        difficultyButton5 = (RadioButton)findViewById(R.id.difficultyButton5);

        answerButton1 = (RadioButton)findViewById(R.id.answerButton1) ;
        answerButton2 = (RadioButton)findViewById(R.id.answerButton2) ;
        answerButton3 = (RadioButton)findViewById(R.id.answerButton3) ;

        answerLayout1 = (LinearLayout)findViewById(R.id.answerLayout1);
        answerLayout2 = (LinearLayout)findViewById(R.id.answerLayout2);
        answerLayout3 = (LinearLayout)findViewById(R.id.answerLayout3);

        answer11 = (EditText)findViewById(R.id.exactAnswer);
        answer21 = (EditText)findViewById(R.id.minAnswer);
        answer22 = (EditText)findViewById(R.id.maxAnswer);
        answer31 = (EditText)findViewById(R.id.alternative1);
        answer32 = (EditText)findViewById(R.id.alternative2);
        answer33 = (EditText)findViewById(R.id.alternative3);
        answer34 = (EditText)findViewById(R.id.alternative4);
        answer35 = (EditText)findViewById(R.id.alternative5);

        selectImageDisplay = (ImageView)findViewById(R.id.imageView);

        // Assign ID's to EditText.
        nameText = (EditText)findViewById(R.id.nameInput);
        exerciseText = (EditText)findViewById(R.id.exerciseDescription);

        // Adding click listener to Choose image button.
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });

        // Adding click listener to Choose image button.
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageDisplay.setImageDrawable(null);
                resetButton.setVisibility(View.INVISIBLE);
                selectButton.setVisibility(View.VISIBLE);
            }
        });


        // Adding click listener to Upload image button.
        createExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadExercise();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                selectImageDisplay.setImageBitmap(bitmap);

                selectButton.setVisibility(View.INVISIBLE);
                resetButton.setVisibility((View.VISIBLE));

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
            final long generatedId = System.currentTimeMillis();
            final StorageReference exerciseImageRef = storageReference.child(Storage_Path + generatedId + "." + GetFileExtension(FilePathUri));
            UploadTask uploadTask = exerciseImageRef.putFile(FilePathUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    Toast.makeText(getApplicationContext(), "Image Uploading", Toast.LENGTH_LONG).show();
                    return exerciseImageRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                // Showing toast message after done uploading.
                                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                                ExerciseInfo exerciseUploadInfo = new ExerciseInfo(nameText.getText().toString(), String.valueOf(getDifficulty()), exerciseText.getText().toString(), downloadUri.toString(),  getAnswer());

                                // Getting image upload ID.
                                String ImageUploadId = databaseReference.push().getKey();

                                databaseReference.child("Exercises").child(String.valueOf(getDifficulty())).child("EX" + String.valueOf(generatedId)).setValue(exerciseUploadInfo);

                                NewExercise.this.finish();
                            } else {
                                // Handle failures
                                // ...
                                Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
        else {

            Toast.makeText(NewExercise.this, "Please Select Image and Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    public void UploadExerciseText(){
        final long generatedId = System.currentTimeMillis();
        ExerciseInfo exerciseUploadInfo = new ExerciseInfo(nameText.getText().toString(), String.valueOf(getDifficulty()), exerciseText.getText().toString(), getAnswer());

        // Getting image upload ID.
        //String ImageUploadId = databaseReference.push().getKey();

        databaseReference.child("Exercises").child(String.valueOf(getDifficulty())).child("EX" + String.valueOf(generatedId)).setValue(exerciseUploadInfo);

        NewExercise.this.finish();
    }

    public void UploadExercise(){
        if(checkIfFieldsAreCorrect()){
            Toast.makeText(getApplicationContext(), "Start uploading exercise", Toast.LENGTH_LONG).show();
            if (selectImageDisplay.getDrawable() == null) {
                UploadExerciseText();
            }else{
                UploadImageFileToFirebaseStorage();
            }
        }
    }

    private boolean checkIfFieldsAreCorrect() {
        // Reset errors.
        nameText.setError(null);
        exerciseText.setError(null);
        answer11.setError(null);
        answer21.setError(null);
        answer22.setError(null);
        answer31.setError(null);
        answer32.setError(null);
        answer33.setError(null);
        answer34.setError(null);
        answer35.setError(null);

        int typeOfAnswer = getTypeOfAnswer();

        // Store values at the time of the login attempt.
        String name = nameText.getText().toString();
        String exerciseDescription = exerciseText.getText().toString();
        String mAnswer11 = answer11.getText().toString();
        String mAnswer21 = answer21.getText().toString();
        String mAnswer22 = answer22.getText().toString();
        String mAnswer31 = answer31.getText().toString();
        String mAnswer32 = answer32.getText().toString();
        String mAnswer33 = answer33.getText().toString();
        String mAnswer34 = answer34.getText().toString();
        String mAnswer35 = answer35.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(name)) {
            nameText.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (typeOfAnswer == 1) {
            if (TextUtils.isEmpty(mAnswer11)) {
                answer11.setError(getString(R.string.error_field_required));
                cancel = true;
            }
        } else if (typeOfAnswer == 2) {
            if (TextUtils.isEmpty(mAnswer21)) {
                answer21.setError(getString(R.string.error_field_required));
                cancel = true;
            }else if (TextUtils.isEmpty(mAnswer22)) {
                answer22.setError(getString(R.string.error_field_required));
                cancel = true;
            }else {
                int minAnswer = Integer.parseInt(mAnswer21);
                int maxAnswer = Integer.parseInt(mAnswer22);
                if (minAnswer > maxAnswer) {
                    answer21.setError("Deve ser o menor valor");
                    answer22.setError("Deve ser o maior valor");
                    cancel = true;
                }
            }
        }else if (typeOfAnswer == 3) {
            if (TextUtils.isEmpty(mAnswer31)) {
                answer31.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(mAnswer32)) {
                answer32.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(mAnswer33)) {
                answer33.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(mAnswer34)) {
                answer34.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(mAnswer35)) {
                answer35.setError(getString(R.string.error_field_required));
                cancel = true;
            }
        }else{
            cancel = true;
        }

        if (TextUtils.isEmpty(exerciseDescription)) {
            if (selectImageDisplay.getDrawable() == null) {
                exerciseText.setError("Obrigatorio se nao possui imagem");
                cancel = true;
            }
        }

        if (getDifficulty() < 1){
            cancel = true;
            Toast.makeText(NewExercise.this, "Escolha uma dificuldade par ao exercicio", Toast.LENGTH_LONG).show();
        }

        return !cancel;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.answerButton1:
                if (checked)
                    answerLayout1.setVisibility(View.VISIBLE);
                    answerLayout2.setVisibility(View.INVISIBLE);
                    answerLayout3.setVisibility(View.INVISIBLE);
                break;
            case R.id.answerButton2:
                if (checked)
                    answerLayout1.setVisibility(View.INVISIBLE);
                    answerLayout2.setVisibility(View.VISIBLE);
                    answerLayout3.setVisibility(View.INVISIBLE);
                break;
            case R.id.answerButton3:
                if (checked)
                    answerLayout1.setVisibility(View.INVISIBLE);
                    answerLayout2.setVisibility(View.INVISIBLE);
                    answerLayout3.setVisibility(View.VISIBLE);
                break;
        }
    }

    public List<String> getAnswer(){
        int typeOfAnswer = getTypeOfAnswer();
        //String[] answerArray;
        List<String> answerList; //= Arrays.asList("sup1", "sup2", "sup3");
        if (typeOfAnswer == 1){
            //answerArray = new String[]{answer11.getText().toString()};
            answerList = Arrays.asList(answer11.getText().toString());
        }else if(typeOfAnswer == 2){
            //answerArray = new String[]{answer21.getText().toString(), answer22.getText().toString()};
            answerList = Arrays.asList(answer21.getText().toString(), answer22.getText().toString());
        }else{
            //answerArray = new String[]{answer31.getText().toString(), answer32.getText().toString(), answer33.getText().toString(), answer34.getText().toString(), answer35.getText().toString()};
            answerList = Arrays.asList(answer31.getText().toString(), answer32.getText().toString(), answer33.getText().toString(), answer34.getText().toString(), answer35.getText().toString());
        }
        return answerList;
    }

    public int getTypeOfAnswer(){
        if (answerButton1.isChecked()){
            return 1;
        }else if (answerButton2.isChecked()){
            return 2;
        }else if (answerButton3.isChecked()){
            return 3;
        }
        return 0;
    }

    public int getDifficulty(){
        if (difficultyButton1.isChecked()){
            return 1;
        }else if (difficultyButton2.isChecked()){
            return 2;
        }else if (difficultyButton3.isChecked()){
            return 3;
        }else if (difficultyButton4.isChecked()){
            return 4;
        }else if (difficultyButton5.isChecked()){
            return 5;
        }else{
            return 0;
        }
    }
}
