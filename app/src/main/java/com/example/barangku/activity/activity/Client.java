package com.example.barangku.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.barangku.R;
import com.example.barangku.activity.adapter.adapter_client;
import com.example.barangku.activity.model.model_client;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class Client extends AppCompatActivity {
private TextView tv_toolbar;
RecyclerView rv_client;
adapter_client Adapter_client;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

    tv_toolbar=findViewById(R.id.tv_judul);
    tv_toolbar.setText("Client");

        rv_client=findViewById(R.id.rv_client);
        rv_client.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<model_client> options =
                new FirebaseRecyclerOptions.Builder<model_client>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Client"), model_client.class)
                        .build();

     Adapter_client =new adapter_client(options);
     rv_client.setAdapter(Adapter_client);
    }

    @Override
    protected void onStart() {
      Adapter_client.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Adapter_client.stopListening();
        super.onStop();
    }
}