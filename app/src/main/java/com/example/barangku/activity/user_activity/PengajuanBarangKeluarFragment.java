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
import com.example.barangku.activity.adapter.AdapterPengajuanBarangKeluar;
import com.example.barangku.activity.model.ModelPengajuanBarangKeluar;
import com.example.barangku.activity.model.ModelBarangKeluar;
import com.example.barangku.activity.model.ModelStock;
import com.example.barangku.activity.user_activity.utils.ItemClickPengajuanBarangKeluar;
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

public class PengajuanBarangKeluarFragment extends Fragment implements ItemClickPengajuanBarangKeluar {
    private TextView tvToolbar;
    private ImageView ivBack;

    private RecyclerView rvPengajuanBarangKeluar;
    private AdapterPengajuanBarangKeluar adapter;
    private List<ModelPengajuanBarangKeluar> listPengajuan = new ArrayList<>();

    private DatabaseReference pengajuanRef = FirebaseDatabase.getInstance().getReference("PengajuanBarangKeluar");
    private DatabaseReference riwayatPengajuanRef = FirebaseDatabase.getInstance().getReference("RiwayatPengajuanBarangKeluar");
    DatabaseReference barangKeluarRef = FirebaseDatabase.getInstance().getReference("BarangKeluar");
    DatabaseReference stokBarangRef = FirebaseDatabase.getInstance().getReference("StockBarang");

    public PengajuanBarangKeluarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pengajuan_barang_keluar, container, false);
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
        rvPengajuanBarangKeluar = view.findViewById(R.id.rv_pengajuan);

        adapter = new AdapterPengajuanBarangKeluar(listPengajuan, getView().getContext());
        adapter.setItemClickPengajuanBarangKeluar(this);
        LinearLayoutManager lmPengajuanBk = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.VERTICAL, false);
        rvPengajuanBarangKeluar.setLayoutManager(lmPengajuanBk);
        rvPengajuanBarangKeluar.setAdapter(adapter);

        loadPengajuanData();
    }

    private void loadPengajuanData() {
        pengajuanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPengajuan.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.getKey();
                    String namaBarang = dataSnapshot.child("namaBarang").getValue(String.class);
                    String satuan = dataSnapshot.child("satuan").getValue(String.class);
                    String keterangan = dataSnapshot.child("keterangan").getValue(String.class);
                    String tanggalKeluar = dataSnapshot.child("tanggalKeluar").getValue(String.class);
                    String formattedTanggalKeluar = formatTanggal(tanggalKeluar);
                    String namaKlien = dataSnapshot.child("namaKlien").getValue(String.class);
                    String alamatKlien = dataSnapshot.child("alamatKlien").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);
                    Long jumlahBarang = dataSnapshot.child("jumlahBarang").getValue(Long.class);

                    ModelPengajuanBarangKeluar mpbk = new ModelPengajuanBarangKeluar(id, namaBarang, satuan, keterangan, formattedTanggalKeluar, namaKlien, alamatKlien, status,jumlahBarang);

                    if (mpbk != null) {
                        listPengajuan.add(mpbk);
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
    private void approvePengajuan(ModelPengajuanBarangKeluar pengajuan, int position) {
        String barangKeluarId = barangKeluarRef.push().getKey();
        ModelBarangKeluar barangKeluarBaru = new ModelBarangKeluar(barangKeluarId, pengajuan.getNamaBarang(), pengajuan.getSatuan(), pengajuan.getKeterangan(), pengajuan.getTanggalKeluar() , pengajuan.getNamaKlien(), pengajuan.getAlamatKlien(), pengajuan.getJumlahBarang());
        barangKeluarRef.child(barangKeluarId).setValue(barangKeluarBaru).addOnCompleteListener(addTask -> {
            if (addTask.isSuccessful()) {
                perbaruiStokBarang(pengajuan);
                pengajuan.setStatus("Diterima");
                simpanRiwayatPengajuan(pengajuan);
                hapusPengajuanSetelahDisetujui(pengajuan, position);
            } else {
                Toast.makeText(requireActivity(), "Gagal menambahkan data baru ke BarangKeluar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectPengajuan(ModelPengajuanBarangKeluar pengajuan) {
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

    private void perbaruiStokBarang(ModelPengajuanBarangKeluar pengajuan) {
        stokBarangRef.orderByChild("namaBarang").equalTo(pengajuan.getNamaBarang()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean stokDitemukan = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelStock stockItem = dataSnapshot.getValue(ModelStock.class);
                    if (stockItem != null) {
                        stokDitemukan = true;
                        long currentStok = Long.parseLong(stockItem.getJumlahBarang());
                        long updatedStok = currentStok - pengajuan.getJumlahBarang();
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

    private void simpanRiwayatPengajuan(ModelPengajuanBarangKeluar pengajuan) {
        String riwayatId = riwayatPengajuanRef.push().getKey();
        riwayatPengajuanRef.child(riwayatId).setValue(pengajuan).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Pengajuan " + pengajuan.getStatus() + " dan disimpan dalam riwayat", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "Gagal menyimpan pengajuan ke dalam riwayat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hapusPengajuanSetelahDisetujui(ModelPengajuanBarangKeluar pengajuan, int position) {
        pengajuanRef.child(pengajuan.getId()).removeValue().addOnCompleteListener(removeTask -> {
            if (!removeTask.isSuccessful()) {
                Toast.makeText(requireActivity(), "Gagal menghapus pengajuan setelah disetujui", Toast.LENGTH_SHORT).show();
            } else {
                if (position >= 0 && position < listPengajuan.size()) {
                    listPengajuan.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public void onItemClickListener(ModelPengajuanBarangKeluar data, int position) {
        if (position >= 0 && position < listPengajuan.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Pengajuan Barang Keluar")
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
