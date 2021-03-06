package com.dev.kbstudios.kbstudiosattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kbstudios on 14/9/17.
 */

public class AddClassFragment extends DialogFragment {

    private DatabaseReference mDatabase;
    private TextView classname;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogClass = inflater.inflate(R.layout.dialog_add_class, null);
        builder.setView(dialogClass)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences sessionUser = getActivity().getApplicationContext()
                                .getSharedPreferences("kbstudiosattendance.userdata", Context.MODE_PRIVATE);
                        String firebaseEmail = sessionUser.getString("user", null);

                        mDatabase = FirebaseDatabase.getInstance().getReference().child(firebaseEmail).child("class");

                        classname = (TextView) dialogClass.findViewById(R.id.classname);
                        Classroom classroom = new Classroom(classname.getText().toString());
                        String classKey = mDatabase.push().getKey();
                        mDatabase.child(classKey).setValue(classroom);
                        Intent i = new Intent(getActivity(), StudentFillActivity.class);
                        i.putExtra("classKey", classKey);
                        Log.d("AddStudent", "Not getting students");
                        getActivity().startActivity(i);
                        Log.d("AddStudent", "Not getting students 2");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
