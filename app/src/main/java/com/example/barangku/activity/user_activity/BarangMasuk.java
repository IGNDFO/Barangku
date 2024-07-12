package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BarangMasuk extends AppCompatActivity {
    private TextView tvToolbar, tvTanggalMasuk, tvNamaBarang, tvSatuan, tvJumlahItem;
    private AutoCompleteTextView etNamaBarang;
    private EditText etJumlah, etKeterangan;
    private Button btnSimpan;
    private ImageView ivback, ivKalender, ivGambar;
    private Uri gambarBarang;
    private String simpan, namaBarang, satuan, keterangan, tanggalMasuk;
    private int jumlahMasuk = 0;
    private String userRole;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StockBarang");
    private DatabaseReference barangMasukref = FirebaseDatabase.getInstance().getReference("BarangMasuk");
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
    private List<String> namaBarangList = new ArrayList<>();
    private List<ModelStock> stockList = new ArrayList<>();

    private ModelStock currentStockItem;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_masuk);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Barang Masuk");

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

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        LocalDate localDate = LocalDate.now();
        int yearToday = localDate.getYear();
        int monthToday = localDate.getMonthValue();
        int dayOfMonthToday = localDate.getDayOfMonth();

        tanggalMasuk = String.format("%04d-%02d-%02d", yearToday, monthToday, dayOfMonthToday);
        tvTanggalMasuk.setText((tanggalMasuk));

        btnSimpan = findViewById(R.id.btn_simpan);

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangMasuk.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set filter to limit input to 4 digits
        etJumlah.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4), new InputFilterMinMax(1, 9999)});

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
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
                        resetFields();
                    }
                }
            }
        });

        etNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchNamaBarang = parent.getItemAtPosition(position).toString();
                cariBarang(searchNamaBarang);
            }
        });

        retrieveStockData();
        checkUserRole(currentUserId);
    } // Akhir On Create

    private boolean validateFields() {
        if (etNamaBarang.getText().toString().trim().isEmpty()) {
            etNamaBarang.setError("Nama barang harus diisi");
            return false;
        }
        if (etJumlah.getText().toString().trim().isEmpty()) {
            etJumlah.setError("Jumlah barang harus diisi");
            return false;
        }
        if (tvNamaBarang.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Nama barang tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvSatuan.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Satuan barang tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void resetFields() {
        etKeterangan.setText("");
        etNamaBarang.setText("");
        etJumlah.setText("");
        tvSatuan.setText("");
        tvJumlahItem.setText("");
        ivGambar.setImageDrawable(getResources().getDrawable(R.drawable.insert_image));
    }

    private void checkUserRole(String userId) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userRole = snapshot.child("jabatan").getValue(String.class);
                    updateUIBasedOnRole();
                } else {
                    Toast.makeText(BarangMasuk.this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarangMasuk.this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
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

    private void simpanBarangMasuk() {
        String id = barangMasukref.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Gagal mendapatkan ID unik", Toast.LENGTH_SHORT).show();
            return;
        }

        ModelBarangMasuk bm = new ModelBarangMasuk(id, namaBarang, satuan, keterangan, tanggalMasuk, jumlahMasuk);

        if ("Admin".equals(userRole)) {
            // Admin saves directly
            barangMasukref.child(id).setValue(bm).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateStockBarang(currentStockItem, jumlahMasuk);
                    Toast.makeText(BarangMasuk.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    resetFields();
                } else {
                    Toast.makeText(BarangMasuk.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User sends a request for approval
            kirimPengajuanKeAdmin(bm);
        }
    }

    private void kirimPengajuanKeAdmin(ModelBarangMasuk bm) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("PengajuanBarangMasuk");
        String notifId = notifRef.push().getKey();
        if (notifId == null) {
            Toast.makeText(this, "Gagal mengirim pengajuan", Toast.LENGTH_SHORT).show();
            return;
        }
        notifRef.child(notifId).setValue(bm).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BarangMasuk.this, "Pengajuan dikirim ke admin", Toast.LENGTH_SHORT).show();
                resetFields();
            } else {
                Toast.makeText(BarangMasuk.this, "Gagal mengirim pengajuan ke admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStockBarang(ModelStock stockItem, int jumlahMasuk) {
        int jumlahBaru = Integer.parseInt(String.valueOf(Integer.parseInt(stockItem.getJumlahBarang()) + jumlahMasuk));
        stockItem.setJumlahBarang(String.valueOf(jumlahBaru));

        reference.child(stockItem.getId()).setValue(stockItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BarangMasuk.this, "Stok barang berhasil diperbarui", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            gambarBarang = data.getData();
            ivGambar.setImageURI(gambarBarang);
        }
    }

    private void setupAutoCompleteTextView() {
        ArrayAdapter<String> adapterBarang = new ArrayAdapter<>(this, R.layout.list_nama_barang, namaBarangList);
        etNamaBarang.setAdapter(adapterBarang);
        etNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BarangMasuk.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                cariBarang(parent.getItemAtPosition(position).toString());
            }
        });
    }

    // InputFilterMinMax class to limit input range
    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
