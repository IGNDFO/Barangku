package com.example.barangku.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barangku.R;
import com.example.barangku.activity.model.ModelStock;
import com.example.barangku.activity.user_activity.utils.ItemClickClient;
import com.example.barangku.activity.user_activity.utils.ItemClickStock;

import java.util.List;

public class AdapterStock extends RecyclerView.Adapter<AdapterStock.ViewHolder> {
    private Context ctx;
    private ItemClickStock itemCLickStock;

    public void setItemCLickStock(ItemClickStock itemCLickStock) {
        this.itemCLickStock = itemCLickStock;
    }

    private List<ModelStock> list_stock;

    public AdapterStock(Context ctx, List<ModelStock> list_stock) {
        this.ctx = ctx;
        this.list_stock = list_stock;
    }
    public void setFilter(List<ModelStock> filteredList){
        this.list_stock = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterStock.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View varview = LayoutInflater.from(ctx).inflate(R.layout.list_stock, viewGroup, false);
        return new ViewHolder(varview);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStock.ViewHolder holder, int i) {
        ModelStock ms = list_stock.get(i);

        holder.tvNamaBarang.setText(ms.getNamaBarang());
        holder.tvJumlahItem.setText(ms.getJumlahBarang());
        holder.tvSatuan.setText(ms.getSatuan());
        Glide.with(holder.ivFoto).load(ms.getGambar()).into(holder.ivFoto);
        if (ms.getEnable())
        {
            holder.tvStatus.setText("Enable");
            holder.tvStatus.setTextColor(Color.GREEN);
        }else {
            holder.tvStatus.setText("Disable");
            holder.tvStatus.setTextColor(Color.RED);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCLickStock.onItemClickListener(ms, i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list_stock.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNamaBarang, tvJumlahItem, tvSatuan, tvStatus;
        public ViewHolder(View itemView) {
            super(itemView);

            tvNamaBarang = (TextView)itemView.findViewById(R.id.tv_nama_barang);
            tvJumlahItem = (TextView)itemView.findViewById(R.id.tv_jumlah_item);
            ivFoto = (ImageView)itemView.findViewById(R.id.iv_foto);
            tvSatuan = (TextView) itemView.findViewById(R.id.tv_satuan);
            tvStatus =(TextView) itemView.findViewById(R.id.tv_status);


        }
    }
}
