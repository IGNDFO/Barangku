package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelStock;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BarangMasuk extends AppCompatActivity {
    private TextView tv_toolbar, tvTanggalMasuk, tvNamaBarang, tvSatuan, tvJumlahItem;
    private AutoCompleteTextView etNamaBarang;
    private EditText etJumlah, etKeterangan;
    private Button btnSimpan, btnCari;
    private ImageView ivback, ivKalender, ivGambar;
    private Uri gambarBarang;
    private String simpan, nama, namaBarang, satuan, keterangan, jumlah, tanggal;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StockBarang");
    private List<String> namaBarangList = new ArrayList<>();
    private List<ModelStock> stockList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_masuk);

        tv_toolbar = findViewById(R.id.tv_judul);
        tv_toolbar.setText("Barang Masuk");

        ivback = findViewById(R.id.iv_back);
        etNamaBarang = findViewById(R.id.et_nama_barang);
        etJumlah = findViewById(R.id.et_jumlah_barang);
        etKeterangan = findViewById(R.id.et_keterangan);

        tvNamaBarang = findViewById(R.id.tv_nama_barang);
        tvJumlahItem = findViewById(R.id.tv_jumlah_item);
        tvSatuan = findViewById(R.id.tv_satuan);

        tvTanggalMasuk = findViewById(R.id.tv_tanggal_masuk);
        ivKalender = findViewById(R.id.iv_kalender);
        ivGambar = findViewById(R.id.iv_gambar);

        LocalDate localDate = LocalDate.now();
        int yearToday = localDate.getYear();
        int monthToday = localDate.getMonthValue();
        int dayOfMonthToday = localDate.getDayOfMonth();

        tanggal = String.format("%04d-%02d-%02d", yearToday, monthToday, dayOfMonthToday);
        tvTanggalMasuk.setText((tanggal));

        btnCari = findViewById(R.id.btn_cari_barang);
        btnSimpan = findViewById(R.id.btn_simpan);

        /**   ivKalender.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Barang_Masuk.this,
        new DatePickerDialog.OnDateSetListener() {
        @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, dayOfMonth);

        String formatTanggal = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(selectedCalendar.getTime());
        tvTanggalMasuk.setText(formatTanggal);

        }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
        }
        });**/




//        Spinner sp = (Spinner) findViewById(R.id.sp_satuan);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.Pilih,
//                android.R.layout.simple_spinner_item
//        );
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sp.setAdapter(adapter);
//
//        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                simpan = adapterView.getItemAtPosition(pos).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangMasuk.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchNamaBarang = etNamaBarang.getText().toString();
                if (!searchNamaBarang.isEmpty()) {
                    cariBarang(searchNamaBarang);
                } else {
                    Toast.makeText(BarangMasuk.this, "Masukkan nama barang", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(BarangMasuk.this)
                        .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                        .crop()
                        .compress(256)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nama = etNamaBarang.getText().toString();
                namaBarang = tvNamaBarang.getText().toString();
                keterangan = etKeterangan.getText().toString();
                jumlah = etJumlah.getText().toString();
                satuan = tvSatuan.getText().toString();

            }
        });
        retrieveStockData();
    }

    private void cariBarang(String searchNamaBarang) {
        boolean found = false;
        for (ModelStock ms : stockList) {
            if (ms.getNamaBarang().equalsIgnoreCase(searchNamaBarang)) {
                tvNamaBarang.setText(ms.getNamaBarang());
                tvSatuan.setText(ms.getSatuan());
                tvJumlahItem.setText(String.valueOf(ms.getJumlahBarang()));
                found = true;
                break;
            }
        }
        if (!found) {
            Toast.makeText(BarangMasuk.this, "Barang tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveStockData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namaBarangList.clear();
                stockList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelStock ms = dataSnapshot.getValue(ModelStock.class);
                    if (ms != null) {
                        namaBarangList.add(ms.getNamaBarang());
                        stockList.add(ms);
                    }
                }
                setupAutoCompleteTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarangMasuk.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gambarBarang = data.getData();
        ivGambar.setImageURI(gambarBarang);
    }

    private void setupAutoCompleteTextView() {
        ArrayAdapter<String> adapterBarang = new ArrayAdapter<>(this, R.layout.list_nama_barang, namaBarangList);
        etNamaBarang.setAdapter(adapterBarang);
        etNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BarangMasuk.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}