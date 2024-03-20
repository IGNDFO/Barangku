package com.example.barangku.activity;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barangku.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class adapter_client extends FirebaseRecyclerAdapter<model_client,adapter_client.clientViewHolder>{


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public adapter_client(@NonNull FirebaseRecyclerOptions<model_client> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull clientViewHolder holder, int position, @NonNull model_client mc) {

    holder.tvnama.setText(mc.getNama());
    holder.tvalamat.setText(mc.getAlamat());
    holder.tvemail.setText(mc.getEmail());
    holder.tvnomor.setText(String.valueOf(mc.getNo_telp()));
    //untuk manggil gambar ic_launcher_background gambaar yang pakai di list client
//        Glide.with(holder.img.getContext())
//                .load(model.getImg())
//                .placeholder(R.drawable.ic_launcher_background)
//                .circleCrop()
//                .error(R.drawable.ic_launcher_foreground)
//                .into(holder.img);
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
