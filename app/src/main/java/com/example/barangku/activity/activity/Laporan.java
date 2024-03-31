package com.example.barangku.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barangku.R;

public class Laporan extends AppCompatActivity {
private TextView tv_toolbar;
private ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Laporan");
        ivback.findViewById(R.id.iv_back);

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Laporan.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}