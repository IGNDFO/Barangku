package com.example.barangku.activity.user_activity.laporan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LaporanMasukBulanan extends AppCompatActivity {
private ImageView ivBack;
private TextView tvToolbar;
private ListView lvLaporanBulanan;
private DatabaseReference barangmasukRef = FirebaseDatabase.getInstance().getReference("BarangMasuk");

private List<String> listLaporanBulanan = new ArrayList<>();
private Button btnPilihTanggalMulai, btnPilihTanggalAkhir,btnTampilkanLaporan;
private String tanggalMulai, tanggalAkhir;
private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_masuk_bulanan);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Masuk Bulanan");

        btnPilihTanggalMulai = findViewById(R.id.btn_pilih_tanggal_mulai);
        btnPilihTanggalAkhir = findViewById(R.id.btn_pilih_tanggal_akhir);
        btnTampilkanLaporan = findViewById(R.id.btn_tampilkan_laporan);

        lvLaporanBulanan = findViewById(R.id.lv_laporan_bulanan);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLaporanBulanan);
        lvLaporanBulanan.setAdapter(arrayAdapter);

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanMasukBulanan.this, LaporanMasuk.class);
            startActivity(intent);
        });

        btnPilihTanggalMulai.setOnClickListener(v -> showDatePickerDialog(true));
        btnPilihTanggalAkhir.setOnClickListener(v -> showDatePickerDialog(false));
        btnTampilkanLaporan.setOnClickListener(v -> {
            if (tanggalMulai != null && tanggalAkhir != null) {
                retrieveLaporanBulanan(tanggalMulai, tanggalAkhir);
            } else {
                Toast.makeText(this, "Pilih tanggal mulai dan akhir", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(LaporanMasukBulanan.this,
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
                    // +1 because month index is 0-based
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, dayOfMonth);
                    if (isStartDate) {
                        tanggalMulai = selectedDate;
                        btnPilihTanggalMulai.setText("Mulai: " + selectedDate);
                    } else {
                        tanggalAkhir = selectedDate;
                        btnPilihTanggalAkhir.setText("Akhir: " + selectedDate);
                    }
                }, year, month, calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void retrieveLaporanBulanan(String tanggalMulai, String tanggalAkhir) {
        barangmasukRef.orderByChild("tanggalMasuk").startAt(tanggalMulai).endAt(tanggalAkhir).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLaporanBulanan.clear();
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
                        listLaporanBulanan.add(laporan);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LaporanMasukBulanan.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getNamaHari(String tanggal) {
        LocalDate date = LocalDate.parse(tanggal);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }
}