package com.example.barangku.activity.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
    @Override
    public void onBackPressed() {
        // Check if user is on the first screen of the app (MainActivity)
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // Display a dialog asking if the user wants to exit the app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit the app?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Close the app
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do nothing and keep the app open
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        // If the user is not on the first screen, just close the current activity
        else {
            finish();
        }
    }
}