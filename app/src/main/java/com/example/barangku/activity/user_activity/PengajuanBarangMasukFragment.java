package com.example.barangku.activity.user_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterPengajuanBarangMasuk;
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.example.barangku.activity.model.ModelPengajuanBarangMasuk;
import com.example.barangku.activity.model.ModelStock;
import com.example.barangku.activity.user_activity.laporan.Laporan;
import com.example.barangku.activity.user_activity.utils.ItemClickPengajuanBarangMasuk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PengajuanBarangMasukFragment extends Fragment implements ItemClickPengajuanBarangMasuk {
    private TextView tvToolbar;
    private ImageView ivBack;
    private RecyclerView rvPengajuanBarangMasuk;
    private AdapterPengajuanBarangMasuk adapter;
    private List<ModelPengajuanBarangMasuk> pengajuanList = new ArrayList<>();
    private DatabaseReference pengajuanRef = FirebaseDatabase.getInstance().getReference("PengajuanBarangMasuk");
    private DatabaseReference riwayatPengajuanRef = FirebaseDatabase.getInstance().getReference("RiwayatPengajuanBarangMasuk");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pengajuan_barang_masuk, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvToolbar = view.findViewById(R.id.tv_judul);
        tvToolbar.setText("Pengajuan");

        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class ) ;
                startActivity(intent);
            }
        });

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
                    String formattedTanggalMasuk = formatTanggal(tanggalMasuk);
                    String status = dataSnapshot.child("status").getValue(String.class);
                    int jumlahBarang = dataSnapshot.child("jumlahBarang").getValue(Integer.class);

                    ModelPengajuanBarangMasuk mpbm = new ModelPengajuanBarangMasuk(id, namaBarang, satuan, keterangan, formattedTanggalMasuk, status, jumlahBarang);

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
    private String formatTanggal(String tanggal) {
        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));
        SimpleDateFormat sdfDestination = new SimpleDateFormat("EEEE, dd MMMM yyyy",new Locale("id", "ID"));
        try {
            Date date = sdfSource.parse(tanggal);
            return sdfDestination.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return tanggal;
        }
    }
    private void approvePengajuan(ModelPengajuanBarangMasuk pengajuan, int position) {
        String barangMasukId = barangMasukRef.push().getKey();
        ModelBarangMasuk barangMasukBaru = new ModelBarangMasuk(barangMasukId, pengajuan.getNamaBarang(), pengajuan.getSatuan(), pengajuan.getKeterangan(), pengajuan.getTanggalMasuk(), pengajuan.getJumlahBarang());
        barangMasukRef.child(barangMasukId).setValue(barangMasukBaru).addOnCompleteListener(addTask -> {
            if (addTask.isSuccessful()) {
                perbaruiStokBarang(pengajuan);
                pengajuan.setStatus("Diterima");
                simpanRiwayatPengajuan(pengajuan);
                hapusPengajuanSetelahDisetujui(pengajuan, position);
            } else {
                Toast.makeText(requireActivity(), "Gagal menambahkan data baru ke BarangMasuk", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void rejectPengajuan(ModelPengajuanBarangMasuk pengajuan) {
        pengajuan.setStatus("Ditolak");
        simpanRiwayatPengajuan(pengajuan);
        pengajuanRef.child(pengajuan.getId()).removeValue().addOnCompleteListener(removeTask -> {
            if (removeTask.isSuccessful()) {
                Toast.makeText(requireActivity(), "Pengajuan berhasil ditolak dan disimpan dalam riwayat", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "Gagal menghapus pengajuan", Toast.LENGTH_SHORT).show();
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

    private void simpanRiwayatPengajuan(ModelPengajuanBarangMasuk pengajuan) {
        String riwayatId = riwayatPengajuanRef.push().getKey();
        riwayatPengajuanRef.child(riwayatId).setValue(pengajuan).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Pengajuan " + pengajuan.getStatus() + " dan disimpan dalam riwayat", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "Gagal menyimpan pengajuan ke dalam riwayat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hapusPengajuanSetelahDisetujui(ModelPengajuanBarangMasuk pengajuan, int position) {
        pengajuanRef.child(pengajuan.getId()).removeValue().addOnCompleteListener(removeTask -> {
            if (!removeTask.isSuccessful()) {
                Toast.makeText(requireActivity(), "Gagal menghapus pengajuan setelah disetujui", Toast.LENGTH_SHORT).show();
            } else {
                if (position >= 0 && position < pengajuanList.size()) {
                    pengajuanList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public void onItemClickListener(ModelPengajuanBarangMasuk data, int position) {
        if (position >= 0 && position < pengajuanList.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Pengajuan Barang Masuk")
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
                            rejectPengajuan(data);
                        }
                    })
                    .setNeutralButton("Batal", null)
                    .show();
        } else {
            Toast.makeText(requireActivity(), "Pengajuan tidak valid", Toast.LENGTH_SHORT).show();
        }
    }
}
