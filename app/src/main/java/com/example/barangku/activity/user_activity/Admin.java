package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.barangku.R;

public class Admin extends AppCompatActivity {
    private TextView tv_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Admin");
    }
}