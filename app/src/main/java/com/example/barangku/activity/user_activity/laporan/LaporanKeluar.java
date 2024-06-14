package com.example.barangku.activity.user_activity.laporan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;

public class LaporanKeluar extends AppCompatActivity {
private TextView tvToolbar;
private ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keluar);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Laporan Barang Keluar");

        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaporanKeluar.this, Laporan.class);
                startActivity(intent);
            }
        });


    }
}