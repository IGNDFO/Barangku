package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;

public class StockBarang extends AppCompatActivity {
private TextView tvToolbar;
private ProgressDialog pd;
RecyclerView rvStock;
private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_barang);
        tvToolbar =findViewById(R.id.tv_judul);
        ivback=findViewById(R.id.iv_back);
        tvToolbar.setText("Stock Barang");
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockBarang.this, MainActivity.class);
                startActivity(intent);
            }
        });
        rvStock = findViewById(R.id.rv_stock);

        adapterStock =new AdapterStock(StockBarang.this, list_stock);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

    }
}