package com.example.barangku.activity;

import static android.content.ContentValues.TAG;

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

public class Login extends AppCompatActivity {
    private EditText etEmail_login,etPassword_login;
    private Button btn_login;
    private FirebaseAuth mAuth;
    private String email, password;
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
        setContentView(R.layout.activity_login);

           mAuth=FirebaseAuth.getInstance();
           etEmail_login= findViewById(R.id.et_email_login);
           etPassword_login=findViewById(R.id.et_password_login);
           btn_login=findViewById(R.id.btn_login_button);

   btn_login.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           email= etEmail_login.getText().toString();
           password= etPassword_login.getText().toString();

           if(email.trim().isEmpty()){
               etEmail_login.setError("Silakan Masukan Email ");
           }
           if(password.trim().isEmpty()){
               etPassword_login.setError("Silakan Masukan Password ");
           }
           mAuth.signInWithEmailAndPassword(email, password)
                   .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(Login.this, MainActivity.class);
                               startActivity(intent);
                               finish();
                           } else {
                               Toast.makeText(Login.this, "Email atau Password Yang Dimasukan Salah", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(Login.this, Login.class);
                               startActivity(intent);
                               finish();
                           }
                       }
                   });

       }
   });
    }
}