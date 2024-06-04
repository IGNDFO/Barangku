package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.barangku.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    GridLayout menuUser, menuAdmin;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

    private Button btnSignout, btnBarangMasukUser, btnBarangKeluarUser;
    private Button btnBarangMasukAdmin, btnBarangKeluarAdmin, btnLaporanAdmin, btnClientAdmin, btnStockAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuUser = findViewById(R.id.menu_user);
        menuAdmin = findViewById(R.id.menu_admin);

        btnBarangMasukUser = findViewById(R.id.btn_Barang_Masuk_User);
        btnBarangKeluarUser = findViewById(R.id.btn_Barang_Keluar_User);

        btnBarangMasukAdmin = findViewById(R.id.btn_Barang_Masuk_admin);
        btnBarangKeluarAdmin = findViewById(R.id.btn_Barang_Keluar_Admin);
        btnClientAdmin = findViewById(R.id.btn_Client_Admin);
        btnLaporanAdmin = findViewById(R.id.btn_Laporan_Admin);
        btnStockAdmin = findViewById(R.id.btn_Stok_Barang_Admin);

        btnSignout = findViewById(R.id.btn_Signout);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                        String jabatan = userSnapshot.child("jabatan").getValue(String.class);
                        if (jabatan.equals("Admin")) {
                            menuAdmin.setVisibility(View.VISIBLE);
                            menuUser.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Anda Login Sebagai Admin", Toast.LENGTH_SHORT).show();
                        }
//                        else if (jabatan.equals("Karyawan")) {
////                            menuUser.setVisibility(View.VISIBLE);
////                            menuAdmin.setVisibility(View.GONE);
////                            Toast.makeText(MainActivity.this, "Anda Login Sebagai User", Toast.LENGTH_SHORT).show();
//                        }
                        else {
                            menuUser.setVisibility(View.VISIBLE);
                            menuAdmin.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Anda Login Sebagai Karyawan", Toast.LENGTH_SHORT).show();
                        }
                        break; // Exit the loop once the user is found
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        btnBarangMasukUser.setOnClickListener(v -> pindahHalaman(MainActivity.this, BarangMasuk.class));
        btnBarangKeluarUser.setOnClickListener(v -> pindahHalaman(MainActivity.this, BarangKeluar.class));

        btnBarangMasukAdmin.setOnClickListener(v -> pindahHalaman(MainActivity.this, BarangMasuk.class));
        btnBarangKeluarAdmin.setOnClickListener(v -> pindahHalaman(MainActivity.this, BarangKeluar.class));
        btnClientAdmin.setOnClickListener(v -> pindahHalaman(MainActivity.this, Client.class));
        btnLaporanAdmin.setOnClickListener(v -> pindahHalaman(MainActivity.this, Laporan.class));
        btnStockAdmin.setOnClickListener(v -> pindahHalaman(MainActivity.this, StockBarang.class));

        btnSignout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void pindahHalaman(Activity activityawal, Class activitytujuan) {
        Intent intent = new Intent(activityawal, activitytujuan);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit the app?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .create()
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}
