package com.example.barangku.activity.user_activity.laporan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;
import com.example.barangku.activity.user_activity.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Laporan extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Laporan");
private TextView tvToolbar;
private Button btnLaporanClient, btnLaporanMasuk, btnLaporanKeluar;

private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tvToolbar=findViewById(R.id.tv_judul);
        tvToolbar.setText("Laporan");

        btnLaporanClient = findViewById(R.id.btn_laporan_client);
        btnLaporanKeluar = findViewById(R.id.btn_laporan_barang_keluar);
        btnLaporanMasuk = findViewById(R.id.btn_laporan_barang_masuk);


        ivback = findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Laporan.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnLaporanClient.setOnClickListener(v -> pindahHalaman(Laporan.this, LaporanClient.class));
        btnLaporanKeluar.setOnClickListener(v -> pindahHalaman(Laporan.this, LaporanKeluar.class));
        btnLaporanMasuk.setOnClickListener(v -> pindahHalaman(Laporan.this, LaporanMasuk.class));
    }

    private void pindahHalaman(Activity laporan, Class activitytujuan) {
        Intent intent = new Intent(laporan, activitytujuan);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}