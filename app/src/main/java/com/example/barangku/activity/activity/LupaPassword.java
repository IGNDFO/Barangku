package com.example.barangku.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LupaPassword extends AppCompatActivity {

    private MaterialButton btnResetPassword;
    private TextInputEditText etEmail;
    private TextInputLayout ilEmail;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String email;
    private TextView tv_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        btnResetPassword = findViewById(R.id.btn_resetpassword);
        etEmail = findViewById(R.id.et_email);
        ilEmail = findViewById(R.id.il_email);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Lupa Password");
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
                Matcher matcher = pattern.matcher(email);
                boolean isValid = matcher.matches();
                if (isValid){
                    ilEmail.setError("");
                }
                else{
                    ilEmail.setError("Alamat email tidak valid");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (email.isEmpty()){
                    ilEmail.setError("");
                }
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                email = String.valueOf(etEmail.getText());
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LupaPassword.this, "Masukkan Email Anda", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LupaPassword.this, "Reset password sudah terkirim ke email anda", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LupaPassword.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
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
}