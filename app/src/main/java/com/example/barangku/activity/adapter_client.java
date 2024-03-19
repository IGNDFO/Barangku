package com.example.barangku.activity;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barangku.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class adapter_client extends FirebaseRecyclerAdapter<> {

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
