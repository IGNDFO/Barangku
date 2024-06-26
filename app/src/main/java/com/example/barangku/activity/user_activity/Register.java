package com.example.barangku.activity.user_activity;

import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {
    private TextInputEditText etregister_nama,etregister_email,etregister_password;
    private Button btn_register;
    private TextView tv_login,tv_toolbar;
    private Spinner spJabatan;
    private String email, password;
    private String nama,jabatan, token;
    private FirebaseAuth mAuth;
    private ImageView ivback;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etregister_nama = findViewById(R.id.et_nama_register);
        etregister_email = findViewById(R.id.et_email_register);
        etregister_password = findViewById(R.id.et_password_register);

        tv_login = findViewById(R.id.tv_login);
        btn_register = findViewById(R.id.btn_register_button);
        ivback = findViewById(R.id.iv_back);

        spJabatan = findViewById(R.id.sp_jabatan);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                Register.this,
                R.array.Jabatan,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJabatan.setAdapter(adapter);

        spJabatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                jabatan = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tv_toolbar = findViewById(R.id.tv_judul);
        tv_toolbar.setText("Tambah User");

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

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        // Set up register button click listener
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nama = etregister_nama.getText().toString();
                email = etregister_email.getText().toString();
                password = etregister_password.getText().toString();

                if (nama.trim().isEmpty()) {
                    etregister_nama.setError("Silakan Masukan Nama");
                    return;
                }
                if (email.trim().isEmpty()) {
                    etregister_email.setError("Silakan Masukan Email");
                    return;
                }
                if (password.trim().isEmpty()) {
                    etregister_password.setError("Silakan Masukan Password");
                    return;
                }

                // Check if email and password fields are not empty
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    // Create a new user account with email and password
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        token = task.getResult();
                                        String userId = user.getUid();
                                        ModelUser mu = new ModelUser(userId, email, nama, jabatan, token);
                                        reference.child(userId).setValue(mu);

                                        Toast.makeText(Register.this, "Berhasil Daftar Akun", Toast.LENGTH_SHORT).show();
//
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Register.this, "Gagal Mendapatkan Token FCM: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Tambahkan lebih banyak detail tentang kesalahan yang terjadi
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(Register.this, "Gagal Membuat Akun: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Masukan Email dan Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, Login.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
    }

}