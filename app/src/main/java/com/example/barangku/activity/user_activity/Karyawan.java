package com.example.barangku.activity.user_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;

import com.example.barangku.activity.adapter.AdapterKaryawan;
import com.example.barangku.activity.model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Karyawan extends AppCompatActivity {
    private TextView tvToolbar;
    private ImageView ivBack;
    private RecyclerView rvKaryawan;
    private DatabaseReference karyawanRef = FirebaseDatabase.getInstance().getReference("User");
    private List<ModelUser> listKaryawan;
    AdapterKaryawan adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan);
        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Kelola User");

        ivBack =findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Karyawan.this, Admin.class);
                startActivity(intent);
            }
        });

        rvKaryawan = findViewById(R.id.rv_karyawan);
        rvKaryawan.setHasFixedSize(true);
        rvKaryawan.setLayoutManager(new LinearLayoutManager(this));

        listKaryawan = new ArrayList<>();
        adapter = new AdapterKaryawan(this, listKaryawan);
        rvKaryawan.setAdapter(adapter);

        karyawanRef.orderByChild("jabatan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listKaryawan.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ModelUser user = postSnapshot.getValue(ModelUser.class);
                    listKaryawan.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Karyawan.this, "Failed to load employees", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemDeleteListener(new AdapterKaryawan.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                showDeleteConfirmationDialog(position);
            }
        });
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus data User ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteKaryawan(position);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteKaryawan(int position) {
        ModelUser user = listKaryawan.get(position);
        DatabaseReference userRef = karyawanRef.child(user.getId());
        userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Karyawan.this, "User berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Karyawan.this, "Gagal menghapus data user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}