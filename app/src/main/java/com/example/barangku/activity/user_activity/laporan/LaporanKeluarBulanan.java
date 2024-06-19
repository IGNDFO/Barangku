package com.example.barangku.activity.user_activity.laporan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LaporanKeluarBulanan extends AppCompatActivity {
    private ListView lvLaporanBulanan;
    private DatabaseReference barangkeluarRef = FirebaseDatabase.getInstance().getReference("BarangKeluar");
    private ImageView ivBack;
    private TextView tvToolbar;
    private List<String> listLaporanBulanan = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private Button btnPilihBulan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keluar_bulanan);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Keluar Bulanan");

        btnPilihBulan = findViewById(R.id.btn_pilih_bulan);

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanKeluarBulanan.this, LaporanKeluar.class);
            startActivity(intent);
        });

        lvLaporanBulanan = findViewById(R.id.lv_laporan_bulanan);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLaporanBulanan);
        lvLaporanBulanan.setAdapter(arrayAdapter);

        btnPilihBulan.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(LaporanKeluarBulanan.this,
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
                    // +1 because month index is 0-based
                    String bulanTahun = String.format("%04d-%02d", selectedYear, selectedMonth + 1);
                    retrieveLaporanBulanan(bulanTahun);
                }, year, month, calendar.get(Calendar.DAY_OF_MONTH));

        // Show only month and year
        datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android"));
        datePickerDialog.show();
    }

    private void retrieveLaporanBulanan(String bulanTahun) {
        barangkeluarRef.orderByChild("tanggalKeluar").startAt(bulanTahun + "-01").endAt(bulanTahun + "-31").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                listLaporanBulanan.clear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelBarangKeluar bk = dataSnapshot.getValue(ModelBarangKeluar.class);
                    if (bk != null) {
                        LocalDate tanggalKeluar = LocalDate.parse(bk.getTanggalKeluar());
                        String formattedTanggalKeluar = tanggalKeluar.format(formatter);

                        String namaHari = getNamaHari(bk.getTanggalKeluar());
                        String laporan = namaHari + ", " + formattedTanggalKeluar + "\n"
                                + "Client: " + bk.getNamaKlien() + "\n"
                                + "Alamat Client: " + bk.getAlamatKlien() + "\n"
                                + "Nama Barang: " + bk.getNamaBarang() + "\n"
                                + "Jumlah Keluar: " + bk.getJumlahBarang();
                        listLaporanBulanan.add(laporan);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LaporanKeluarBulanan.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getNamaHari(String tanggal) {
        LocalDate date = LocalDate.parse(tanggal);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }
}