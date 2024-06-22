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
import com.example.barangku.activity.model.ModelRiwayatPengajuanBarangMasuk;

import java.util.List;

public class AdapterRiwayatPengajuanBarangMasuk  extends RecyclerView.Adapter<AdapterRiwayatPengajuanBarangMasuk.ViewHolder>{
    private List<ModelRiwayatPengajuanBarangMasuk> listRiwayat;
    private Context context;

    public AdapterRiwayatPengajuanBarangMasuk(List<ModelRiwayatPengajuanBarangMasuk> listRiwayat, Context context) {
        this.listRiwayat = listRiwayat;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterRiwayatPengajuanBarangMasuk.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_pengajuan_barang_masuk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRiwayatPengajuanBarangMasuk.ViewHolder holder, int position) {
        ModelRiwayatPengajuanBarangMasuk riwayat = listRiwayat.get(position);
        holder.tvNamaBarang.setText(riwayat.getNamaBarang());
        holder.tvSatuan.setText(riwayat.getSatuan());
        holder.tvKeterangan.setText(riwayat.getKeterangan());
        holder.tvTanggalMasuk.setText(riwayat.getTanggalMasuk());
        holder.tvStatus.setText(riwayat.getStatus());
        holder.tvJumlahBarang.setText(String.valueOf(riwayat.getJumlahBarang()));

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
        TextView tvNamaBarang, tvSatuan, tvKeterangan, tvTanggalMasuk, tvJumlahBarang, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaBarang = itemView.findViewById(R.id.tv_nama_barang);
            tvSatuan = itemView.findViewById(R.id.tv_satuan);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
            tvTanggalMasuk = itemView.findViewById(R.id.tv_tanggal_masuk);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvJumlahBarang = itemView.findViewById(R.id.tv_jumlah_barang);
        }
    }
}
