package com.example.barangku.activity.user_activity.laporan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelBarangKeluar;
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LaporanKeluarHarian extends AppCompatActivity {

    private ListView lvLaporanHarian;
    private DatabaseReference barangkeluarRef = FirebaseDatabase.getInstance().getReference("BarangKeluar");
    private ImageView ivBack;
    private TextView tvToolbar;
    private List<String> listLaporanHarian = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keluar_harian);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Keluar Harian");

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanKeluarHarian.this, LaporanKeluar.class);
            startActivity(intent);
        });

        lvLaporanHarian = findViewById(R.id.lv_laporan_harian);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLaporanHarian);
        lvLaporanHarian.setAdapter(arrayAdapter);

        retrieveLaporanHarian();
    }

    private void retrieveLaporanHarian() {
        LocalDate today = LocalDate.now();
        String tanggalHariIni = today.toString();

        barangkeluarRef.orderByChild("tanggalKeluar").equalTo(tanggalHariIni).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLaporanHarian.clear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelBarangKeluar  bk = dataSnapshot.getValue(ModelBarangKeluar.class);
                    if (bk != null) {
                        LocalDate tanggalKeluar = LocalDate.parse(bk.getTanggalKeluar());
                        String formattedTanggalKeluar = tanggalKeluar.format(formatter);

                        String namaHari = getNamaHari(bk.getTanggalKeluar());
                        String laporan = namaHari + ", " + formattedTanggalKeluar + "\n"
                                + "Client: " + bk.getNamaKlien() + "\n"
                                + "Alamat Client: " + bk.getAlamatKlien() + "\n"
                                + "Nama Barang: " + bk.getNamaBarang() + "\n"
                                + "Jumlah Keluar: " + bk.getJumlahBarang();
                        listLaporanHarian.add(laporan);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LaporanKeluarHarian.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getNamaHari(String tanggal) {
        LocalDate date = LocalDate.parse(tanggal);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }
}