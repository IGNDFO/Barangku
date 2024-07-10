package com.example.barangku.activity.user_activity.laporan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelBarangKeluar;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LaporanKeluarBulanan extends AppCompatActivity {
    private ListView lvLaporanBulanan;
    private DatabaseReference barangkeluarRef = FirebaseDatabase.getInstance().getReference("BarangKeluar");
    private ImageView ivBack;
    private TextView tvToolbar, tvJumlahData;
    private Button btnPilihTanggalMulai, btnPilihTanggalAkhir, btnTampilkanLaporan;
    private String tanggalMulai, tanggalAkhir;
    private Spinner spinnerFilter, spinnerSort;
    private List<ModelBarangKeluar> originalList = new ArrayList<>();
    private List<String> listLaporanBulanan = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keluar_bulanan);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Keluar Bulanan");

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanKeluarBulanan.this, LaporanKeluar.class);
            startActivity(intent);
        });

        lvLaporanBulanan = findViewById(R.id.lv_laporan_bulanan);
        tvJumlahData = findViewById(R.id.tv_jumlah_data);
        spinnerFilter = findViewById(R.id.spinner_filter);
        spinnerSort = findViewById(R.id.spinner_sort);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLaporanBulanan);
        lvLaporanBulanan.setAdapter(arrayAdapter);

        btnPilihTanggalMulai = findViewById(R.id.btn_pilih_tanggal_mulai);
        btnPilihTanggalAkhir = findViewById(R.id.btn_pilih_tanggal_akhir);
        btnTampilkanLaporan = findViewById(R.id.btn_tampilkan_laporan);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this, R.array.filter_options, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sort_options_keluar, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        btnPilihTanggalMulai.setOnClickListener(v -> showDatePickerDialog(true));
        btnPilihTanggalAkhir.setOnClickListener(v -> showDatePickerDialog(false));
        btnTampilkanLaporan.setOnClickListener(v -> {
            if (tanggalMulai != null && tanggalAkhir != null) {
                retrieveLaporanBulanan(tanggalMulai, tanggalAkhir);
            } else {
                Toast.makeText(this, "Pilih tanggal mulai dan akhir", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortData("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortData("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(LaporanKeluarBulanan.this,
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
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
        barangkeluarRef.orderByChild("tanggalKeluar").startAt(tanggalMulai).endAt(tanggalAkhir).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalList.clear();
                listLaporanBulanan.clear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelBarangKeluar bk = dataSnapshot.getValue(ModelBarangKeluar.class);
                    if (bk != null) {
                        originalList.add(bk);
                    }
                }
                filterAndSortData("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LaporanKeluarBulanan.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAndSortData(String keyword) {
        List<ModelBarangKeluar> filteredList = new ArrayList<>();
        String filterOption = spinnerFilter.getSelectedItem().toString();
        String sortOption = spinnerSort.getSelectedItem().toString();

        for (ModelBarangKeluar bk : originalList) {
            if (filterOption.equals("Semua") || (filterOption.equals("Jumlah Barang > 50") && bk.getJumlahBarang() > 50)) {
                if (keyword.isEmpty() || bk.getNamaKlien().toLowerCase().contains(keyword.toLowerCase()) ||
                        bk.getNamaBarang().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(bk);
                }
            }
        }

        switch (sortOption) {
            case "Nama Klien (Naik)":
                Collections.sort(filteredList, Comparator.comparing(ModelBarangKeluar::getNamaKlien));
                break;
            case "Nama Klien (Turun)":
                Collections.sort(filteredList, (bk1, bk2) -> bk2.getNamaKlien().compareTo(bk1.getNamaKlien()));
                break;
            case "Jumlah Barang (Naik)":
                Collections.sort(filteredList, Comparator.comparingLong(ModelBarangKeluar::getJumlahBarang));
                break;
            case "Jumlah Barang (Turun)":
                Collections.sort(filteredList, (bk1, bk2) -> Long.compare(bk2.getJumlahBarang(), bk1.getJumlahBarang()));
                break;
        }

        updateListView(filteredList);
    }

    private void updateListView(List<ModelBarangKeluar> list) {
        listLaporanBulanan.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        for (ModelBarangKeluar bk : list) {
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
        arrayAdapter.notifyDataSetChanged();
        tvJumlahData.setText("Jumlah Data: " + list.size());
    }

    private String getNamaHari(String tanggal) {
        LocalDate date = LocalDate.parse(tanggal);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }
}
