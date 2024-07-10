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
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LaporanMasukMingguan extends AppCompatActivity {

    private ListView lvLaporanMingguan;
    private ImageView ivBack;
    private TextView tvToolbar, tvJumlahData;
    private DatabaseReference barangmasukRef = FirebaseDatabase.getInstance().getReference("BarangMasuk");
    private List<ModelBarangMasuk> originalList = new ArrayList<>();
    private List<String> listLaporanMingguan = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spinnerFilter, spinnerSort;
    private Button btnPilihTanggal;
    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_masuk_mingguan);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Masuk Mingguan");

        lvLaporanMingguan = findViewById(R.id.lv_laporan_mingguan);
        tvJumlahData = findViewById(R.id.tv_jumlah_data);
        spinnerFilter = findViewById(R.id.spinner_filter);
        spinnerSort = findViewById(R.id.spinner_sort);
        btnPilihTanggal = findViewById(R.id.btn_pilih_tanggal);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLaporanMingguan);
        lvLaporanMingguan.setAdapter(arrayAdapter);

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanMasukMingguan.this, LaporanMasuk.class);
            startActivity(intent);
        });

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this, R.array.filter_options, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sort_options_masuk, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

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

        btnPilihTanggal.setOnClickListener(v -> showDatePickerDialog());

        retrieveLaporanMingguan();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(LaporanMasukMingguan.this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    retrieveLaporanMingguan();
                }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());

        datePickerDialog.show();
    }

    private void retrieveLaporanMingguan() {
        LocalDate today = selectedDate != null ? selectedDate : LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        String tanggalAwal = startOfWeek.toString();
        String tanggalAkhir = endOfWeek.toString();

        barangmasukRef.orderByChild("tanggalMasuk").startAt(tanggalAwal).endAt(tanggalAkhir).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalList.clear();
                listLaporanMingguan.clear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelBarangMasuk bm = dataSnapshot.getValue(ModelBarangMasuk.class);
                    if (bm != null) {
                        originalList.add(bm);
                    }
                }
                filterAndSortData("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LaporanMasukMingguan.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAndSortData(String keyword) {
        List<ModelBarangMasuk> filteredList = new ArrayList<>();
        String filterOption = spinnerFilter.getSelectedItem().toString();
        String sortOption = spinnerSort.getSelectedItem().toString();

        for (ModelBarangMasuk bm : originalList) {
            if (filterOption.equals("Semua") || (filterOption.equals("Jumlah Barang > 50") && bm.getJumlahBarang() > 50)) {
                if (keyword.isEmpty() || bm.getNamaBarang().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(bm);
                }
            }
        }

        switch (sortOption) {
            case "Nama Barang (Naik)":
                Collections.sort(filteredList, Comparator.comparing(ModelBarangMasuk::getNamaBarang));
                break;
            case "Nama Barang (Turun)":
                Collections.sort(filteredList, (bm1, bm2) -> bm2.getNamaBarang().compareTo(bm1.getNamaBarang()));
                break;
            case "Jumlah Barang (Naik)":
                Collections.sort(filteredList, Comparator.comparingInt(ModelBarangMasuk::getJumlahBarang));
                break;
            case "Jumlah Barang (Turun)":
                Collections.sort(filteredList, (bm1, bm2) -> Integer.compare(bm2.getJumlahBarang(), bm1.getJumlahBarang()));
                break;
        }

        updateListView(filteredList);
    }

    private void updateListView(List<ModelBarangMasuk> list) {
        listLaporanMingguan.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        for (ModelBarangMasuk bm : list) {
            LocalDate tanggalMasuk = LocalDate.parse(bm.getTanggalMasuk());
            String formattedTanggalMasuk = tanggalMasuk.format(formatter);

            String namaHari = getNamaHari(bm.getTanggalMasuk());
            String laporan = namaHari + ", " + formattedTanggalMasuk + "\n"
                    + "Nama Barang: " + bm.getNamaBarang() + "\n"
                    + "Jumlah Masuk: " + bm.getJumlahBarang() + "\n"
                    + "Keterangan: " + bm.getKeterangan();
            listLaporanMingguan.add(laporan);
        }
        arrayAdapter.notifyDataSetChanged();
        tvJumlahData.setText("Jumlah Data: " + list.size());
    }

    private String getNamaHari(String tanggal) {
        LocalDate date = LocalDate.parse(tanggal);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }
}
