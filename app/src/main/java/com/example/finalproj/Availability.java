package com.example.finalproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class Availability extends AppCompatActivity {

    CalendarView calendarView;
    TextView currentDate, myHours;
    TimePickerDialog startTime, endTime;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    int currentDay, currentMonth, currentYear, hour, minute, startHour, startMin, endHour, endMin;
    String selectedDate;
    Hours currentHours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        calendarView = findViewById(R.id.calendarView);
        currentDate = findViewById(R.id.currentDay);
        myHours = findViewById(R.id.myHours);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        hour = -1;
        minute = -1;

        startHour = 0;
        startMin = 0;

        endHour = 0;
        endMin = 0;

        LocalDate today = LocalDate.now();
        currentDay = today.getDayOfMonth();
        currentMonth = today.getMonthValue();
        currentYear = today.getYear();
        currentDate.setText(String.format("My Hours for: %s/%s/%s", today.getDayOfMonth(), today.getMonthValue(), today.getYear()));
        selectedDate = String.format("%s-%s-%s", today.getDayOfMonth(), today.getMonthValue(), today.getYear());
        findHours();


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                selectedDate = String.format("%s-%s-%s", dayOfMonth, month+1, year);
                currentDate.setText(String.format("My Hours for: %s/%s/%s", dayOfMonth, month+1, year));
                currentDay = dayOfMonth;
                currentMonth = month + 1;
                currentYear = year;
                findHours();

            }
        });

        startTime = new TimePickerDialog(Availability.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                startHour = hourOfDay;
                startMin = min;
                endTime.show();
            }
        },0, 0, false);

        startTime.setButton(DialogInterface.BUTTON_POSITIVE, "Start Time", startTime);

        endTime = new TimePickerDialog(Availability.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                endHour = hourOfDay;
                endMin = min;

                if (startHour > endHour){
                    Toast.makeText(Availability.this, "Invalid Time! Please try again.", Toast.LENGTH_SHORT).show();
                    startTime.show();
                } else if (startHour == endHour){
                    if (startMin > endMin){
                        Toast.makeText(Availability.this, "Invalid Time! Please try again.", Toast.LENGTH_SHORT).show();
                        startTime.show();
                    }
                } else {
                    setTime();
                }
            }
        },0, 0, false);

        endTime.setButton(DialogInterface.BUTTON_POSITIVE, "End Time", endTime);



    }

    public void backToAccount(View view){
        finish();
    }

    public void addNewHours(View view){
        LocalDate today = LocalDate.now();

        int nowYear = today.getYear();
        int nowMonth = today.getMonthValue();
        int nowDay = today.getDayOfMonth();

        Log.d("date", String.format("%s/%s/%s", today.getYear(), today.getMonthValue(), today.getDayOfMonth()));

        Date selected = new Date(currentYear, currentMonth, currentDay);

        if (nowYear > currentYear){

            Toast.makeText(this, "Invalid Date! Please try again.", Toast.LENGTH_SHORT).show();

        } else if (nowYear == currentYear) {

            if (nowMonth > currentMonth) {

                Toast.makeText(this, "Invalid Date! Please try again.", Toast.LENGTH_SHORT).show();

            } else if (nowMonth == currentMonth) {

                if (nowDay > currentDay) {

                    Toast.makeText(this, "Invalid Date! Please try again.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Please Enter Start and End Time", Toast.LENGTH_SHORT).show();
                    startTime.show();
                }
            }
        }
    }

    public void setTime(){

        currentHours = new Hours(selectedDate, startHour, startMin, endHour, endMin);

        Query serviceQuery = mDatabase.getReference("AllUsers").child(mAuth.getCurrentUser().getUid()).orderByChild("name");
        serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){

                    HashMap person = (HashMap) dataSnapshot.child("General Info").getValue();

                    Log.d("clinic", "" + dataSnapshot.getValue());

                    mDatabase.getReference("Clinics").child((String)person.get("clinic")).child("Hours").child(currentHours.getDate()).setValue(currentHours);
                    mDatabase.getReference("AllUsers").child(mAuth.getCurrentUser().getUid()).child("Hours").child(currentHours.getDate()).setValue(currentHours)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task){
                                    if (task.isSuccessful()){
                                        Log.d("good", "hoursAddSuccess");
                                        Toast.makeText(Availability.this, "Success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("error", "FirebaseError");
                                        Toast.makeText(Availability.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    myHours.setText("There are no Hours");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Availability.this, "Bruh", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void findHours(){

        DatabaseReference ref = mDatabase.getReference("AllUsers").child(mAuth.getCurrentUser().getUid()).child("Hours").child(String.format("%s-%s-%s", currentDay, currentMonth, currentYear));
        Log.d("search", String.format("%s-%s-%s", currentDay, currentMonth, currentYear));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("snap", "" + dataSnapshot.getValue());

                if (dataSnapshot.getValue() != null) {
                    HashMap hours = (HashMap) dataSnapshot.getValue();
                    myHours.setText(String.format("%s to %s", hours.get("timeStart"), hours.get("timeEnd")));
                } else {
                    myHours.setText("No hours today!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
