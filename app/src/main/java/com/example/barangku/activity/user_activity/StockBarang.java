package com.example.barangku.activity.user_activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterStock;
import com.example.barangku.activity.model.ModelClient;
import com.example.barangku.activity.model.ModelStock;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class StockBarang extends AppCompatActivity {
private TextView tvToolbar, tvNamaBarang, tvJumlahItem, tvSatuan;
private Dialog dialog;
private ImageView ivStockBarang;
private String namabarang, jumlahbarang, satuan;
private Uri  gambar ;
private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StockBarang");
private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
private FloatingActionButton fabTambah;
private ProgressDialog pd;
private SearchView searchView;
RecyclerView rvStock;
private ImageView ivback;
private List<ModelStock> list_stock = new ArrayList<>();
private AdapterStock adapterStock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_barang);

        searchView = findViewById(R.id.search);
        tvToolbar =findViewById(R.id.tv_judul);
        tvNamaBarang = findViewById(R.id.tv_nama_barang);
        tvJumlahItem = findViewById(R.id.tv_jumlah_item);
        tvSatuan = findViewById(R.id.tv_satuan);
        ivback=findViewById(R.id.iv_back);

        fabTambah = findViewById(R.id.fab_tambah_barang);


        tvToolbar.setText("Stock Barang");
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockBarang.this, MainActivity.class);
                startActivity(intent);
            }
        });
        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        rvStock = findViewById(R.id.rv_stock);

        adapterStock = new AdapterStock(StockBarang.this, list_stock);
        LinearLayoutManager lmStock = new  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvStock.setLayoutManager(lmStock);
        rvStock.setAdapter(adapterStock);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelStock ms = snapshot.getValue(ModelStock.class);
                    list_stock.add(ms);
                }

                pd.dismiss();
                adapterStock.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        fabTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(StockBarang.this);
                dialog.setContentView(R.layout.dialog_tambah_stock);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.show();
                EditText etNamaBarang = dialog.findViewById(R.id.et_nama_barang);
                EditText etJumlahBarang = dialog.findViewById(R.id.et_jumlah_barang);
                ivStockBarang = dialog.findViewById(R.id.iv_stock_barang);

                Spinner sp = dialog.findViewById(R.id.sp_satuan);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        StockBarang.this,
                        R.array.Pilih,
                        android.R.layout.simple_spinner_item
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adapter);

                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        satuan = adapterView.getItemAtPosition(pos).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                ivStockBarang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.with(StockBarang.this)
                                .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                                .crop()
                                .compress(256)
                                .maxResultSize(1080, 1080)
                                .start();
                    }
                });

                dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        namabarang= etNamaBarang.getText().toString();
                        jumlahbarang= etJumlahBarang.getText().toString();

                        if(namabarang.trim().isEmpty()){
                            etNamaBarang.setError("Required");
                            return;
                        }
                        if(jumlahbarang.trim().isEmpty()){
                            etJumlahBarang.setError("Required");
                            return;
                        }
                        if(gambar == null){
                            Toast.makeText(StockBarang.this, "Pilih Gambar", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveFirebase();
                    }
                });

            }
        });
    }

    private void saveFirebase() {
        StorageReference imagereference = storageReference.child("StockBarang").child(String.valueOf(System.currentTimeMillis()));
        imagereference.putFile(gambar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ModelStock ms = new ModelStock(namabarang, jumlahbarang, satuan, uri.toString());
                        reference.child(namabarang).setValue(ms);
                        Toast.makeText(StockBarang.this, "Stock Barang Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StockBarang.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void filterList(String query) {
        List<ModelStock> filteredList = new ArrayList<>();
        String queryInLower = query.toLowerCase();
        for (ModelStock ms : list_stock) {
            if (ms.getNamaBarang().toLowerCase().contains(queryInLower)) {
                filteredList.add(ms);
            } else if (ms.getJumlahBarang().toLowerCase().contains(queryInLower)) {
                filteredList.add(ms);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Tidak Ada Stock Yang Tersedia", Toast.LENGTH_SHORT).show();
        } else {
            //adapterStokbarang
            adapterStock.setFilter(filteredList);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gambar = data.getData();
        ivStockBarang.setImageURI(gambar);


    }
}