package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.barangku.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Pengajuan extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan);
        gantiFragment(new PengajuanBarangMasukFragment());
        BottomNavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_barang_masuk){
                gantiFragment(new PengajuanBarangMasukFragment());
            }else if(item.getItemId() == R.id.nav_barang_keluar){
                gantiFragment(new PengajuanBarangKeluarFragment());
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