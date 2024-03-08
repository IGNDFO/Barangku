package com.example.barangku.activity;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
private EditText etregister_email,etregister_password;
private Button btn_register;
private TextView tv_login;
private String email, password;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            reload();
        }
    }
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etregister_email = findViewById(R.id.et_email_register);
        etregister_password = findViewById(R.id.et_password_register);
        tv_login=findViewById(R.id.tv_login);
        btn_register=findViewById(R.id.btn_register_button);
        mAuth = FirebaseAuth.getInstance();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

            btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=etregister_email.getText().toString();
                password=etregister_password.getText().toString();

                if(email.trim().isEmpty()){
                    etregister_email.setError("Silakan Masukan Email ");
                }
                if(password.trim().isEmpty()){
                    etregister_password .setError("Silakan Masukan Password ");
                }

// Check if the email and password fields are not empty
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    // Proceed with Firebase sign in
//                    mAuth.signInWithEmailAndPassword(email, password)
//                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
//                                        FirebaseUser user =mAuth.getCurrentUser();
//                                        Intent intent = new Intent(Register.this, Login.class);
//                                           startActivity(intent);
//                                           finish();
////                                        updateUI(user);
//                                    } else {
//                                        // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                        Toast.makeText(Register.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
////                                        updateUI(null);
//                                    }
//                                }
//                            }

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success, update UI with the signed-up user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign up fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    // Display a message to the user telling them to enter an email and password
                    Toast.makeText(Register.this, "Please enter an email and password", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}