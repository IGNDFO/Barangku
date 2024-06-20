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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barangku.R;
import com.example.barangku.activity.model.ModelBarangMasuk;
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
    private String simpan, namaBarang, satuan, keterangan, tanggalMasuk;
    private int jumlahMasuk = 0;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StockBarang");
    DatabaseReference barangMasukref = FirebaseDatabase.getInstance().getReference("BarangMasuk");
    private List<String> namaBarangList = new ArrayList<>();
    private List<ModelStock> stockList = new ArrayList<>();

    private ModelStock currentStockItem;

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
        ivGambar = findViewById(R.id.iv_gambar_barang);

        LocalDate localDate = LocalDate.now();
        int yearToday = localDate.getYear();
        int monthToday = localDate.getMonthValue();
        int dayOfMonthToday = localDate.getDayOfMonth();

        tanggalMasuk = String.format("%04d-%02d-%02d", yearToday, monthToday, dayOfMonthToday);
        tvTanggalMasuk.setText((tanggalMasuk));

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
                try {
                    namaBarang = tvNamaBarang.getText().toString();
                    keterangan = etKeterangan.getText().toString();
                    jumlahMasuk = Integer.parseInt(etJumlah.getText().toString());
                    satuan = tvSatuan.getText().toString();
                } catch (NumberFormatException e) {
                    Toast.makeText(BarangMasuk.this, "Jumlah harus berupa angka", Toast.LENGTH_SHORT).show();
                    return;
                }

                gambarBarang = ivGambar.getDrawable() != null ? gambarBarang : Uri.EMPTY;
                if (namaBarang.trim().isEmpty() || jumlahMasuk == 0) {
                    Toast.makeText(BarangMasuk.this, "Pastikan semua data terisi dengan benar", Toast.LENGTH_SHORT).show();
                } else {
                    simpanBarangMasuk();
                    // Reset fields after saving
                    etKeterangan.setText("");
                    etNamaBarang.setText("");
                    etJumlah.setText("");
                    tvSatuan.setText("");
                    tvJumlahItem.setText("");
                    ivGambar.setImageDrawable(getResources().getDrawable(R.drawable.insert_image));
                }
            }
        });

        retrieveStockData();
    }

    private void simpanBarangMasuk() {
        String id = barangMasukref.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Gagal mendapatkan ID unik", Toast.LENGTH_SHORT).show();
            return;
        }

        ModelBarangMasuk bm = new ModelBarangMasuk(id, namaBarang, satuan, keterangan,tanggalMasuk, jumlahMasuk );

        barangMasukref.child(id).setValue(bm).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateStockBarang(currentStockItem, jumlahMasuk);
                Toast.makeText(BarangMasuk.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BarangMasuk.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStockBarang(ModelStock stockItem, int jumlahMasuk) {
        int jumlahBaru = Integer.parseInt(String.valueOf(Integer.parseInt(stockItem.getJumlahBarang()) + jumlahMasuk));
        stockItem.setJumlahBarang(String.valueOf(jumlahBaru));

        reference.child(stockItem.getId()).setValue(stockItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BarangMasuk.this, "Stok barang berhasil diperbarui", Toast.LENGTH_SHORT).show();
//                tvJumlahItem.setText(String.valueOf(jumlahBaru));
            } else {
                Toast.makeText(BarangMasuk.this, "Gagal memperbarui stok barang", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cariBarang(String searchNamaBarang) {
        boolean found = false;
        for (ModelStock ms : stockList) {
            if (ms.getNamaBarang().equalsIgnoreCase(searchNamaBarang)) {
                currentStockItem = ms;
                tvNamaBarang.setText(ms.getNamaBarang());
                tvSatuan.setText(ms.getSatuan());
                tvJumlahItem.setText(String.valueOf(ms.getJumlahBarang()));
                Glide.with(BarangMasuk.this)
                        .load(ms.getGambar())
                        .into(ivGambar);
                found = true;
                break;
            }
        }
        if (!found) {
            Toast.makeText(BarangMasuk.this, "Barang tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveStockData() {
        reference.orderByChild("enable").equalTo(true).addValueEventListener(new ValueEventListener() {
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
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                namaBarangList.clear();
//                stockList.clear();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    ModelStock ms = dataSnapshot.getValue(ModelStock.class);
//                    if (ms != null) {
//                        namaBarangList.add(ms.getNamaBarang());
//                        stockList.add(ms);
//                    }
//                }
//                setupAutoCompleteTextView();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(BarangMasuk.this, "Failed to load data", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            gambarBarang = data.getData();
            ivGambar.setImageURI(gambarBarang);
        }
//        gambarBarang = data.getData();
//        ivGambar.setImageURI(gambarBarang);
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