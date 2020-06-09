package com.example.finalproj;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.widget.AdapterView;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import java.util.ArrayList;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;

public class CreateAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    EditText fName, lName, emailaddress, pass;
    Spinner accType;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        initFields();
    }

    private void initFields(){
        fName = (EditText)findViewById(R.id.firstName);
        lName = (EditText)findViewById(R.id.lastName);
        accType = (Spinner)findViewById(R.id.accountType);
        emailaddress = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Sets screen reference to create account screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //Initialize Account Type Selector
        Spinner spinner = findViewById(R.id.accountType);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("-Select Account Type-");
        arrayList.add("Employee");
        arrayList.add("Patient");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    /*
    -Attempts to go to sign in page
    -Used when user already has existing account
     */
    public void onGoToSignIn(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult (intent,0);
    }

    /*
    -Attempts to create account using given information
     */
    public void signUpOnClick(View view) {
        String firstName = fName.getText().toString().trim();
        String lastName = lName.getText().toString().trim();
        final String accountType = accType.getSelectedItem().toString().trim();
        String email = emailaddress.getText().toString().trim();
        String password = pass.getText().toString().trim();

        //Blank field contingency
        if (firstName.equals("") || lastName.equals("") || email.equals("") || password.equals("") || accountType.equals("-Select Account Type-") ) {
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave fields blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        // Store password as hash
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            password = ConvertFunctions.bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            Log.d("bad", "Hash Password:failure");
            Toast t = Toast.makeText(CreateAccount.this, "Error Hashing Password", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        //Creates account corresponding to account type
        final Person user;
        if (accountType.equals("Employee")) {
            user = new Employee(firstName, lastName, email, password, "", "",
                    "", true, true, true, true);
        } else {
            // Patient
            user = new Patient(firstName, lastName, email, password);
        }

        //Attempts to create user in firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    //Gets result of firebase creation
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mDatabase.getReference( "AllUsers").child(mAuth.getCurrentUser().getUid()).child("General Info").setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast t1;
                                                    if (task.isSuccessful()) {
                                                        Log.d("good", "createUserWithEmail:success");
                                                        t1 = Toast.makeText(CreateAccount.this, "Account Created", Toast.LENGTH_SHORT);
                                                        Intent intent = new Intent(CreateAccount.this, Home.class);
                                                        startActivityForResult (intent,0);
                                                    } else {
                                                        Log.d("error", "createUserWithEmail:Firebase database error");
                                                        t1 = Toast.makeText(CreateAccount.this, "Database Error", Toast.LENGTH_SHORT);
                                                    }
                                                    t1.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                                                    t1.show();
                                                }
                                            });
                        } else {
                            //Sign in failure
                            Toast t;
                            if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                // Account already exists, display a message to the user.
                                Log.w("error", "createUserWithEmail:Email Already Exists", task.getException());
                                t = Toast.makeText(CreateAccount.this, "Email Already Exists. Please Sign In (test)", Toast.LENGTH_LONG);
                                finish();
                            } else if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
                                // Invalid password, display a message to the user.
                                Log.w("error", "createUserWithEmail:Invalid Password", task.getException());
                                t = Toast.makeText(CreateAccount.this, "Invalid Password (Password should be at least 6 characters long)", Toast.LENGTH_LONG);
                            } else if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                                // Invalid email, display a message to the user.
                                Log.w("error", "createUserWithEmail:Invalid Email", task.getException());
                                t = Toast.makeText(CreateAccount.this, "Invalid email", Toast.LENGTH_LONG);
                            } else {
                                // Blanket error
                                Log.w("error", "createUserWithEmail:Error", task.getException());
                                t = Toast.makeText(CreateAccount.this, "Error Creating Account", Toast.LENGTH_LONG);
                            }

                            //Displays message
                            t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                            t.show();
                        }
                    }
                });
    }
}
