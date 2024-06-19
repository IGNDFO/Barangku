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
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.example.barangku.activity.user_activity.BarangKeluar;
import com.example.barangku.activity.user_activity.MainActivity;
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

public class LaporanMasukHarian extends AppCompatActivity {
private ListView lvLaporanHarian;
private DatabaseReference barangmasukRef = FirebaseDatabase.getInstance().getReference("BarangMasuk");
private ImageView ivBack;
private TextView tvToolbar;
private List<String> listLaporanHarian = new ArrayList<>();
private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_masuk_harian);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Masuk Harian");

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanMasukHarian.this, LaporanMasuk.class);
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

        barangmasukRef.orderByChild("tanggalMasuk").equalTo(tanggalHariIni).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLaporanHarian.clear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelBarangMasuk bm = dataSnapshot.getValue(ModelBarangMasuk.class);
                    if (bm != null) {
                        LocalDate tanggalMasuk = LocalDate.parse(bm.getTanggalMasuk());
                        String formattedTanggalMasuk = tanggalMasuk.format(formatter);

                        String namaHari = getNamaHari(bm.getTanggalMasuk());
                        String laporan = namaHari + ", " + formattedTanggalMasuk + "\n"
                                + "Nama Barang: " + bm.getNamaBarang() + "\n"
                                + "Jumlah Masuk: " + bm.getJumlahBarang() + "\n"
                                + "Keterangan: " + bm.getKeterangan();

                        listLaporanHarian.add(laporan);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LaporanMasukHarian.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private String getNamaHari(String tanggal) {
        LocalDate date = LocalDate.parse(tanggal);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }
}