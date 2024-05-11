package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterClient;
import com.example.barangku.activity.model.ModelClient;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Client extends AppCompatActivity implements ItemClickListener {
    private TextView tvToolbar;
    private Dialog dialog;
    private FloatingActionButton fabTambah;
    private String namaKlien, alamatKlien, noTelpKlien, emailKlien, catatanKlien;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Client");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private SearchView searchView;
    private ImageView ivback;
    private ProgressDialog pd;
    RecyclerView rv_client;
    AdapterClient adapterClient;
    private List<ModelClient> list_client = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Client");

        fabTambah = findViewById(R.id.fab_tambah_klien);
        searchView = findViewById(R.id.search);
        rv_client = findViewById(R.id.rv_client);

        ivback = findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Client.this, MainActivity.class);
                startActivity(intent);
            }
        });
        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();


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


        adapterClient = new AdapterClient(Client.this, list_client);
        adapterClient.setItemClickListener(this);
        LinearLayoutManager lm_client = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_client.setLayoutManager(lm_client);
        rv_client.setAdapter(adapterClient);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Client");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelClient mc = snapshot.getValue(ModelClient.class);
                    list_client.add(mc);
                }
                adapterClient.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        fabTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(Client.this);
                dialog.setContentView(R.layout.dialog_tambah_client);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.show();

                EditText etNamaKlien = dialog.findViewById(R.id.et_nama_klien);
                EditText etAlamatKlien = dialog.findViewById(R.id.et_alamat_klien);
                EditText etNoTelpKlien = dialog.findViewById(R.id.et_telp_klien);
                EditText etEmailKlien = dialog.findViewById(R.id.et_email_klien);
                EditText etCatatanKlien = dialog.findViewById(R.id.et_catatan_klien);


                dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        namaKlien = etNamaKlien.getText().toString();
                        alamatKlien = etAlamatKlien.getText().toString();
                        noTelpKlien = etNoTelpKlien.getText().toString();
                        emailKlien = etEmailKlien.getText().toString();
                        catatanKlien = etCatatanKlien.getText().toString();

                        if (namaKlien.trim().isEmpty()) {
                            etNamaKlien.setError("Required");
                            return;
                        } else if (alamatKlien.trim().isEmpty()) {
                            etAlamatKlien.setError("Required");
                            return;
                        } else if (noTelpKlien.trim().isEmpty()) {
                            etNoTelpKlien.setError("Required");
                            return;
                        } else if (noTelpKlien.length() > 13) {
                            etNoTelpKlien.setError("Nomor Telpon Tidak Boleh Melebihi 13 digit");
                            return;
                        } else if (emailKlien.trim().isEmpty()) {
                            etEmailKlien.setError("Required");
                            return;
                        }
                        else if (!isValidEmail(emailKlien)){
                            etEmailKlien.setError("Format Email Tidak Didukung");
                            return;
                        }
                         else if (catatanKlien.trim().isEmpty()) {
                            etCatatanKlien.setError("Required");
                            return;
                        } else {
                            ModelClient mc = new ModelClient(namaKlien, alamatKlien, noTelpKlien, emailKlien, catatanKlien);
                            reference.child(namaKlien).setValue(mc);
                            Toast.makeText(Client.this, "Nama Klien Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Clear the list again to ensure only new data is fetched
                                    list_client.clear();

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ModelClient mc = snapshot.getValue(ModelClient.class);
                                        list_client.add(mc);
                                    }
                                    adapterClient.notifyDataSetChanged();
                                    pd.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database errors (optional)
                                }
                            });

                        }

                    }
                });
            }
        });
    }

    private boolean isValidEmail(String emailKlien) {
        String regex = "^[\\w-_\\.+]+@[\\w-]+\\.[\\w-]{2,}$";
        return Pattern.matches(regex, emailKlien);
    }


    private void filterList(String query) {
        List<ModelClient> filteredList = new ArrayList<>();
        String queryInLower = query.toLowerCase();
        for (ModelClient mc : list_client) {
            if (mc.getNama().toLowerCase().contains(queryInLower)) {
                filteredList.add(mc);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No clients found", Toast.LENGTH_SHORT).show();
        } else {
            adapterClient.setFilter(filteredList);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClickListener(ModelClient data, int position) {

    }
}


