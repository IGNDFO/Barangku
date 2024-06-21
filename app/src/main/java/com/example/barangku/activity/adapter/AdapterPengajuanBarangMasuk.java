package com.example.barangku.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barangku.R;
import com.example.barangku.activity.model.ModelBarangMasuk;
import com.example.barangku.activity.model.ModelPengajuanBarangMasuk;
import com.example.barangku.activity.user_activity.utils.ItemClickClient;
import com.example.barangku.activity.user_activity.utils.ItemClickPengajuanBarangMasuk;

import java.util.List;

public class AdapterPengajuanBarangMasuk extends RecyclerView.Adapter<AdapterPengajuanBarangMasuk.ViewHolder> {

    private List<ModelPengajuanBarangMasuk> pengajuanList;
    private Context ctx;
    private ItemClickPengajuanBarangMasuk itemClickPengajuanBarangMasuk;
    public AdapterPengajuanBarangMasuk(List<ModelPengajuanBarangMasuk> pengajuanList, Context ctx) {
        this.pengajuanList = pengajuanList;
        this.ctx = ctx;
    }

    public void setItemClickPengajuanBarangMasuk(ItemClickPengajuanBarangMasuk itemClickPengajuanBarangMasuk) {
        this.itemClickPengajuanBarangMasuk = itemClickPengajuanBarangMasuk;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengajuan_barang_masuk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelPengajuanBarangMasuk pengajuan = pengajuanList.get(position);
        holder.tvNamaBarang.setText(pengajuan.getNamaBarang());
        holder.tvJumlahBarang.setText(String.valueOf(pengajuan.getJumlahBarang()));
        holder.tvKeterangan.setText(pengajuan.getKeterangan());

        holder.itemView.setOnClickListener(v -> itemClickPengajuanBarangMasuk.onItemClickListener(pengajuan,position));
    }

    @Override
    public int getItemCount() {
        return pengajuanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaBarang, tvJumlahBarang, tvKeterangan;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNamaBarang = itemView.findViewById(R.id.tvNamaBarang);
            tvJumlahBarang = itemView.findViewById(R.id.tvJumlahBarang);
            tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
        }
    }
}
