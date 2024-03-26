package com.example.barangku.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.barangku.R;

public class Stock_Barang extends AppCompatActivity {
private TextView tv_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_barang);
        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Stock Barang");
    }
}