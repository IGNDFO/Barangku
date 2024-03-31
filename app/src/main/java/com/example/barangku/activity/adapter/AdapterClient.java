package com.example.barangku.activity.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.example.barangku.activity.model.ModelClient;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AdapterClient extends FirebaseRecyclerAdapter<ModelClient, AdapterClient.clientViewHolder>{


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterClient(@NonNull FirebaseRecyclerOptions<ModelClient> options) {
        super(options);
        notifyDataSetChanged();
    }
    public void setFilteredList(FirebaseRecyclerOptions<ModelClient> options){
        updateOptions(options);
    }

    public void updateOptions(FirebaseRecyclerOptions<ModelClient> options) {
        super.updateOptions(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull clientViewHolder holder, int position, @NonNull ModelClient mc) {

    holder.tvnama.setText(mc.getNama());
    holder.tvalamat.setText(mc.getAlamat());
    holder.tvemail.setText(mc.getEmail());
    holder.tvnomor.setText(mc.getNo_telp());

    }

    @NonNull
    @Override
    public clientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_client,parent,false);
        return new clientViewHolder(view);
    }

    class clientViewHolder extends RecyclerView.ViewHolder{

        TextView tvnama,tvnomor,tvalamat,tvemail;
        public clientViewHolder(@NonNull View itemView) {
            super(itemView);

            tvalamat=(TextView)itemView.findViewById(R.id.tv_alamat_client);
            tvnama=(TextView)itemView.findViewById(R.id.tv_nama_client);
            tvnomor=(TextView)itemView.findViewById(R.id.tv_telp_client);
            tvemail=(TextView)itemView.findViewById(R.id.tv_email);

        }
    }

}
