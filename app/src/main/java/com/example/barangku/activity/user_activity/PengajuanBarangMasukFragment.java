package com.example.barangku.activity.user_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterPengajuanBarangMasuk;
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.example.barangku.activity.model.ModelPengajuanBarangMasuk;
import com.example.barangku.activity.model.ModelStock;
import com.example.barangku.activity.user_activity.utils.ItemClickPengajuanBarangMasuk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PengajuanBarangMasukFragment extends Fragment implements ItemClickPengajuanBarangMasuk {

    private RecyclerView rvPengajuanBarangMasuk;
    private AdapterPengajuanBarangMasuk adapter;
    private List<ModelPengajuanBarangMasuk> pengajuanList = new ArrayList<>();
    private DatabaseReference pengajuanRef = FirebaseDatabase.getInstance().getReference("PengajuanBarangMasuk");
    DatabaseReference barangMasukRef = FirebaseDatabase.getInstance().getReference("BarangMasuk");
    DatabaseReference stokBarangRef = FirebaseDatabase.getInstance().getReference("StockBarang");

    public PengajuanBarangMasukFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pengajuan_barang_masuk, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPengajuanBarangMasuk = view.findViewById(R.id.rv_pengajuan);

        adapter = new AdapterPengajuanBarangMasuk(pengajuanList, getView().getContext());
        adapter.setItemClickPengajuanBarangMasuk(this);
        LinearLayoutManager lmPengajuanBm = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.VERTICAL, false);
        rvPengajuanBarangMasuk.setLayoutManager(lmPengajuanBm);
        rvPengajuanBarangMasuk.setAdapter(adapter);

        loadPengajuanData();
    }

    private void loadPengajuanData() {
        pengajuanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pengajuanList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.getKey();
                    String namaBarang = dataSnapshot.child("namaBarang").getValue(String.class);
                    String satuan = dataSnapshot.child("satuan").getValue(String.class);
                    String keterangan = dataSnapshot.child("keterangan").getValue(String.class);
                    String tanggalMasuk = dataSnapshot.child("tanggalMasuk").getValue(String.class);
                    int jumlahBarang = dataSnapshot.child("jumlahBarang").getValue(Integer.class);

                    ModelPengajuanBarangMasuk mpbm = new ModelPengajuanBarangMasuk(id, namaBarang, satuan, keterangan, tanggalMasuk, jumlahBarang);

                    if (mpbm != null) {
                        pengajuanList.add(mpbm);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "Gagal memuat data pengajuan", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void approvePengajuan(ModelPengajuanBarangMasuk pengajuan, int position) {
        // Tambahkan data baru ke tabel BarangMasuk
        String barangMasukId = barangMasukRef.push().getKey();
        ModelBarangMasuk barangMasukBaru = new ModelBarangMasuk(barangMasukId, pengajuan.getNamaBarang(), pengajuan.getSatuan(), pengajuan.getKeterangan(), pengajuan.getTanggalMasuk(), pengajuan.getJumlahBarang());
        barangMasukRef.child(barangMasukId).setValue(barangMasukBaru).addOnCompleteListener(addTask -> {
            if (addTask.isSuccessful()) {
                perbaruiStokBarang(pengajuan);
                hapusPengajuanSetelahDisetujui(pengajuan, position);
            } else {
                Toast.makeText(requireActivity(), "Gagal menambahkan data baru ke BarangMasuk", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void perbaruiStokBarang(ModelPengajuanBarangMasuk pengajuan) {
        stokBarangRef.orderByChild("namaBarang").equalTo(pengajuan.getNamaBarang()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean stokDitemukan = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelStock stockItem = dataSnapshot.getValue(ModelStock.class);
                    if (stockItem != null) {
                        stokDitemukan = true;
                        int currentStok = Integer.parseInt(stockItem.getJumlahBarang());
                        int updatedStok = currentStok + pengajuan.getJumlahBarang();
                        stockItem.setJumlahBarang(String.valueOf(updatedStok));

                        dataSnapshot.getRef().setValue(stockItem).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(requireActivity(), "Pengajuan disetujui dan Jumlah stok diperbarui", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireActivity(), "Gagal memperbarui stok barang", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
                if (!stokDitemukan) {
                    Toast.makeText(requireActivity(), "Stok barang tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "Gagal memperbarui stok barang", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void hapusPengajuanSetelahDisetujui(ModelPengajuanBarangMasuk pengajuan, int position) {
        pengajuanRef.child(pengajuan.getId()).removeValue().addOnCompleteListener(removeTask -> {
            if (!removeTask.isSuccessful()) {
                Toast.makeText(requireActivity(), "Gagal menghapus pengajuan setelah disetujui", Toast.LENGTH_SHORT).show();
            } else {
                pengajuanList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }

//    private void removePengajuanFromList(ModelBarangMasuk pengajuan, int position) {
//        pengajuanList.remove(position);
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onItemClickListener(ModelPengajuanBarangMasuk data, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Pengajuan Barang" + data.getId() + data.getJumlahBarang() + data.getNamaBarang())
                .setMessage("Apakah Anda ingin menerima atau menolak pengajuan ini?")
                .setPositiveButton("Terima", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        approvePengajuan(data, position);
                    }
                })
                .setNegativeButton("Tolak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pengajuanRef.child(data.getId()).removeValue();
                        Toast.makeText(requireActivity(), "Data Berhasil di Tolak", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Batal", null)
                .show();
    }
}
