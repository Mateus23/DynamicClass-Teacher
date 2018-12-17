package com.example.mateu.dynamicclass_teacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopupCopySubject extends DialogFragment {
    EditText subjectCodeTextView;
    DatabaseReference classesReference;
    DataSnapshot classesSnapshot;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        classesReference = FirebaseDatabase.getInstance().getReference("Classes");

        classesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classesSnapshot = dataSnapshot;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d("TEMOS AS MATERIAS", postSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        builder.setView(inflater.inflate(R.layout.popup_copy_subject, null))
                // Add action buttons
                .setPositiveButton("Copiar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        tryToCopySubject();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PopupCopySubject.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    tryToCopySubject();
                }
            });
        }

        subjectCodeTextView = d.findViewById(R.id.subjectCode);
    }

    public void tryToCopySubject(){
        subjectCodeTextView.setError(null);

        // Store values at the time of the login attempt.
        String code = subjectCodeTextView.getText().toString().toUpperCase();

        boolean cancel = false;

        if (TextUtils.isEmpty(code)) {
            subjectCodeTextView.setError(getString(R.string.error_field_required));
            cancel = true;
        }


        if (!classesSnapshot.child(code).exists()){
            subjectCodeTextView.setError("Codigo de turma nao encontrado");
            cancel = true;
        }

        if (!cancel) {
            DatabaseReference newClassReference = classesReference.child(code);
            MyChapters.copySubject(code, classesSnapshot.child(code).child("Chapters"));
            PopupCopySubject.this.getDialog().cancel();
        }
    }


}

