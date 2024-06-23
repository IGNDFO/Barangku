package com.example.barangku.activity.user_activity;

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
import com.example.barangku.activity.adapter.AdapterRiwayatPengajuanBarangKeluar;
import com.example.barangku.activity.adapter.AdapterRiwayatPengajuanBarangMasuk;
import com.example.barangku.activity.model.ModelRiwayatPengajuanBarangKeluar;
import com.example.barangku.activity.model.ModelRiwayatPengajuanBarangMasuk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RiwayatPengajuanBarangKeluarFragment extends Fragment {

    private RecyclerView rvRiwayatPengajuan;
    private AdapterRiwayatPengajuanBarangKeluar adapter;
    private List<ModelRiwayatPengajuanBarangKeluar> riwayatList = new ArrayList<>();
    private DatabaseReference riwayatPengajuanRef = FirebaseDatabase.getInstance().getReference("RiwayatPengajuanBarangKeluar");

    public RiwayatPengajuanBarangKeluarFragment() {
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
        return inflater.inflate(R.layout.fragment_riwayat_pengajuan_barang_keluar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRiwayatPengajuan = view.findViewById(R.id.rv_riwayat_pengajuan);

        adapter = new AdapterRiwayatPengajuanBarangKeluar(riwayatList, getView().getContext());
        LinearLayoutManager lmRiwayat = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.VERTICAL, false);
        rvRiwayatPengajuan.setLayoutManager(lmRiwayat);
        rvRiwayatPengajuan.setAdapter(adapter);

        loadRiwayatData();
    }

    private void loadRiwayatData() {
        riwayatPengajuanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                riwayatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelRiwayatPengajuanBarangKeluar riwayat = dataSnapshot.getValue(ModelRiwayatPengajuanBarangKeluar.class);
                    if (riwayat != null) {
                        riwayatList.add(riwayat);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "Gagal memuat data riwayat pengajuan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}