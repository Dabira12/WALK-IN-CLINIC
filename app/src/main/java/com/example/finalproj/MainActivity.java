package com.example.finalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.Gravity;
// import android.app.AlertDialog;
// import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    static boolean adminLoggedIn;
    static WalkInClinic clinic;
    static Administrator adminCredentials;
    EditText emailaddress, pass;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        adminLoggedIn = false;
        adminCredentials = new Administrator("admin", "admin", "admin","5T5ptQ");
        clinic = new WalkInClinic("Clinic Name", "");
        initFields();
    }

    private void initFields(){
        emailaddress = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    /*
    public void onPasswordReset(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Reset password")
                .setMessage("Confirm password reset email to be sent")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String email = emailaddress.getText().toString().trim();

                        if (email.equals("")) {
                            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave email field blank", Toast.LENGTH_LONG);
                            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                            noInputToast.show();
                            return;
                        }

                        // Hardcoded admin account
                        if (email.equals("admin@admin.com") || email.equals("admin")) {;
                            Toast t = Toast.makeText(getApplicationContext(), "Cannot reset admin password", Toast.LENGTH_LONG);
                            t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                            t.show();
                            Log.d("error", "Cannot reset admin password");
                            return;
                        }

                        auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast t;
                                        if (task.isSuccessful()) {
                                            t = Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG);
                                            Log.d("email", "Email sent.");
                                        } else {
                                            t = Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_LONG);
                                            Log.d("email", "Please enter a valid email address");
                                        }
                                        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                                        t.show();
                                    }
                                });
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    */

    public static boolean adminLoggedIn() {
        return adminLoggedIn;
    }

    public static WalkInClinic getClinic() {
        return clinic;
    }

    public static Administrator getAdmin() {
        return adminCredentials;
    }

    public void onLogin(View view) {

        //Takes value of fields
        String email = emailaddress.getText().toString().trim();
        String password = pass.getText().toString().trim();

        //Empty Field Contingency
        if (email.equals("") || password.equals("")) {
            Toast noInputToast = Toast.makeText(getApplicationContext(), "Do not leave fields blank", Toast.LENGTH_LONG);
            noInputToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            noInputToast.show();
            return;
        }

        // Hardcoded admin account
        if (email.equals(adminCredentials.getEmail()) && password.equals(adminCredentials.getPassword())) {
            adminLoggedIn = true;
            Toast toast = Toast.makeText(getApplicationContext(), "Successfully logged in.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
            toast.show();
            Log.d("good","loggedin");
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivityForResult (intent,0);
            return;
        }

        // Convert password to hash
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            password = ConvertFunctions.bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            Log.d("bad", "Hash Password:failure");
            Toast t = Toast.makeText(MainActivity.this, "Error Hashing Password", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        //Attempts to sign in with value
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            //Checks if firebase was able to sign in
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast toast;

                if (task.isSuccessful()) {
                    //If firebase was able to
                    toast = Toast.makeText(getApplicationContext(), "Successfully logged in.", Toast.LENGTH_LONG);
                    Log.d("good","loggedin");
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivityForResult (intent,0);
                } else {
                    //If it was not
                    toast = Toast.makeText(getApplicationContext(), "Incorrrect username or password", Toast.LENGTH_LONG);
                    Log.d("bad","nouser");
                }
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                toast.show();
            }
        });
    }

    public void onGoToCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivityForResult (intent,0);
    }
}
