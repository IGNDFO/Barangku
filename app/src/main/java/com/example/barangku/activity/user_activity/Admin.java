package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;

public class Admin extends AppCompatActivity {
    private TextView tvToolbar;
    private TextView tv_register;
    private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tvToolbar = findViewById(R.id.tv_judul);
        tv_register=findViewById(R.id.tv_register);
        tvToolbar.setText("Admin");

        ivback = findViewById(R.id.iv_back);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}