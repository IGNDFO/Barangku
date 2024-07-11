package com.example.barangku.activity.user_activity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RiwayatPengajuanBarangKeluarFragment extends Fragment {
    private TextView tvToolbar, tvTotalRiwayat;
    private ImageView ivBack;
    private RecyclerView rvRiwayatPengajuan;
    private AdapterRiwayatPengajuanBarangKeluar adapter;
    private List<ModelRiwayatPengajuanBarangKeluar> riwayatList = new ArrayList<>();
    private DatabaseReference riwayatPengajuanRef = FirebaseDatabase.getInstance().getReference("RiwayatPengajuanBarangKeluar");
    private Spinner spinnerFilterStatus, spinnerFilterTanggal;
    private String filterStatus = "Semua";
    private String filterTanggal = "Tanggal Naik";

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

        tvToolbar = view.findViewById(R.id.tv_judul);
        tvToolbar.setText("Riwayat Pengajuan");
        tvTotalRiwayat = view.findViewById(R.id.tv_total_riwayat_keluar);
        spinnerFilterStatus = view.findViewById(R.id.spinner_filter);
        spinnerFilterTanggal = view.findViewById(R.id.spinner_filters);

        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        rvRiwayatPengajuan = view.findViewById(R.id.rv_riwayat_pengajuan);
        adapter = new AdapterRiwayatPengajuanBarangKeluar(riwayatList, getView().getContext());
        LinearLayoutManager lmRiwayat = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.VERTICAL, false);
        rvRiwayatPengajuan.setLayoutManager(lmRiwayat);
        rvRiwayatPengajuan.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterSpinnerStatus = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options_riwayat, android.R.layout.simple_spinner_item);
        adapterSpinnerStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterStatus.setAdapter(adapterSpinnerStatus);

        ArrayAdapter<CharSequence> adapterSpinnerTanggal = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_date_options, android.R.layout.simple_spinner_item);
        adapterSpinnerTanggal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterTanggal.setAdapter(adapterSpinnerTanggal);

        spinnerFilterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterStatus = parentView.getItemAtPosition(position).toString();
                loadRiwayatData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        spinnerFilterTanggal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterTanggal = parentView.getItemAtPosition(position).toString();
                loadRiwayatData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        loadRiwayatData();
    }

    private void loadRiwayatData() {
        riwayatPengajuanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                riwayatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelRiwayatPengajuanBarangKeluar riwayat = dataSnapshot.getValue(ModelRiwayatPengajuanBarangKeluar.class);
                    if (riwayat != null && (filterStatus.equals("Semua") || filterStatus.equals(riwayat.getStatus()))) {
                        riwayat.setTanggalKeluar(formatTanggal(riwayat.getTanggalKeluar()));
                        riwayatList.add(riwayat);
                    }
                }

                // Sort by date
                if (filterTanggal.equals("Tanggal Naik")) {
                    Collections.sort(riwayatList, new Comparator<ModelRiwayatPengajuanBarangKeluar>() {
                        @Override
                        public int compare(ModelRiwayatPengajuanBarangKeluar o1, ModelRiwayatPengajuanBarangKeluar o2) {
                            return o1.getTanggalKeluar().compareTo(o2.getTanggalKeluar());
                        }
                    });
                } else if (filterTanggal.equals("Tanggal Turun")) {
                    Collections.sort(riwayatList, new Comparator<ModelRiwayatPengajuanBarangKeluar>() {
                        @Override
                        public int compare(ModelRiwayatPengajuanBarangKeluar o1, ModelRiwayatPengajuanBarangKeluar o2) {
                            return o2.getTanggalKeluar().compareTo(o1.getTanggalKeluar());
                        }
                    });
                }

                adapter.notifyDataSetChanged();
                tvTotalRiwayat.setText("Total Riwayat Pengajuan: " + riwayatList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "Gagal memuat data riwayat pengajuan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTanggal(String tanggal) {
        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));
        SimpleDateFormat sdfDestination = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        try {
            Date date = sdfSource.parse(tanggal);
            return sdfDestination.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return tanggal;
        }
    }
}