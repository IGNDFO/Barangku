package com.example.barangku.activity.user_activity.laporan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.barangku.R;

public class LaporanMasuk extends AppCompatActivity {
    private LinearLayout laporanHarian;
    private LinearLayout laporanMingguan;
    private LinearLayout laporanBulanan;
    private TextView tvToolbar;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_masuk);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Laporan Barang Masuk");

        laporanHarian = findViewById(R.id.laporan_harian);
        laporanMingguan = findViewById(R.id.laporan_mingguan);
        laporanBulanan = findViewById(R.id.laporan_bulanan);


        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaporanMasuk.this, Laporan.class);
                startActivity(intent);
            }
        });
        laporanHarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaporanMasuk.this, LaporanMasukHarian.class);
                startActivity(intent);
            }
        });

        laporanMingguan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaporanMasuk.this, LaporanMasukMingguan.class);
                startActivity(intent);
            }
        });

        laporanBulanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaporanMasuk.this, LaporanMasukBulanan.class);
                startActivity(intent);
            }
        });


    }
}
