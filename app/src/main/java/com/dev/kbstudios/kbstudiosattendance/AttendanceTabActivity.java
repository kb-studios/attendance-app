package com.dev.kbstudios.kbstudiosattendance;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendanceTabActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private ArrayList<Lecture> lectures = new ArrayList<>();

    private int hourOfDay;
    private int minute;
    private int year;
    private int month;
    private int day;

    private DatabaseReference mDatabaseStudents;
    private DatabaseReference mDatabaseAttendees;
    private DatabaseReference mDatabaseAttendeesStudents;

    String lectureKey;
    String classKey;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_new:
                    DialogFragment newFragment2 = new DatePickerFragment();
                    newFragment2.show(getFragmentManager(), "datePicker");
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_tab);


        SharedPreferences sessionUser = getApplicationContext()
                .getSharedPreferences("kbstudiosattendance.userdata", Context.MODE_PRIVATE);
        String firebaseEmail = sessionUser.getString("user", null);

        mDatabaseAttendees = FirebaseDatabase.getInstance().getReference().child(firebaseEmail).child("attendees");

        classKey = getIntent().getStringExtra("classKey");
        mDatabaseStudents = FirebaseDatabase.getInstance().getReference().child(firebaseEmail).child("class").child(classKey).child("students");

        final ArrayAdapter<Lecture> adapter = new LectureAdapter(this, 0, lectures);
        ListView listView = (ListView) findViewById(R.id.listview_attendance);
        listView.setAdapter(adapter);

        mDatabaseAttendees.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Lecture lecture = dataSnapshot.getValue(Lecture.class);
                lecture.setLectureKey(dataSnapshot.getKey());
                adapter.add(lecture);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Lecture lecture = (Lecture) adapterView.getItemAtPosition(i);
                String key = lecture.getLectureKey();
                Intent intent = new Intent(AttendanceTabActivity.this, AttendanceActivity.class);
                intent.putExtra("lectureKey", key);
                startActivity(intent);
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void gotoTime(){
        DialogFragment newFragment1 = new TimePickerFragment();
        newFragment1.show(getFragmentManager(), "timePicker");
    }


    public void gotoAttendance(){
        mDatabaseStudents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lecture lecture = new Lecture(classKey,
                        getHourOfDay(),
                        getMinute(),
                        getYear(),
                        getMonth(),
                        getDay());
                lectureKey = mDatabaseAttendees.push().getKey();
                mDatabaseAttendees.child(lectureKey).setValue(lecture);
                mDatabaseAttendeesStudents = mDatabaseAttendees.child(lectureKey).child("students");
                int i = 1;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Student student = snapshot.getValue(Student.class);
                    Attendee attendee = new Attendee(i, student.getFullName(), 0);
                    mDatabaseAttendeesStudents.push().setValue(attendee);
                    i++;
                }

                Intent intent = new Intent(AttendanceTabActivity.this, AttendanceActivity.class);
                intent.putExtra("lectureKey", lectureKey);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

}
