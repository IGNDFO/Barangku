package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.barangku.activity.user_activity.utils.ItemClickClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class Client extends AppCompatActivity implements ItemClickClient {
    private TextView tvToolbar;
    private Dialog dialog, dialogClient, dialogEdit;
    private FloatingActionButton fabTambah;
    private String namaKlien, alamatKlien, noTelpKlien, emailKlien, catatanKlien;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Client");
    DatabaseReference counterRef = FirebaseDatabase.getInstance().getReference().child("counter").child("clientId");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private SearchView searchView;
    private ImageView ivback;
    private ProgressDialog pd;
    RecyclerView rvClient;
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
        rvClient = findViewById(R.id.rv_client);

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
        rvClient.setLayoutManager(lm_client);
        rvClient.setAdapter(adapterClient);

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
                            TambahClient(namaKlien, alamatKlien,noTelpKlien, emailKlien, catatanKlien);
                        }

                    }
                });
            }
        });
    }

    private void TambahClient(String namaKlien, String alamatKlien, String noTelpKlien, String emailKlien, String catatanKlien) {
        counterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currentId = dataSnapshot.exists() ? dataSnapshot.getValue(Long.class) : 0;
                long newId = currentId + 1;

                String clientId = String.valueOf(newId);
                ModelClient mc = new ModelClient(clientId, namaKlien, alamatKlien, noTelpKlien, emailKlien, catatanKlien);
                reference.child(clientId).setValue(mc);
                Toast.makeText(Client.this, "Nama Klien Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                counterRef.setValue(newId);
                refreshClientList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refreshClientList() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            Toast.makeText(this, "No Clients Found", Toast.LENGTH_SHORT).show();
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
        dialogClient = new Dialog(Client.this);
        dialogClient.setContentView(R.layout.dialog_tampil_client);
        dialogClient.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogClient.setCancelable(false);
        dialogClient.show();

        TextView tvNamaKlien = dialogClient.findViewById(R.id.tv_nama_klien);
        tvNamaKlien.setText(data.getNama());
        TextView tvAlamatKlien = dialogClient.findViewById(R.id.tv_alamat_klien);
        tvAlamatKlien.setText(data.getAlamat());
        TextView tvTelpKlien = dialogClient.findViewById(R.id.tv_telp_klien);
        tvTelpKlien.setText(data.getNo_telp());
        TextView tvEmailKlien = dialogClient.findViewById(R.id.tv_email_klien);
        tvEmailKlien.setText(data.getEmail());
        TextView tvCatatanKlien = dialogClient.findViewById(R.id.tv_catatan_klien);
        tvCatatanKlien.setText(data.getCatatan());
        dialogClient.findViewById(R.id.iv_close).setOnClickListener(view -> {dialogClient.dismiss();});

        dialogClient.findViewById(R.id.btn_edit_klien).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEdit = new Dialog(Client.this);
                dialogEdit.setContentView(R.layout.dialog_edit_client);
                dialogEdit.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogEdit.setCancelable(false);
                dialogEdit.show();

                EditText etNamaKlien = dialogEdit.findViewById(R.id.et_nama_klien);
                etNamaKlien.setText(data.getNama());
                EditText etAlamatKlien = dialogEdit.findViewById(R.id.et_alamat_klien);
                etAlamatKlien.setText(data.getAlamat());
                EditText etNoTelpKlien = dialogEdit.findViewById(R.id.et_telp_klien);
                etNoTelpKlien.setText(data.getNo_telp());
                EditText etEmailKlien = dialogEdit.findViewById(R.id.et_email_klien);
                etEmailKlien.setText(data.getEmail());
                EditText etCatatanKlien = dialogEdit.findViewById(R.id.et_catatan_klien);
                etCatatanKlien.setText(data.getCatatan());
                dialogEdit.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogEdit.dismiss();
                    }
                });

                dialogEdit.findViewById(R.id.btn_simpan_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        namaKlien = etNamaKlien.getText().toString();
                        alamatKlien = etAlamatKlien.getText().toString();
                        noTelpKlien = etNoTelpKlien.getText().toString();
                        emailKlien = etEmailKlien.getText().toString();
                        catatanKlien = etCatatanKlien.getText().toString();

                        if (namaKlien.trim().isEmpty()) {
                            etNamaKlien.setError("Required");
                        } else if (alamatKlien.trim().isEmpty()) {
                            etAlamatKlien.setError("Required");
                        } else if (noTelpKlien.trim().isEmpty()) {
                            etNoTelpKlien.setError("Required");
                        } else if (noTelpKlien.length() > 13) {
                            etNoTelpKlien.setError("Nomor Telpon Tidak Boleh Melebihi 13 digit");
                        } else if (emailKlien.trim().isEmpty()) {
                            etEmailKlien.setError("Required");
                        } else if (!isValidEmail(emailKlien)){
                            etEmailKlien.setError("Format Email Tidak Didukung");
                        } else if (catatanKlien.trim().isEmpty()) {
                            etCatatanKlien.setError("Required");
                        } else {
                            updateClient(data.getId(), namaKlien, alamatKlien, noTelpKlien, emailKlien, catatanKlien);
                            dialogEdit.dismiss();
                            dialogClient.dismiss();
                        }

                    }
                });


            }
        });

        dialogClient.findViewById(R.id.btn_hapus_klien).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientId = data.getId();
                reference.child(clientId).removeValue();
                Toast.makeText(Client.this, "Data Klien Telah Dihapus", Toast.LENGTH_SHORT).show();
                refreshClientList();
                dialogClient.dismiss();
            }
        });
        }
    private void updateClient(String id, String nama, String alamat, String noTelp, String email, String catatan) {
        ModelClient mc = new ModelClient(id, nama, alamat, noTelp, email, catatan);
        reference.child(id).setValue(mc);
        Toast.makeText(Client.this, "Data Klien Berhasil Diubah", Toast.LENGTH_SHORT).show();
        refreshClientList();
    }
}


