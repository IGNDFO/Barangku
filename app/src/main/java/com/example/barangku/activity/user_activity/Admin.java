package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.barangku.R;

import org.checkerframework.checker.units.qual.A;

public class Admin extends AppCompatActivity {
    private TextView tvToolbar;
    private ImageView ivBack;
    private LinearLayout tambahUser, listKaryawan;
    private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Admin");

        tambahUser = findViewById(R.id.tambah_user);
        listKaryawan = findViewById(R.id.list_karyawan);
        ivback = findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, MainActivity.class);
                startActivity(intent);
            }
        });
       tambahUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, Register.class);
                startActivity(intent);
            }
        });


        listKaryawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, Karyawan.class);
                startActivity(intent);
            }
        });
    }
}