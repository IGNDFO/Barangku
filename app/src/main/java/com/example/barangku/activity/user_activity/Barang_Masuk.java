package com.example.barangku.activity.user_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Barang_Masuk extends AppCompatActivity {
    private TextView tv_toolbar, tvTanggalMasuk;
    private ImageView ivback, ivKalender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_masuk);

        ivback=findViewById(R.id.iv_back);

        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Barang Masuk");

        tvTanggalMasuk = findViewById(R.id.tv_tanggal_masuk);
        ivKalender = findViewById(R.id.iv_kalender);

        ivKalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Barang_Masuk.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(year, month, dayOfMonth);

                                String formatTanggal = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(selectedCalendar.getTime());
                                tvTanggalMasuk.setText(formatTanggal);

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

//        Spinner sp = (Spinner) findViewById(R.id.sp_satuan);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.Pilih,
//                R.layout.activity_barang_masuk
//        );
//
//        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
//        sp.setAdapter(adapter);

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Barang_Masuk.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}