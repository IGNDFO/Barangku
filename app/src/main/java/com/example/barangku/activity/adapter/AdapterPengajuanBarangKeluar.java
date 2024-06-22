package com.example.barangku.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelPengajuanBarangKeluar;
import com.example.barangku.activity.model.ModelPengajuanBarangMasuk;
import com.example.barangku.activity.user_activity.utils.ItemClickPengajuanBarangKeluar;

import java.util.List;

public class AdapterPengajuanBarangKeluar extends RecyclerView.Adapter<AdapterPengajuanBarangKeluar.ViewHolder> {
    private List<ModelPengajuanBarangKeluar> listPengajuan;
    private Context ctx;
    private ItemClickPengajuanBarangKeluar itemClickPengajuanBarangKeluar;

    public AdapterPengajuanBarangKeluar(List<ModelPengajuanBarangKeluar> listPengajuan, Context ctx) {
        this.listPengajuan = listPengajuan;
        this.ctx = ctx;
    }

    public void setItemClickPengajuanBarangKeluar(ItemClickPengajuanBarangKeluar itemClickPengajuanBarangKeluar) {
        this.itemClickPengajuanBarangKeluar = itemClickPengajuanBarangKeluar;
    }

    @NonNull
    @Override
    public AdapterPengajuanBarangKeluar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengajuan_barang_keluar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPengajuanBarangKeluar.ViewHolder holder, int position) {
        ModelPengajuanBarangKeluar pengajuan = listPengajuan.get(position);
        holder.tvNamaBarang.setText(pengajuan.getNamaBarang());
        holder.tvJumlahBarang.setText(String.valueOf(pengajuan.getJumlahBarang()));
        holder.tvSatuan.setText(String.valueOf(pengajuan.getSatuan()));
        holder.tvKeterangan.setText(pengajuan.getKeterangan());
        holder.tvTanggalKeluar.setText(pengajuan.getTanggalKeluar());
        holder.tvNamaKlien.setText(pengajuan.getNamaKlien());
        holder.tvAlamatKlien.setText(pengajuan.getAlamatKlien());

        holder.itemView.setOnClickListener(v -> itemClickPengajuanBarangKeluar.onItemClickListener(pengajuan,position));
    }

    @Override
    public int getItemCount() {
        return listPengajuan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTanggalKeluar, tvNamaKlien, tvAlamatKlien, tvNamaBarang, tvSatuan, tvKeterangan, tvJumlahBarang;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNamaKlien = itemView.findViewById(R.id.tv_nama_klien);
            tvAlamatKlien = itemView.findViewById(R.id.tv_alamat_klien);
            tvNamaBarang = itemView.findViewById(R.id.tv_nama_barang);
            tvJumlahBarang = itemView.findViewById(R.id.tv_jumlah_barang);
            tvSatuan = itemView.findViewById(R.id.tv_satuan);
            tvTanggalKeluar = itemView.findViewById(R.id.tv_tanggal_keluar);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);

        }
    }
}
