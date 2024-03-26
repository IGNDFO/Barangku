package com.example.barangku.activity.activity;

import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class Register extends AppCompatActivity {
    private TextInputEditText etregister_email,etregister_password;
    private Button btn_register;
    private TextView tv_login,tv_toolbar;
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

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        etregister_email = findViewById(R.id.et_email_register);
        etregister_password = findViewById(R.id.et_password_register);
        tv_login=findViewById(R.id.tv_login);
        btn_register=findViewById(R.id.btn_register_button);

        // Set up toolbar title
        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Register");

        // Set up login button click listener
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                // Close Register activity when login button is clicked
                finish();
            }
        });

        // Set up register button click listener
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=etregister_email.getText().toString();
                password=etregister_password.getText().toString();

                if(email.trim().isEmpty()){
                    etregister_email.setError("Silakan Masukan Email ");
                    return;
                }
                if(password.trim().isEmpty()){
                    etregister_password .setError("Silakan Masukan Password ");
                    return;
                }

                // Check if email and password fields are not empty
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    // Create a new user account with email and password
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<AuthResult> task) {
                                                           // If the registration is successful, send a verification email and close Register activity
                                                           if (task.isSuccessful()) {
                                                               FirebaseUser user = mAuth.getCurrentUser();
                                                               user.sendEmailVerification()
                                                                       .addOnCompleteListener(new OnCompleteListener<Void>(){
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if (task.isSuccessful()) {
                                                                                   Toast.makeText(Register.this, "Berhasil Daftar Akun, Silakan Verif email",
                                                                                           Toast.LENGTH_SHORT).show();
                                                                                   // Close Register activity when verification email is sent
                                                                                   finish();
                                                                               }
                                                                               else {
                                                                                   Log.w(TAG, "sendEmailVerification:failure", task.getException());
                                                                                   Toast.makeText(Register.this, "Gagal Daftar akun",
                                                                                           Toast.LENGTH_SHORT).show();
                                                                               }
                                                                           }
                                                                       });
                                                           }
                                                           // If the registration is not successful, display a failure message
                                                           else {
                                                               Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                               Toast.makeText(Register.this, "Gagal Daftar akun",
                                                                       Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                                                   }
                            );
                }
                // If email or password field is empty, display an error message
                else {
                    Toast.makeText(Register.this, "Masukan Email dan Password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
//
//    @Override
//    public void onBackPressed() {
//        // Check if user is on the first screen of the app (MainActivity)
//        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            // Display a dialog asking if the user wants to exit the app
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Are you sure you want to exit the app?")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // Close the app
//                            moveTaskToBack(true);
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(1);
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // Do nothing and keep the app open
//                            dialog.cancel();
//                        }
//                    });
//            AlertDialog alert = builder.create();
//            alert.show();
//        }
//        // If the user is not on the first screen, just close the current activity
//        else {
//            finish();
//        }
    //}

}