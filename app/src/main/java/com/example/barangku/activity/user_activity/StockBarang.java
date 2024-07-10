package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterStock;
import com.example.barangku.activity.model.ModelStock;
import com.example.barangku.activity.user_activity.utils.ItemClickStock;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StockBarang extends AppCompatActivity implements ItemClickStock {
private TextView tvToolbar, tvNamaBarang, tvJumlahItem, tvSatuan;
private TextView tvTotalItems;
private Spinner spinnerSort;
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
        tvTotalItems = findViewById(R.id.tv_total_items);

        spinnerSort = findViewById(R.id.spinner_sort);

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
                list_stock.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelStock ms = snapshot.getValue(ModelStock.class);
                    list_stock.add(ms);
                }
                pd.dismiss();
                adapterStock.notifyDataSetChanged();
                tvTotalItems.setText("Total Items: " + list_stock.size()); // Set total items

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StockBarang.this, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("A-Z")) {
                    sortListAZ();
                } else if (selectedItem.equals("Z-A")) {
                    sortListZA();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
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
                        R.array.Satuan,
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
                                .crop(16f, 9f)
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

                        int jumlahBarangInt;
                        try {
                            jumlahBarangInt = Integer.parseInt(jumlahbarang);
                        } catch (NumberFormatException e) {
                            etJumlahBarang.setError("Jumlah Barang harus berupa angka");
                            return;
                        }

                        if (jumlahBarangInt > 1000) {
                            etJumlahBarang.setError("Jumlah Barang tidak boleh lebih dari 1000");
                            return;
                        }

                        if (gambar == null) {
                            Toast.makeText(StockBarang.this, "Pilih Gambar", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        saveFirebase();
                    }
                });
            }
        });

    }

    private void sortListZA() {
        Collections.sort(list_stock, new Comparator<ModelStock>() {
            @Override
            public int compare(ModelStock o1, ModelStock o2) {
                return o2.getNamaBarang().compareToIgnoreCase(o1.getNamaBarang());
            }
        });
        adapterStock.notifyDataSetChanged();
    }

    private void sortListAZ() {
        Collections.sort(list_stock, new Comparator<ModelStock>() {
            @Override
            public int compare(ModelStock o1, ModelStock o2) {
                return o1.getNamaBarang().compareToIgnoreCase(o2.getNamaBarang());
            }
        });
        adapterStock.notifyDataSetChanged();
    }


    private void saveFirebase() {
        StorageReference imagereference = storageReference.child("StockBarang").child(String.valueOf(System.currentTimeMillis()));
        imagereference.putFile(gambar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String idBarang = generateIdBarang(namabarang);

                        ModelStock ms = new ModelStock(idBarang, namabarang, jumlahbarang, satuan, uri.toString(), true);
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
        int maxLength = 3;
        String prefix = namabarang.length() >= maxLength ? namabarang.substring(0, maxLength).toUpperCase() : namabarang.toUpperCase();

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String id = prefix.concat(timestamp.substring(timestamp.length() - (7 - maxLength)));
        return id;
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

        Button btnEnable = dialogStock.findViewById(R.id.btn_enable);
        Button btnDisable = dialogStock.findViewById(R.id.btn_disable);

        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> updateData = new HashMap<>();
                updateData.put("enable", true);
                reference.child(data.getId()).updateChildren(updateData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(StockBarang.this, "Status Berhasil Diubah Ke Enable", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(StockBarang.this, "Gagal Mengubah Status", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        btnDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> updateData = new HashMap<>();
                updateData.put("enable", false);
                reference.child(data.getId()).updateChildren(updateData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(StockBarang.this, "Status Berhasil Diubah Ke Disable", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(StockBarang.this, "Gagal Mengubah Status", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        dialogStock.findViewById(R.id.iv_close).setOnClickListener(view -> {dialogStock.dismiss();});


    }


}