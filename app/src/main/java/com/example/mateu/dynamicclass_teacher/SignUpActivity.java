package com.example.mateu.dynamicclass_teacher;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mLastNameView;
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mEmailView2;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private FirebaseAuth mAuth;

    // Creating DatabaseReference object.
    DatabaseReference databaseReference;

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Professores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        // Set up the login form.
        mNameView = findViewById(R.id.name);
        mLastNameView = findViewById(R.id.lastName);

        mEmailView = findViewById(R.id.email);
        mEmailView2 = findViewById(R.id.email2);

        mPasswordView = findViewById(R.id.password);
        mPasswordView2 = findViewById(R.id.password2);

        Button mSignUpButton = findViewById(R.id.email_sign_in_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }




    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mEmailView2.setError(null);
        mPasswordView.setError(null);
        mPasswordView2.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String email2 = mEmailView2.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPasswordView2.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mNameView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if(TextUtils.isEmpty(password2)) {
            mPasswordView2.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }else if (!areEqual(password, password2)){
            mPasswordView.setError(getString(R.string.error_different_password));
            mPasswordView2.setError(getString(R.string.error_different_password));
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (TextUtils.isEmpty(email2)) {
            mEmailView2.setError(getString(R.string.error_invalid_email));
            cancel = true;
        } else if (!areEqual(email, email2)) {
            mEmailView.setError(getString(R.string.error_different_email));
            mEmailView2.setError(getString(R.string.error_different_email));
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }
        if (!cancel) {
            performSignUp(email, password, name, lastName);
        }
    }

    private void performSignUp(final String email, String password, final String name, final String lastName){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            UserInfo info = new UserInfo(userId, name, lastName, email);
                            databaseReference.child(user.getUid()).setValue(info);
                            updateUserName(user, name);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void updateUserName(FirebaseUser user, String name){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            toHomeActivity();
                        }
                    }
                });
    }

    public void toHomeActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean areEqual(String string1, String string2){
        return string1.equals(string2);
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }
}
