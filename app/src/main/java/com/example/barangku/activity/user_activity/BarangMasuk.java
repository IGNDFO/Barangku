package com.example.barangku.activity.user_activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.barangku.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.time.LocalDate;

public class BarangMasuk extends AppCompatActivity {
    private TextView tv_toolbar, tvTanggalMasuk;
    private EditText etNama, etKeterangan, etJumlah;
    private Button btnSimpan;
    private ImageView ivback, ivKalender, ivGambar;
    private Uri gambarBarang;
    private String simpan, nama, keterangan, jumlah, tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_masuk);

        ivback=findViewById(R.id.iv_back);
        etNama = findViewById(R.id.et_nama_barang);
        etJumlah = findViewById(R.id.et_jumlah_barang);
        etKeterangan = findViewById(R.id.et_keterangan);

        btnSimpan = findViewById(R.id.btn_simpan);


        tv_toolbar=findViewById(R.id.tv_judul);
        tv_toolbar.setText("Barang Masuk");

        tvTanggalMasuk = findViewById(R.id.tv_tanggal_masuk);
        ivKalender = findViewById(R.id.iv_kalender);
        ivGambar = findViewById(R.id.iv_gambar);

    /**   ivKalender.setOnClickListener(new View.OnClickListener() {
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
        });**/

        LocalDate localDate = LocalDate.now();
        int yearToday = localDate.getYear();
        int monthToday = localDate.getMonthValue();
        int dayOfMonthToday = localDate.getDayOfMonth();

        tanggal = String.format("%04d-%02d-%02d", yearToday, monthToday, dayOfMonthToday);
        tvTanggalMasuk.setText((tanggal));



        Spinner sp = (Spinner) findViewById(R.id.sp_satuan);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Pilih,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                simpan = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangMasuk.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ivGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(BarangMasuk.this)
                        .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                        .crop()
                        .compress(256)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nama = etNama.getText().toString();
                keterangan = etKeterangan.getText().toString();
                jumlah = etJumlah.getText().toString();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gambarBarang = data.getData();
        ivGambar.setImageURI(gambarBarang);
    }
}