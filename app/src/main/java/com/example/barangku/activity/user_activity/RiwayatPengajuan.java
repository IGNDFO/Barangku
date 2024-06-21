package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.barangku.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RiwayatPengajuan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_pengajuan);
        gantiFragment(new RiwayatPengajuanBarangMasukFragment());
        BottomNavigationView navigationView = findViewById(R.id.navigations_bar);
        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_barang_masuk){
                gantiFragment(new RiwayatPengajuanBarangMasukFragment());
            }else if(item.getItemId() == R.id.nav_barang_keluar){
                gantiFragment(new RiwayatPengajuanBarangKeluarFragment());
            }
            return true;
        });
    }
    private void gantiFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }
}