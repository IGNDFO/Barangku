package com.example.barangku.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.barangku.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
private Button btnsignout,btnlaporan,btnadmin,btnbarngmasuk,btnbarangkeluar,btnclient,btnstock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnsignout=findViewById(R.id.btn_signout);
        btnclient=findViewById(R.id.btn_Client);
        btnadmin=findViewById(R.id.btn_Admin);
        btnbarangkeluar=findViewById(R.id.btn_Barang_Keluar);
        btnbarngmasuk=findViewById(R.id.btn_Barang_Masuk);
        btnstock=findViewById(R.id.btn_Stok_Barang);
        btnlaporan=findViewById(R.id.btn_Laporan);
        //client
        btnclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Client.class);
                startActivity(intent);

            }
        });
        //b.masuk
        btnbarngmasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Barang_Masuk.class);
                startActivity(intent);

            }
        });
        //b.keluar
        btnbarangkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Barang_Keluar.class);
                startActivity(intent);

            }
        });
        //admin
        btnadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Admin.class);
                startActivity(intent);

            }
        });
        //laporan
        btnlaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Laporan.class);
                startActivity(intent);

            }
        });
        //stock
        btnstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Stock_Barang.class);
                startActivity(intent);
            }
        });


        //sign out
        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


    }
}