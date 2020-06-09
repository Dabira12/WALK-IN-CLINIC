package com.example.finalproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ClinicPage extends AppCompatActivity {

    private TextView clinicName, hourDisplay;
    private EditText c, r;
    private String comment, username;
    private int rating, currentDay, currentMonth, currentYear, timeForAppointments;
    private CalendarView hourCal;
    private Hours forToday;

    String nameOfClinic;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_page);
        clinicName = findViewById(R.id.clinicNameLabel);
        Bundle b = getIntent().getExtras();
        Log.d("clinic", (String) b.get("name"));

        nameOfClinic = (String) b.get("name"); // use this to search in firebase

        clinicName.setText(nameOfClinic);
        c = (EditText) findViewById(R.id.comment);
        r = (EditText) findViewById(R.id.rating);

        hourCal = findViewById(R.id.appointmentCal);
        hourDisplay = findViewById(R.id.availableHours);

        hourCal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currentDay = dayOfMonth;
                currentMonth = month + 1;
                currentYear = year;
                getHours(dayOfMonth, month + 1, year);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase.getReference("AllUsers").child(mUser.getUid()).child("General Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap person = (HashMap) dataSnapshot.getValue();
                username = String.format("%s, %s", person.get("lastName"), person.get("firstName"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getHours(int day, int month, int year) {
        DatabaseReference ref = mDatabase.getReference("Clinics").child(nameOfClinic).child("Hours").child(String.format("%s-%s-%s", day, month, year));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap hours = (HashMap) dataSnapshot.getValue();
                    hourDisplay.setText(String.format("Open from %s to %s", hours.get("timeStart"), hours.get("timeEnd")));
                } else {
                    hourDisplay.setText("Closed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onRequestAppointment(View view) {

        mDatabase.getReference("Clinics").child(nameOfClinic).child("Hours").child(String.format("%s-%s-%s", currentDay, currentMonth, currentYear)).child("Appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("t", dataSnapshot + "");
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        HashMap info = (HashMap) areaSnapshot.getValue();

                        if (username.equals((String) info.get("name"))) {
                            Log.d("t", (String) info.get("name"));
                            Toast.makeText(ClinicPage.this, "You already have an appointment", Toast.LENGTH_SHORT).show();
                        } else {
                            createAppointment();
                        }
                    }
                } else {
                    System.out.println("test");
                    createAppointment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createAppointment(){
        mDatabase.getReference("Clinics").child(nameOfClinic).child("Hours").child(String.format("%s-%s-%s", currentDay, currentMonth, currentYear)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                HashMap hours = (HashMap) dataSnapshot.getValue();

                if (hours == null) {
                    Toast noInputToast = Toast.makeText(getApplicationContext(), "Clinic is closed on this day", Toast.LENGTH_LONG);
                    noInputToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
                    noInputToast.show();
                    return;
                }
                hourDisplay.setText(String.format("Open from %s to %s", hours.get("timeStart"), hours.get("timeEnd")));

                int startHour = ((Long) hours.get("startHour")).intValue();
                int endHour = ((Long) hours.get("endHour")).intValue();
                int startMinute = ((Long) hours.get("startMinute")).intValue();
                int endMinute = ((Long) hours.get("endMinute")).intValue();

                timeForAppointments = ((endHour - startHour) * 60) + (endMinute - startMinute);

                for (DataSnapshot areaSnapshot : dataSnapshot.child("Appointments").getChildren()) {
                    timeForAppointments -= 15;
                    counter++;
                }
                Log.d("time", "Time remaining: " + timeForAppointments);

                if (timeForAppointments >= 15) {

                    int consumedTime = counter * 15;
                    int timeInMinutes = (startHour * 60) + startMinute;

                    int appointmentStartTimeInMinutes = timeInMinutes + consumedTime;
                    int appointmentEndTimeInMinutes = appointmentStartTimeInMinutes + 15;

                    final int appointmentStartMinute = appointmentStartTimeInMinutes % 60;
                    final int appointmentStartHour = appointmentStartTimeInMinutes / 60;

                    final int appointmentEndMinute = appointmentEndTimeInMinutes % 60;
                    final int appointmentEndHour = appointmentEndTimeInMinutes / 60;

                    addToDatabase(appointmentStartHour, appointmentStartMinute, appointmentEndHour, appointmentEndMinute);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToDatabase(int startHour, int startMinute, int endHour, int endMinute){

        Appointment myAppointment = new Appointment(String.format("%s-%s-%s", currentDay, currentMonth, currentYear), startHour, startMinute, endHour, endMinute, nameOfClinic, username);

        mDatabase.getReference("Clinics").child(nameOfClinic).child("Hours").child(String.format("%s-%s-%s", currentDay, currentMonth, currentYear)).child("Appointments").child(username).setValue(myAppointment);
        mDatabase.getReference("AllUsers").child(mUser.getUid()).child("General Info").child("Appointments").child(String.format("%s-%s-%s", currentDay, currentMonth, currentYear)).child(nameOfClinic).setValue(myAppointment);


    }


    public void onExitClinicPage(View view) {
        finish();
    }

    public void onSubmitReview(View view) {
        comment = c.getText().toString().trim();
        rating = Integer.parseInt(r.getText().toString());

        if (rating < 0 || rating > 5) {
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Please enter a rating between 0-5", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }
        Review review = new Review(comment, rating);

        mDatabase.getReference("Clinics").child(nameOfClinic).child("Reviews").child(mAuth.getUid()).setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast t;
                if (task.isSuccessful()) {
                    t = Toast.makeText(ClinicPage.this, "Your review has been created. Thank you for your feedback!", Toast.LENGTH_LONG);
                    Log.d("good", "review:created");
                } else {
                    t = Toast.makeText(ClinicPage.this, "There was an error creating your review. Please try again...", Toast.LENGTH_LONG);
                    Log.d("bad", "review:NotCreated");
                }
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
                t.show();
            }
        });


    }
}
