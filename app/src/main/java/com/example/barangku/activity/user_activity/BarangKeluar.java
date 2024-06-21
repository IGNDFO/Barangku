package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barangku.R;
import com.example.barangku.activity.model.ModelBarangKeluar;
import com.example.barangku.activity.model.ModelClient;
import com.example.barangku.activity.model.ModelStock;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BarangKeluar extends AppCompatActivity {
    private TextView tv_toolbar, tvTanggalKeluar, tvNamaBarang, tvSatuan, tvJumlahItem, tvAlamatKlien;
    private AutoCompleteTextView etNamaBarang, etNamaKlien;
    private EditText etJumlah, etKeterangan;
    private Button btnSimpan, btnCari;
    private ImageView ivback, ivKalender, ivGambar;
    private Uri gambarBarang;
    private String simpan, namaBarang, satuan, keterangan, tanggalKeluar, namaKlien, alamatKlien;
    private long jumlahKeluar = 0;
    DatabaseReference stockReference = FirebaseDatabase.getInstance().getReference("StockBarang");
    DatabaseReference barangKeluarRef = FirebaseDatabase.getInstance().getReference("BarangKeluar");
    DatabaseReference clientReference = FirebaseDatabase.getInstance().getReference("Client");
    private List<String> namaBarangList = new ArrayList<>();
    private List<String> namaClientList = new ArrayList<>();


    private List<ModelStock> stockList = new ArrayList<>();
    private List<ModelClient> clientList = new ArrayList<>();
    private ModelStock currentStockItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_keluar);

        tv_toolbar = findViewById(R.id.tv_judul);
        tv_toolbar.setText("Barang Keluar");

        ivback = findViewById(R.id.iv_back);
        etNamaBarang = findViewById(R.id.et_nama_barang);
        etNamaKlien = findViewById(R.id.et_nama_klien);
        etJumlah = findViewById(R.id.et_jumlah_barang);
        etKeterangan = findViewById(R.id.et_keterangan);

        tvNamaBarang = findViewById(R.id.tv_nama_barang);
        tvJumlahItem = findViewById(R.id.tv_jumlah_item);
        tvSatuan = findViewById(R.id.tv_satuan);
        tvAlamatKlien = findViewById(R.id.tv_alamat_client);

        tvTanggalKeluar = findViewById(R.id.tv_tanggal_keluar);
        ivKalender = findViewById(R.id.iv_kalender);
        ivGambar = findViewById(R.id.iv_gambar_barang);

        retrieveStockData();
        retrieveClientData();

        ArrayAdapter<String> adapterBarang = new ArrayAdapter<>(BarangKeluar.this, R.layout.list_nama_barang, namaBarangList);
        etNamaBarang.setAdapter(adapterBarang);
        etNamaBarang.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(BarangKeluar.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show());

        ArrayAdapter<String> adapterClient = new ArrayAdapter<>(BarangKeluar.this, R.layout.list_nama_klien, namaClientList);
        etNamaKlien.setAdapter(adapterClient);

        etNamaBarang.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBarang = parent.getItemAtPosition(position).toString();
            Toast.makeText(BarangKeluar.this, selectedBarang, Toast.LENGTH_SHORT).show();
        });
        etNamaKlien.setOnItemClickListener((parent, view, position, id) -> {
            String selectedClient = parent.getItemAtPosition(position).toString();
            for (ModelClient client : clientList) {
                if (client.getNama().equals(selectedClient)) {
                    tvAlamatKlien.setText(client.getAlamat());
                    break;
                }
            }
        });

        LocalDate localDate = LocalDate.now();
        int yearToday = localDate.getYear();
        int monthToday = localDate.getMonthValue();
        int dayOfMonthToday = localDate.getDayOfMonth();

        tanggalKeluar = String.format("%04d-%02d-%02d", yearToday, monthToday, dayOfMonthToday);
        tvTanggalKeluar.setText((tanggalKeluar));

        btnCari = findViewById(R.id.btn_cari_barang);
        btnSimpan = findViewById(R.id.btn_simpan);

        ivback.setOnClickListener(v -> {
            Intent intent = new Intent(BarangKeluar.this, MainActivity.class);
            startActivity(intent);
        });

        btnCari.setOnClickListener(v -> {
            String searchNamaBarang = etNamaBarang.getText().toString();
            String searchNamaClient = etNamaKlien.getText().toString();
            if (!searchNamaBarang.isEmpty() && !searchNamaClient.isEmpty()) {
                cariBarang(searchNamaBarang);
                cariKlien(searchNamaClient);
            } else {
                Toast.makeText(BarangKeluar.this, "Masukkan nama barang", Toast.LENGTH_SHORT).show();
            }
        });

        btnSimpan.setOnClickListener(view -> {
            if (tvNamaBarang != null && etNamaKlien != null && etKeterangan != null && etJumlah != null && tvSatuan != null && tvAlamatKlien != null) {
                namaBarang = tvNamaBarang.getText().toString();
                namaKlien = etNamaKlien.getText().toString();
                keterangan = etKeterangan.getText().toString();
                try {
                    jumlahKeluar = Long.parseLong(etJumlah.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(BarangKeluar.this, "Jumlah harus berupa angka", Toast.LENGTH_SHORT).show();
                    return;
                }
                satuan = tvSatuan.getText().toString();
                alamatKlien = tvAlamatKlien.getText().toString();

                gambarBarang = ivGambar.getDrawable() != null ? gambarBarang : Uri.EMPTY;
                if (namaBarang.trim().isEmpty() || jumlahKeluar == 0 || namaKlien.trim().isEmpty()) {
                    Toast.makeText(BarangKeluar.this, "Pastikan semua data terisi dengan benar", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentStockItem != null) {
                        long stokTersedia = Long.parseLong(currentStockItem.getJumlahBarang());
                        if (jumlahKeluar > stokTersedia) {
                            Toast.makeText(BarangKeluar.this, "Jumlah barang keluar melebihi stok yang tersedia!!" , Toast.LENGTH_SHORT).show();
                        } else {
                            simpanBarangKeluar();
                            resetForm();
                        }
                    } else {
                        Toast.makeText(BarangKeluar.this, "Barang tidak ditemukan!!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(BarangKeluar.this, "Periksa kembali elemen yang ada!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        etKeterangan.setText("");
        etNamaBarang.setText("");
        etJumlah.setText("");
        tvSatuan.setText("");
        tvJumlahItem.setText("");
        etNamaKlien.setText("");
        tvAlamatKlien.setText("");
        ivGambar.setImageDrawable(getResources().getDrawable(R.drawable.insert_image));
    }

    private void simpanBarangKeluar() {
        String id = barangKeluarRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Gagal mendapatkan ID unik", Toast.LENGTH_SHORT).show();
            return;
        }
        ModelBarangKeluar bk = new ModelBarangKeluar(id, namaBarang, satuan, keterangan, tanggalKeluar, namaKlien, alamatKlien, jumlahKeluar);

        barangKeluarRef.child(id).setValue(bk).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateStockBarang(currentStockItem, jumlahKeluar);
                Toast.makeText(BarangKeluar.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BarangKeluar.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStockBarang(ModelStock stockItem, long jumlahKeluar) {
        long jumlahBaru = Long.parseLong(stockItem.getJumlahBarang()) - jumlahKeluar;
        stockItem.setJumlahBarang(String.valueOf(jumlahBaru));

        stockReference.child(stockItem.getId()).setValue(stockItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BarangKeluar.this, "Stok barang berhasil diperbarui", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BarangKeluar.this, "Gagal memperbarui stok barang", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cariBarang(String searchNamaBarang) {
        boolean found = false;
        for (ModelStock ms : stockList) {
            if (ms.getNamaBarang().equalsIgnoreCase(searchNamaBarang)) {
                currentStockItem = ms;
                if (tvNamaBarang != null) {
                    tvNamaBarang.setText(ms.getNamaBarang());
                }
                if (tvSatuan != null) {
                    tvSatuan.setText(ms.getSatuan());
                }
                if (tvJumlahItem != null) {
                    tvJumlahItem.setText(ms.getJumlahBarang());
                }
                if (ivGambar != null) {
                    Glide.with(BarangKeluar.this)
                            .load(ms.getGambar())
                            .into(ivGambar);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            Toast.makeText(BarangKeluar.this, "Barang tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void cariKlien(String searchNamaClient) {
        boolean found = false;
        for (ModelClient mc : clientList) {
            if (mc.getNama().equalsIgnoreCase(searchNamaClient)) {
                tvAlamatKlien.setText(mc.getAlamat());
                found = true;
                break;
            }
        }
        if (!found) {
            Toast.makeText(BarangKeluar.this, "Klien tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveStockData() {

        stockReference.orderByChild("enable").equalTo(true).addValueEventListener(new ValueEventListener() {
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
                ((ArrayAdapter) etNamaBarang.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarangKeluar.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveClientData() {
        clientReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namaClientList.clear();
                clientList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelClient mc = dataSnapshot.getValue(ModelClient.class);
                    if (mc != null) {
                        namaClientList.add(mc.getNama());
                        clientList.add(mc);
                    }
                }
                ((ArrayAdapter) etNamaKlien.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarangKeluar.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            gambarBarang = data.getData();
            ivGambar.setImageURI(gambarBarang);
        }
    }
}

