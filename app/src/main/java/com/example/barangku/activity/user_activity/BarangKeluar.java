package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;

public class BarangKeluar extends AppCompatActivity {
    private TextView tv_toolbar;
    private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_keluar);
        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Register");
        ivback=findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangKeluar.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}