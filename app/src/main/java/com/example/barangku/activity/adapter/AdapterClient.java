package com.example.barangku.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelClient;
import com.example.barangku.activity.user_activity.ItemClickListener;

import java.util.List;

public class AdapterClient extends RecyclerView.Adapter<AdapterClient.ViewHolder> {
    private Context ctx;
    private ItemClickListener itemClickListener;
    private List<ModelClient> list_client;

    public AdapterClient(Context ctx, List<ModelClient> list_client) {
        this.ctx = ctx;
        this.list_client = list_client;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setFilter(List<ModelClient> filteredList){
        this.list_client = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterClient.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View varview = LayoutInflater.from(ctx).inflate(R.layout.list_client, viewGroup, false);
        return new ViewHolder(varview);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClient.ViewHolder holder, int i ) {
        ModelClient mc = list_client.get(i);

        holder.tvnama.setText(mc.getNama());
        holder.tvalamat.setText(mc.getAlamat());
        holder.tvemail.setText(mc.getEmail());
        holder.tvnomor.setText(mc.getNo_telp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClickListener(mc, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_client.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvnama,tvnomor,tvalamat,tvemail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvalamat=(TextView)itemView.findViewById(R.id.tv_alamat_client);
            tvnama=(TextView)itemView.findViewById(R.id.tv_nama_client);
            tvnomor=(TextView)itemView.findViewById(R.id.tv_telp_client);
            tvemail=(TextView)itemView.findViewById(R.id.tv_email);

        }
    }
}
