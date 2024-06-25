package com.example.barangku.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterKaryawan extends RecyclerView.Adapter<AdapterKaryawan.KaryawanViewHolder> {
    private Context context;
    private List<ModelUser> listKaryawan;
    private OnItemDeleteListener onItemDeleteListener;
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }
    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public AdapterKaryawan(Context context, List<ModelUser> listKaryawan) {
        this.context = context;
        this.listKaryawan = listKaryawan;
    }

    @NonNull
    @Override
    public KaryawanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_karyawan, parent, false);
        return new KaryawanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KaryawanViewHolder holder, int position) {
        ModelUser karyawan = listKaryawan.get(position);
        holder.tvNamaKaryawan.setText(karyawan.getNama());
        holder.tvEmailKaryawan.setText(karyawan.getEmail());
        holder.tvJabatan.setText(karyawan.getJabatan());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemDeleteListener != null) {
                    onItemDeleteListener.onItemDelete(position);
                }
            }
        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (onItemDeleteListener != null) {
//                    onItemDeleteListener.onItemDelete(position);
//                }
//                return true;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listKaryawan.size();
    }

    public static class KaryawanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaKaryawan, tvEmailKaryawan, tvJabatan;
        ImageView ivDelete;

        public KaryawanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaKaryawan = itemView.findViewById(R.id.tv_nama_karyawan);
            tvEmailKaryawan = itemView.findViewById(R.id.tv_email_karyawan);
            tvJabatan = itemView.findViewById(R.id.tv_jabatan);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    private void deleteKaryawan(ModelUser karyawan) {
        DatabaseReference karyawanRef = FirebaseDatabase.getInstance().getReference("User");
        karyawanRef.child(karyawan.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete employee", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
