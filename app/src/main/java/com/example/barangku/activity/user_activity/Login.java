package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextInputEditText etEmail_login,etPassword_login;
    private Button btn_login;
    private TextView tv_register,tv_lupapassword;
    private FirebaseAuth mAuth;
    private String email, password;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null ){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
           tv_register=findViewById(R.id.tv_register);
           mAuth=FirebaseAuth.getInstance();
           etEmail_login= findViewById(R.id.et_email_login);
           etPassword_login=findViewById(R.id.et_password_login);
           btn_login=findViewById(R.id.btn_login_button);
           tv_lupapassword=findViewById(R.id.tv_lupaPassword);

tv_lupapassword.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Login.this, LupaPassword.class);
        startActivity(intent);
        finish();
    }
});

   btn_login.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           email = etEmail_login.getText().toString();
           password= etPassword_login.getText().toString();

           if(email.trim().isEmpty()){
               etEmail_login.setError("Silakan Masukan Email ");
           }
           if(password.trim().isEmpty()){
               etPassword_login.setError("Silakan Masukan Password ");
           }
           String email = etEmail_login.getText().toString().trim();
           String password = etPassword_login.getText().toString().trim();

           if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

               mAuth.signInWithEmailAndPassword(email, password)
                       .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   Toast.makeText(Login.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                                   FirebaseUser user = mAuth.getCurrentUser();
                                   Intent intent = new Intent(Login.this, MainActivity.class);
                                   startActivity(intent);
                                   finish();
                               } else {

                                   Toast.makeText(Login.this, "login Gagal.",
                                           Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
           }
       }
   });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
    }
}