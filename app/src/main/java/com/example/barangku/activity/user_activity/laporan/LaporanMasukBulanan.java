package com.example.barangku.activity.user_activity.laporan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LaporanMasukBulanan extends AppCompatActivity {
private ListView lvLaporanBulanan;
private DatabaseReference barangmasukRef = FirebaseDatabase.getInstance().getReference("BarangMasuk");
private List<String> listLaporanBulanan = new ArrayList<>();
private ArrayAdapter<String> arrayAdapter;
private Button btnPilihBulan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_masuk_bulanan);

        lvLaporanBulanan = findViewById(R.id.lv_laporan_bulanan);
        btnPilihBulan = findViewById(R.id.btn_pilih_bulan);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLaporanBulanan);
        lvLaporanBulanan.setAdapter(arrayAdapter);

        btnPilihBulan.setOnClickListener(v -> showDatePickerDialog());
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(LaporanMasukBulanan.this,
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
        barangmasukRef.orderByChild("tanggalMasuk").startAt(bulanTahun + "-01").endAt(bulanTahun + "-31").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                listLaporanBulanan.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelBarangMasuk bm = dataSnapshot.getValue(ModelBarangMasuk.class);
                    if (bm != null) {
                        String namaHari = getNamaHari(bm.getTanggalMasuk());
                        String laporan = namaHari + "\n"
                                + "Tanggal: " + bm.getTanggalMasuk() + "\n"
                                + "Nama Barang: " + bm.getNamaBarang() + "\n"
                                + "Jumlah Masuk: " + bm.getJumlahBarang() + "\n"
                                + "Satuan: " + bm.getSatuan() + "\n"
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