package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
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
    private String namaBarang, satuan, keterangan, tanggalKeluar, namaKlien, alamatKlien, userRole;
    private long jumlahKeluar = 0;
    private DatabaseReference stockReference, barangKeluarRef, clientReference, userRef;
    private List<String> namaBarangList, namaClientList;
    private List<ModelStock> stockList;
    private List<ModelClient> clientList;
    private ModelStock currentStockItem;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_keluar);


        initViews();
        initFirebaseReferences();
        setInitialDate();
        setAdapters();
        retrieveStockData();
        retrieveClientData();
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        checkUserRole(currentUserId); // Ganti dengan user ID yang sesuai

        ivback.setOnClickListener(v -> navigateToMainActivity());
        btnCari.setOnClickListener(v -> searchBarang());
        btnSimpan.setOnClickListener(v -> saveBarangKeluar());
    }

    private void initViews() {
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

        btnCari = findViewById(R.id.btn_cari_barang);
        btnSimpan = findViewById(R.id.btn_simpan);

        namaBarangList = new ArrayList<>();
        namaClientList = new ArrayList<>();
        stockList = new ArrayList<>();
        clientList = new ArrayList<>();
        // Set filter to limit keterangan to 255 characters
        etKeterangan.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});

    }

    private void initFirebaseReferences() {
        stockReference = FirebaseDatabase.getInstance().getReference("StockBarang");
        barangKeluarRef = FirebaseDatabase.getInstance().getReference("BarangKeluar");
        clientReference = FirebaseDatabase.getInstance().getReference("Client");
        userRef = FirebaseDatabase.getInstance().getReference("User");
    }

    private void setInitialDate() {
        LocalDate localDate = LocalDate.now();
        tanggalKeluar = String.format("%04d-%02d-%02d", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        tvTanggalKeluar.setText(tanggalKeluar);
    }

    private void setAdapters() {
        ArrayAdapter<String> adapterBarang = new ArrayAdapter<>(this, R.layout.list_nama_barang, namaBarangList);
        etNamaBarang.setAdapter(adapterBarang);
        etNamaBarang.setOnItemClickListener((parent, view, position, id) -> updateSelectedBarang(parent.getItemAtPosition(position).toString()));

        ArrayAdapter<String> adapterClient = new ArrayAdapter<>(this, R.layout.list_nama_klien, namaClientList);
        etNamaKlien.setAdapter(adapterClient);
        etNamaKlien.setOnItemClickListener((parent, view, position, id) -> updateSelectedClient(parent.getItemAtPosition(position).toString()));
    }

    private void updateSelectedBarang(String selectedBarang) {
        Toast.makeText(this, selectedBarang, Toast.LENGTH_SHORT).show();
        cariBarang(selectedBarang);
    }

    private void updateSelectedClient(String selectedClient) {
        for (ModelClient client : clientList) {
            if (client.getNama().equals(selectedClient)) {
                tvAlamatKlien.setText(client.getAlamat());
                break;
            }
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void searchBarang() {
        String searchNamaBarang = etNamaBarang.getText().toString();
        String searchNamaClient = etNamaKlien.getText().toString();
        if (!searchNamaBarang.isEmpty() && !searchNamaClient.isEmpty()) {
            cariBarang(searchNamaBarang);
            cariKlien(searchNamaClient);
        } else {
            Toast.makeText(this, "Masukkan nama barang dan klien", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBarangKeluar() {
        if (validateFields()) {
            namaBarang = tvNamaBarang.getText().toString();
            namaKlien = etNamaKlien.getText().toString();
            keterangan = etKeterangan.getText().toString();
            try {
                jumlahKeluar = Long.parseLong(etJumlah.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Jumlah harus berupa angka", Toast.LENGTH_SHORT).show();
                return;
            }
            satuan = tvSatuan.getText().toString();
            alamatKlien = tvAlamatKlien.getText().toString();
            gambarBarang = ivGambar.getDrawable() != null ? gambarBarang : Uri.EMPTY;

            if (currentStockItem != null) {
                long stokTersedia = Long.parseLong(currentStockItem.getJumlahBarang());
                if (jumlahKeluar > stokTersedia) {
                    Toast.makeText(this, "Jumlah barang keluar melebihi stok yang tersedia!!", Toast.LENGTH_SHORT).show();
                } else {
                    simpanBarangKeluar();
                }
            } else {
                Toast.makeText(this, "Barang tidak ditemukan!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Pastikan semua data terisi dengan benar", Toast.LENGTH_SHORT).show();
        }
        
    }

    private boolean validateFields() {
        if (tvNamaBarang.getText().toString().trim().isEmpty() ||
                etNamaKlien.getText().toString().trim().isEmpty() ||
                etKeterangan.getText().toString().trim().isEmpty() ||
                etJumlah.getText().toString().trim().isEmpty() ||
                tvSatuan.getText().toString().trim().isEmpty() ||
                tvAlamatKlien.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void simpanBarangKeluar() {
        String id = barangKeluarRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Gagal mendapatkan ID unik", Toast.LENGTH_SHORT).show();
            return;
        }

        ModelBarangKeluar bk = new ModelBarangKeluar(id, namaBarang, satuan, keterangan, tanggalKeluar, namaKlien, alamatKlien, jumlahKeluar);

        if ("Admin".equals(userRole)) {
            barangKeluarRef.child(id).setValue(bk).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateStockBarang(currentStockItem, jumlahKeluar);
                    Toast.makeText(BarangKeluar.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    resetFields();
                } else {
                    Toast.makeText(BarangKeluar.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User sends a request for approval
            kirimPengajuanKeAdmin(bk);
        }
    }

    private void kirimPengajuanKeAdmin(ModelBarangKeluar bk) {
        DatabaseReference pengajuanRef = FirebaseDatabase.getInstance().getReference("PengajuanBarangKeluar");
        String pengajuanId = pengajuanRef.push().getKey();
        if (pengajuanId != null) {
            pengajuanRef.child(pengajuanId).setValue(bk).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Pengajuan barang keluar berhasil dikirim", Toast.LENGTH_SHORT).show();
                    resetFields();
                } else {
                    Toast.makeText(this, "Pengajuan barang keluar gagal dikirim", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Gagal membuat pengajuan", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStockBarang(ModelStock stock, long jumlahKeluar) {
        long stokBaru = Long.parseLong(stock.getJumlahBarang()) - jumlahKeluar;
        stockReference.child(stock.getId()).child("jumlahBarang").setValue(String.valueOf(stokBaru));
    }

    private void resetFields() {
        etNamaBarang.setText("");
        etNamaKlien.setText("");
        etJumlah.setText("");
        etKeterangan.setText("");
        tvNamaBarang.setText("");
        tvSatuan.setText("");
        tvJumlahItem.setText("");
        tvAlamatKlien.setText("");
        ivGambar.setImageResource(R.drawable.insert_image);
        currentStockItem = null;
    }

    private void retrieveStockData() {
        stockReference.orderByChild("enable").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namaBarangList.clear();
                stockList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelStock stock = dataSnapshot.getValue(ModelStock.class);
                    if (stock != null) {
                        namaBarangList.add(stock.getNamaBarang());
                        stockList.add(stock);
                    }
                }
                setAdapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BarangKeluar", "Gagal mengambil data stok: " + error.getMessage());
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
                    ModelClient client = dataSnapshot.getValue(ModelClient.class);
                    if (client != null) {
                        namaClientList.add(client.getNama());
                        clientList.add(client);
                    }
                }
                setAdapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BarangKeluar", "Gagal mengambil data klien: " + error.getMessage());
            }
        });
    }

    private void retrieveUserRole() {
        // Assume you have a way to get the user role, e.g., from Firebase Authentication or Realtime Database
        // Here is an example using Firebase Realtime Database
        DatabaseReference userRoleRef = FirebaseDatabase.getInstance().getReference("UserRole");
        String userId = "currentUserId"; // Replace with the actual current user ID
        userRoleRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userRole = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BarangKeluar", "Gagal mendapatkan peran pengguna: " + error.getMessage());
            }
        });
    }

    private void cariBarang(String namaBarang) {
        for (ModelStock stock : stockList) {
            if (stock.getNamaBarang().equals(namaBarang)) {
                tvNamaBarang.setText(stock.getNamaBarang());
                tvJumlahItem.setText(stock.getJumlahBarang());
                tvSatuan.setText(stock.getSatuan());
                currentStockItem = stock;
                loadImage(stock.getGambar());
                break;
            }
        }
    }

    private void cariKlien(String namaKlien) {
        for (ModelClient client : clientList) {
            if (client.getNama().equals(namaKlien)) {
                tvAlamatKlien.setText(client.getAlamat());
                break;
            }
        }
    }

    private void loadImage(String imageUrl) {
        if (!imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(ivGambar);
        } else {
            ivGambar.setImageResource(R.drawable.insert_image);
        }
    }

    private void checkUserRole(String userId) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userRole = snapshot.child("jabatan").getValue(String.class);
                    updateUIBasedOnRole();
                } else {
                    Toast.makeText(BarangKeluar.this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarangKeluar.this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIBasedOnRole() {
        if ("Admin".equals(userRole)) {
            // Admin can see all functionalities
        } else {
            // User (non-admin) has limited functionalities
            ivKalender.setVisibility(View.GONE); // Hide the calendar icon for users
        }
    }
}
