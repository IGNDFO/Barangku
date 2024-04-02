package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;

public class Barang_Masuk extends AppCompatActivity {
    private TextView tv_toolbar;
    private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_masuk);

        ivback=findViewById(R.id.iv_back);
        tv_toolbar=findViewById(R.id.tv_judul);

        tv_toolbar.setText("Barang Masuk");

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Barang_Masuk.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}