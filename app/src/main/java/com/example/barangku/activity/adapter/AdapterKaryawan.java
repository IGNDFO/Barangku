package com.example.barangku.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelUser;

import java.util.List;

public class AdapterKaryawan extends RecyclerView.Adapter<AdapterKaryawan.KaryawanViewHolder> {
    private Context context;
    private List<ModelUser> listKaryawan;

    public AdapterKaryawan(Context context, List<ModelUser> listKaryawan) {
        this.context = context;
        this.listKaryawan = listKaryawan;
    }

    @NonNull
    @Override
    public AdapterKaryawan.KaryawanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_karyawan, parent, false);
        return new KaryawanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterKaryawan.KaryawanViewHolder holder, int position) {
        ModelUser user = listKaryawan.get(position);
        holder.tvNamaKaryawan.setText(user.getNama());
        holder.tvEmailKaryawan.setText(user.getEmail());
        holder.tvJabatan.setText(user.getJabatan());
    }

    @Override
    public int getItemCount() {
        return listKaryawan.size();
    }

    public class KaryawanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaKaryawan, tvEmailKaryawan, tvJabatan;
        public KaryawanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmailKaryawan = itemView.findViewById(R.id.tv_email_karyawan);
            tvNamaKaryawan = itemView.findViewById(R.id.tv_nama_karyawan);
            tvJabatan = itemView.findViewById(R.id.tv_jabatan);
        }
    }
}
