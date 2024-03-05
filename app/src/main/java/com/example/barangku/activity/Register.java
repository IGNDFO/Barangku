package com.example.barangku.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
private String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etregister_email = findViewById(R.id.et_email_register);
        etregister_password = findViewById(R.id.et_password_register);
        btn_register=findViewById(R.id.btn_register_button);


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
            }
        });


    }
}