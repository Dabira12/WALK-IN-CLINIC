package com.example.finalproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;

    private Person user;
    private Employee employee;
    private Patient patient;

    private EditText s;
    private Spinner r, sp;
    private String serviceName;
    private EditText sI;
    private RadioButton aSearch, hSearch, sSearch;
    private ArrayList<String> roleList = new ArrayList<>();
    private ArrayList<String> serviceList = new ArrayList<>();
    private ArrayList<String> searched = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter, serviceArrayAdapter;
    private ListView listView;
    private String[] arraySearch;

    private EditText a, p, c;
    private Switch credit, debit, t1Insurance, t2Insurance;
    private boolean acceptsCredit = false;
    private boolean acceptsDebit = false;
    private boolean acceptsType1Insurance = false;
    private boolean acceptsType2Insurance = false;
    private EditText es;
    private String employeeService;
    private String clinicSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Sets screen to corresponding screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTexts();

        //Attempts to launch firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        //Initialization of role spinner
        Spinner spinner = findViewById(R.id.role);
        roleList.add("-Select Role Performing Service-");
        roleList.add("Doctor");
        roleList.add("Nurse");
        roleList.add("Staff");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roleList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        Spinner serviceSpinner = findViewById(R.id.employeeService);
        serviceList.add("-Please Select a Service-");
        mDatabase.getReference("Possible Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot areaSnapshot : dataSnapshot.getChildren()){
                    Log.d("tag", areaSnapshot.getValue() + "");
                    String serviceName = areaSnapshot.child("name").getValue(String.class);
                    serviceList.add(serviceName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this, "Please select a service.", Toast.LENGTH_SHORT).show();
            }
        });

        serviceArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serviceList);
        serviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(serviceArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editorLaunchInterest = new Intent(getApplicationContext(), ClinicPage.class);
                editorLaunchInterest.putExtra("position",position);
                editorLaunchInterest.putExtra("name",arraySearch[position]);
                startActivityForResult(editorLaunchInterest, 0);
            }
        });

        //Sets display text
        final TextView loggedInAs = (TextView) findViewById(R.id.loggedInAs);


        //Check if account is type admin
        if (MainActivity.adminLoggedIn()) {
            Person user = MainActivity.getAdmin();
            findViewById(R.id.adminLayout).setVisibility(View.VISIBLE);
            loggedInAs.setText(user.getAccountType());
        } else {
            //Employee or patient
            mDatabase.getReference( "AllUsers").child(mUser.getUid()).child("General Info").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Person temp = dataSnapshot.getValue(Employee.class);
                    System.out.println(temp == null);
                    loggedInAs.setText(temp.getAccountType());
                    if (temp.getAccountType() != null && temp.getAccountType().equals("Employee")) {
                        // User is an employee
                        employee = dataSnapshot.getValue(Employee.class);
                        EditText addField = a;
                        addField.setText(employee.getClinicAddress());
                        EditText phoneField = p;
                        phoneField.setText(employee.getPhoneNumber());
                        EditText clinicField = c;
                        clinicField.setText(employee.getClinic());

                        Switch creditAccepted = credit;
                        creditAccepted.setChecked(employee.getAcceptsCredit());
                        Switch debitAccepted = debit;
                        debitAccepted.setChecked(employee.getAcceptsDebit());
                        Switch type1Insurance = t1Insurance;
                        type1Insurance.setChecked(employee.getAcceptsType1Insurance());
                        Switch type2Insurance = t2Insurance;
                        type2Insurance.setChecked(employee.getAcceptsType2Insurance());


                        findViewById(R.id.employeeLayout).setVisibility(View.VISIBLE);
                    } else if (temp.getAccountType() != null){
                        // User is a patient
                        patient = dataSnapshot.getValue(Patient.class);

                        findViewById(R.id.patientLayout).setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void onSaveInformation(View view) {
        EditText addField = a;
        EditText phoneField = p;
        EditText clinicField = c;
        Switch creditAccepted = credit;
        Switch debitAccepted = debit;
        Switch type1Insurance = t1Insurance;
        Switch type2Insurance = t2Insurance;

        if (creditAccepted.isChecked()) {
            acceptsCredit = true;
        } else {
            acceptsCredit = false;
        }
        if (debitAccepted.isChecked()) {
            acceptsDebit = true;
        } else {
            acceptsDebit = false;
        }
        if (type1Insurance.isChecked()) {
            acceptsType1Insurance = true;
        } else {
            acceptsType1Insurance = false;
        }
        if (type2Insurance.isChecked()) {
            acceptsType2Insurance = true;
        } else {
            acceptsType2Insurance = false;
        }

        employee.setClinicAddress(a.getText().toString().trim());
        employee.setPhoneNumber(p.getText().toString().trim());
        employee.setClinic(c.getText().toString().trim());

        // If field is blank
        if(employee.getClinicAddress().equals("") || employee.getPhoneNumber().equals("") || employee.getClinic().equals("")) {
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave fields blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }
        if (!employee.validPhoneNumber()) {
            Toast invalidPhoneNumberToast = Toast.makeText(getApplicationContext(), "Invalid Phone Number - Must contain under 16 NUMBERS (no spaces)", Toast.LENGTH_LONG);
            invalidPhoneNumberToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            invalidPhoneNumberToast.show();
            return;
        }
        if (!employee.validAddress(getApplicationContext(), employee.getClinicAddress())) {
            Toast invalidAddressToast = Toast.makeText(getApplicationContext(), "Invalid Address", Toast.LENGTH_LONG);
            invalidAddressToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            invalidAddressToast.show();
            return;
        }
        Log.d("A", ""+ acceptsDebit);
        employee.setAcceptsCredit(acceptsCredit);
        employee.setAcceptsDebit(acceptsDebit);
        employee.setAcceptsType1Insurance(acceptsType1Insurance);
        employee.setAcceptsType2Insurance(acceptsType2Insurance);

        mDatabase.getReference("AllUsers").child(mUser.getUid()).child("General Info").setValue(employee)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    //Checks if the task could be completed
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast t5;
                        if (task.isSuccessful()) {
                            Log.d("good", "employeeInfo:Updated");
                            t5 = Toast.makeText(Home.this, "Employee Information Updated", Toast.LENGTH_LONG);

                            // Add clinic to list of clinics

                            Query serviceQuery = mDatabase.getReference("Clinics").child(employee.getClinic()).orderByChild("clinicName");
                            serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Toast t6;
                                    //Checks if service exists
                                    if (dataSnapshot.getValue() == null) {
                                        // Clinic doesn't exist
                                        WalkInClinic clinic = new WalkInClinic(employee.getClinic(), employee.getClinicAddress());
                                        mDatabase.getReference("Clinics").child(employee.getClinic()).setValue(clinic)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    //Checks if the task could be completed
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("good", "clinic:Updated");
                                                        } else {
                                                            Log.d("bad", "clinic:NotUpdated");
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        } else {
                            Log.d("bad", "employeeInfo:NotUpdated");
                            t5 = Toast.makeText(Home.this, "Error Updating Employee Information", Toast.LENGTH_LONG);
                        }
                        t5.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                        t5.show();
                    }
                });
    }

    public void onAddEmployeeService(View view) {
        employeeService = sp.getSelectedItem().toString().trim();

        //Checks for blank service
        if (employeeService.equals("-Please Select a Service-")){
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave service field blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        if (employee.getClinic().equals("")) {
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Please enter your clinic name before adding services", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        final Service service = new Service(employeeService, employee.getFirstName() + " " + employee.getLastName());

        Query serviceQuery = mDatabase.getReference("Possible Services").child(service.getName()).orderByChild("name");
        serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast t6;
                //Checks if service exists
                if (dataSnapshot.getValue() != null) {
                    // Service Exists
                    mDatabase.getReference("AllUsers").child(mUser.getUid()).child("Services Offered").child(service.getName()).setValue(service)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    employee.addService(service);
                                    Toast t5;
                                    if (task.isSuccessful()) {
                                        Log.d("good", "onAddEmployeeService:success");
                                        t5 = Toast.makeText(Home.this, "Service '" + employeeService + "' Added To your Account", Toast.LENGTH_LONG);
                                        sp.setSelection(0);

                                        WalkInClinic clinic = new WalkInClinic(employee.getClinic(), "");

                                        // Add service to clinic
                                        mDatabase.getReference("Clinics").child(employee.getClinic()).child("Services").child(service.getName()).setValue(employee.getService(service))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    //Checks if the task could be completed
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("good", "clinic:Updated");
                                                        } else {
                                                            Log.d("bad", "clinic:NotUpdated");
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.d("error", "onAddEmployeeService:Firebase database error");
                                        t5 = Toast.makeText(Home.this, "Could not add service: Database Error", Toast.LENGTH_LONG);
                                    }
                                    t5.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                                    t5.show();
                                }
                            });
                } else {
                    //If value was null
                    Log.d("error", "onAddEmployeeService:failure");
                    t6 = Toast.makeText(Home.this, "Service '" + employeeService + "' does not exist", Toast.LENGTH_LONG);
                    t6.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                    t6.show();
                }
            }

            //In case of Error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast t;
                Log.d("error", "onDeleteService:failure - " + databaseError.toException());
                t = Toast.makeText(Home.this, "Could not delete service: service not found", Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t.show();

            }
        });
    }

    public void onRemoveEmployeeService(View view) {
        employeeService = sp.getSelectedItem().toString().trim();

        //Checks for empty field
        if (employeeService.equals("-Please Select a Service-")){
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave service field blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        //Creates service to be found and deleted
        final Service service = new Service(employeeService, "");

        //Attempts to Remove service from firebase
        Query serviceQuery = mDatabase.getReference("AllUsers").child(mUser.getUid()).child("Services Offered").child(service.getName()).orderByChild("name");
        Log.d("query","" + serviceQuery);
        serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast t6;
                Log.d("snap","" + dataSnapshot.getValue());

                //Checks if service exists
                if (dataSnapshot.getValue() != null) {
                    //If value exists
                    employee.removeService(service);
                    dataSnapshot.getRef().removeValue();
                    Log.d("good", "onDeleteEmployeeService:success");
                    // Remove service from clinics
                    Query serviceQuery = mDatabase.getReference("Clinics").child(employee.getClinic()).child("Services").child(service.getName()).orderByChild("name");
                    serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    dataSnapshot.getRef().removeValue();
                                } else {
                                    Log.d("Database", "could not delete service from clinic: database out of sync");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Database", "database error");
                            }
                        });

                    t6 = Toast.makeText(Home.this, "Service '" + employeeService + "' Deleted From Employee Account", Toast.LENGTH_LONG);
                    sp.setSelection(0);
                } else {
                    //If value was null
                    Log.d("error", "onDeleteEmployeeService:failure");
                    t6 = Toast.makeText(Home.this, "Service '" + employeeService + "' could not be found/deleted", Toast.LENGTH_LONG);
                }
                t6.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t6.show();
            }

            //In case of Error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast t;
                Log.d("error", "onDeleteService:failure - " + databaseError.toException());
                t = Toast.makeText(Home.this, "Could not delete service: service not found", Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t.show();
            }
        });
    }

    //Initializes texts
    public void setTexts() {
        s = (EditText) findViewById(R.id.serviceName);
        r = (Spinner) findViewById(R.id.role);
        sp = (Spinner) findViewById(R.id.employeeService);
        a = (EditText) findViewById(R.id.clinicAddressField);
        p = (EditText) findViewById(R.id.phoneField);
        c = (EditText) findViewById(R.id.clinicField);
        listView = (ListView) findViewById(R.id.list);
        credit = (Switch) findViewById(R.id.creditAccepted);
        debit = (Switch) findViewById(R.id.debitAccepted);
        t1Insurance = (Switch) findViewById(R.id.insurance1);
        t2Insurance = (Switch) findViewById(R.id.insurance2);
        //es = (EditText) findViewById(R.id.employeeService);
        sI = (EditText) findViewById(R.id.clinicSearch);
        aSearch = (RadioButton) findViewById(R.id.addressSearch);
        hSearch = (RadioButton) findViewById(R.id.hoursSearch);
        sSearch = (RadioButton) findViewById(R.id.serviceSearch);
    }

    //Upon Adding of Service
    public void onAddService(View view) {

        //Grabs name of service, required role
        serviceName = s.getText().toString().trim();
        final String role = r.getSelectedItem().toString().trim();

        //Checks for blank service or unselected role
        if (serviceName.equals("") || role.equals("-Select Role Performing Service-")){
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave fields blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        final Service service = new Service(serviceName, role);

        //Attempts to add service to firebase
        Query serviceQuery = mDatabase.getReference("Possible Services").child(service.getName()).orderByChild("name");
        Log.d("query","" + serviceQuery);
        serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Adds service to list of Possible services
                mDatabase.getReference("Possible Services").child(service.getName()).setValue(service)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            //Checks if the task could be completed
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast t5;
                                if (!task.isSuccessful()) {
                                    Log.d("error", "onAddService:Firebase database error");
                                    t5 = Toast.makeText(Home.this, "Could not create service: Database Error", Toast.LENGTH_LONG);
                                    t5.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                                    t5.show();
                                    return;
                                }
                            }
                        });

                Toast t6;
                Log.d("snap","" + dataSnapshot.getValue());

                //Checks if service already exists, adds on null value
                if (dataSnapshot.getValue() != null) {
                    // Update service
                    Log.d("good", "onAddService:success");
                    t6 = Toast.makeText(Home.this, "Service '" + serviceName + "' Updated", Toast.LENGTH_LONG);
                    s.setText("");
                    r.setAdapter(arrayAdapter);
                } else {
                    // Add new service
                    Log.d("good", "onAddService:success");
                    t6 = Toast.makeText(Home.this, "Service '" + serviceName + "' Added", Toast.LENGTH_LONG);
                    s.setText("");
                    r.setAdapter(arrayAdapter);
                }

                //Displays outcome
                t6.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t6.show();
            }

            //In case of error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast t;
                Log.d("error", "onDeleteService:failure - " + databaseError.toException());
                t = Toast.makeText(Home.this, "Could not delete service: service not found", Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t.show();
            }
        });


        /*
        mDatabase.getReference("Possible Services").child(service.getName()).setValue(service)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast t5;
                        if (task.isSuccessful()) {
                            Log.d("good", "onAddService:success");
                            t5 = Toast.makeText(Home.this, "Service '" + serviceName + "' Added", Toast.LENGTH_LONG);
                            s.setText("");
                            r.setAdapter(arrayAdapter);
                        } else {
                            Log.d("error", "onAddService:Firebase database error");
                            t5 = Toast.makeText(Home.this, "Could not create service: Database Error", Toast.LENGTH_LONG);
                        }
                        t5.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                        t5.show();
                    }
                });*/

    }

    public void onDeleteService(View view) {

        //Grabs name of service to delete
        serviceName = s.getText().toString().trim();
        final String role = r.getSelectedItem().toString().trim();

        //Empty field contingency
        if (serviceName.equals("")){
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave fields blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        //Creates service to be found and deleted
        final Service service = new Service(serviceName, role);

        //Attempts to Remove service from firebase
        Query serviceQuery = mDatabase.getReference("Possible Services").child(service.getName()).orderByChild("name");
        Log.d("query","" + serviceQuery);
        serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast t6;
                Log.d("snap","" + dataSnapshot.getValue());

                //Checks if service exists
                if (dataSnapshot.getValue() != null) {
                    //If value exists
                    dataSnapshot.getRef().removeValue();
                    Log.d("good", "onDeleteService:success");
                    t6 = Toast.makeText(Home.this, "Service '" + serviceName + "' Deleted", Toast.LENGTH_LONG);
                    s.setText("");
                    r.setAdapter(arrayAdapter);
                } else {
                    //If value was null
                    Log.d("error", "onDeleteService:failure");
                    t6 = Toast.makeText(Home.this, "Service '" + serviceName + "' could not be found/deleted", Toast.LENGTH_LONG);
                }
                t6.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t6.show();
            }

            //In case of Error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast t;
                Log.d("error", "onDeleteService:failure - " + databaseError.toException());
                t = Toast.makeText(Home.this, "Could not delete service: service not found", Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                t.show();
            }
        });
    }

    public void onSearchForClinic(View view) {
        //Grabs name of service to delete
        clinicSearch = sI.getText().toString().trim();

        //Empty field contingency
        if (clinicSearch.equals("")){
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave fields blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }
        if (aSearch.isChecked() && !Employee.validAddress(this, clinicSearch)){
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Invalid Address", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }
        if (!aSearch.isChecked() && !hSearch.isChecked() && !sSearch.isChecked()) {
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Please indicate what you would like to search by", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        mDatabase.getReference("Clinics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot areaSnapshot : dataSnapshot.getChildren()){

                    final HashMap clinic = (HashMap) areaSnapshot.getValue();


                    if(aSearch.isChecked()){
                        if (clinicSearch.equals(clinic.get("address"))){
                            searched.add( (String) clinic.get("clinicName"));
                        }
                    } else if (sSearch.isChecked()){
                        mDatabase.getReference("Clinics").child((String)clinic.get("clinicName")).child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot current : dataSnapshot.getChildren()){
                                    HashMap service = (HashMap) current.getValue();

                                    if(clinicSearch.equals(service.get("name"))){
                                        searched.add((String) clinic.get("clinicName"));
                                    }
                                }
                                initList();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else if (hSearch.isChecked()){
                        mDatabase.getReference("Clinics").child((String)clinic.get("clinicName")).child("Hours").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot current : dataSnapshot.getChildren()){
                                    System.out.println("test");
                                    HashMap service = (HashMap) current.getValue();
                                    Log.d("hours", service + "");

                                    if(clinicSearch.equals(service.get("date"))){
                                        searched.add((String) clinic.get("clinicName"));
                                    }
                                }
                                initList();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                Log.d("object array", searched.size() + "");
                if (aSearch.isChecked()) {
                    initList();
                }
                searched.clear();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initList(){
        arraySearch = Arrays.copyOf(searched.toArray(), searched.size(), String[].class);
        ClinicCustomAdapter adapter = new ClinicCustomAdapter(this, arraySearch);
        listView.setAdapter(adapter);
        if (searched.isEmpty()){
            Toast.makeText(Home.this, "No Results Found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddressSearch(View view){
        sI.setHint("Please enter the address");
    }

    public void onServiceSearch(View view){
        sI.setHint("Please enter a service");
    }

    public void onHoursSearch(View view){
        sI.setHint("DD-MM-YYYY");
    }

    public void onAvailability(View view){
        Intent intent = new Intent(this, Availability.class);
        startActivityForResult(intent, 0);
    }
}
