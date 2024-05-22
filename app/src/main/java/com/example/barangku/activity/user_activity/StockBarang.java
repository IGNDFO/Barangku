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

import com.bumptech.glide.Glide;
import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterStock;
import com.example.barangku.activity.model.ModelClient;
import com.example.barangku.activity.model.ModelStock;
import com.example.barangku.activity.user_activity.utils.ItemClickStock;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockBarang extends AppCompatActivity implements ItemClickStock {
private TextView tvToolbar, tvNamaBarang, tvJumlahItem, tvSatuan;
private Dialog dialog, dialogStock, dialogEdit;
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

        tvToolbar =findViewById(R.id.tv_judul);
        tvToolbar.setText("Stock Barang");
        searchView = findViewById(R.id.search);

        tvNamaBarang = findViewById(R.id.tv_nama_barang);
        tvJumlahItem = findViewById(R.id.tv_jumlah_item);
        tvSatuan = findViewById(R.id.tv_satuan);

        ivback=findViewById(R.id.iv_back);
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

        adapterStock = new AdapterStock(StockBarang.this, list_stock);
        adapterStock.setItemCLickStock(this);
        LinearLayoutManager lmStock = new  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvStock.setLayoutManager(lmStock);
        rvStock.setAdapter(adapterStock);//

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelStock ms = snapshot.getValue(ModelStock.class);
                    list_stock.add(ms);
                }
                pd.dismiss();
                adapterStock.notifyDataSetChanged();//
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StockBarang.this, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
        fabTambah = findViewById(R.id.fab_tambah_barang);
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
                    }});

                dialog.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        namabarang = etNamaBarang.getText().toString();
                        jumlahbarang = etJumlahBarang.getText().toString();

                        if (namabarang.trim().isEmpty()) {
                            etNamaBarang.setError("Required");
                            return;
                        }
                        if (jumlahbarang.trim().isEmpty()) {
                            etJumlahBarang.setError("Required");
                            return;
                        }
                        if (gambar == null) {
                            Toast.makeText(StockBarang.this, "Pilih Gambar", Toast.LENGTH_SHORT).show();
                            return;
                        }//
                        else {
                        saveFirebase();
                        }
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
//                        String uniqueId = reference.getKey();
//                        ModelStock ms = new ModelStock(uniqueId,namabarang, jumlahbarang, satuan, uri.toString());
//                        reference.child(uniqueId).setValue(ms);

//                        Generate ID Barang secara otomatis di Firebaase
//                        DatabaseReference newReference = reference.push();
//                        String id = newReference.getKey();
//                        ModelStock ms = new ModelStock(id, namabarang, jumlahbarang, satuan, uri.toString());
//                        newReference.setValue(ms);

                        String idBarang = generateIdBarang(namabarang);

                        ModelStock ms = new ModelStock(idBarang, namabarang, jumlahbarang, satuan, uri.toString());
                        reference.child(idBarang).setValue(ms);

                        Toast.makeText(StockBarang.this, "Stock Barang Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        refreshStockList();
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

    private String generateIdBarang(String namabarang) {
        String prefix = namabarang.length() >= 3 ? namabarang.substring(0, 3).toUpperCase() : "XXX";

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return prefix + timestamp;
    }

    private void refreshStockList() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list again to ensure only new data is fetched
                list_stock.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelStock ms = snapshot.getValue(ModelStock.class);
                    list_stock.add(ms);

                }
                adapterStock.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors (optional)
                Toast.makeText(StockBarang.this, "Data Gagal Di Simpan", Toast.LENGTH_SHORT).show();
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
            adapterStock.setFilter(filteredList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gambar = data.getData();
        ivStockBarang.setImageURI(gambar);
    }

    @Override
    public void onItemClickListener(ModelStock data, int position) {
        dialogStock = new Dialog(StockBarang.this);
        dialogStock.setContentView(R.layout.dialog_tampil_stock);
        dialogStock.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogStock.setCancelable(false);
        dialogStock.show();

        ImageView ivStockBarang = dialogStock.findViewById(R.id.iv_foto_barang);
        Glide.with(StockBarang.this)
                .load(data.getGambar())
                .into(ivStockBarang);

        TextView tvNamaBarang = dialogStock.findViewById(R.id.tv_nama_barang);
        tvNamaBarang.setText(data.getNamaBarang());
        TextView tvJumlahItem = dialogStock.findViewById(R.id.tv_jumlah_item);
        tvJumlahItem.setText(data.getJumlahBarang());
        TextView tvSatuan = dialogStock.findViewById(R.id.tv_satuan);
        tvSatuan.setText(data.getSatuan());
        dialogStock.findViewById(R.id.iv_close).setOnClickListener(view -> {dialogStock.dismiss();});

        dialogStock.findViewById(R.id.btn_hapus_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idBarang = data.getId();
                reference.child(idBarang).removeValue();
                //perlu menghapus value gambar yang ada di storage! nanti saja -_-
                Toast.makeText(StockBarang.this, "Data Barang Telah di Hapus", Toast.LENGTH_SHORT).show();
                refreshStockList();
                dialogStock.dismiss();
            }
        });
    }


}