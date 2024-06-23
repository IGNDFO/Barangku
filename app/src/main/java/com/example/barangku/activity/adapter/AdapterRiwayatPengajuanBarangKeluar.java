package com.example.barangku.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelRiwayatPengajuanBarangKeluar;
import com.example.barangku.activity.model.ModelRiwayatPengajuanBarangMasuk;

import java.util.List;

public class AdapterRiwayatPengajuanBarangKeluar extends RecyclerView.Adapter<AdapterRiwayatPengajuanBarangKeluar.ViewHolder>{
    private List<ModelRiwayatPengajuanBarangKeluar> listRiwayat;
    private Context context;

    public AdapterRiwayatPengajuanBarangKeluar(List<ModelRiwayatPengajuanBarangKeluar> listRiwayat, Context context) {
        this.listRiwayat = listRiwayat;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterRiwayatPengajuanBarangKeluar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_pengajuan_barang_keluar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelRiwayatPengajuanBarangKeluar riwayat = listRiwayat.get(position);
        holder.tvNamaBarang.setText(riwayat.getNamaBarang());
        holder.tvJumlahBarang.setText(String.valueOf(riwayat.getJumlahBarang()));
        holder.tvSatuan.setText(String.valueOf(riwayat.getSatuan()));
        holder.tvKeterangan.setText(riwayat.getKeterangan());
        holder.tvTanggalKeluar.setText(riwayat.getTanggalKeluar());
        holder.tvNamaKlien.setText(riwayat.getNamaKlien());
        holder.tvStatus.setText(riwayat.getStatus());
        holder.tvAlamatKlien.setText(riwayat.getAlamatKlien());

        if ("Diterima".equals(riwayat.getStatus())) {
            holder.tvStatus.setTextColor(Color.GREEN);
        } else {
            holder.tvStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return listRiwayat.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTanggalKeluar, tvNamaKlien, tvAlamatKlien, tvNamaBarang, tvSatuan, tvKeterangan, tvStatus, tvJumlahBarang;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaKlien = itemView.findViewById(R.id.tv_nama_klien);
            tvAlamatKlien = itemView.findViewById(R.id.tv_alamat_klien);
            tvNamaBarang = itemView.findViewById(R.id.tv_nama_barang);
            tvJumlahBarang = itemView.findViewById(R.id.tv_jumlah_barang);
            tvSatuan = itemView.findViewById(R.id.tv_satuan);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTanggalKeluar = itemView.findViewById(R.id.tv_tanggal_keluar);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
        }
    }
}
