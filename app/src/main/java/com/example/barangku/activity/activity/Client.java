package com.example.barangku.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterClient;
import com.example.barangku.activity.model.ModelClient;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Client extends AppCompatActivity {
private TextView tvToolbar;
private NavigationView navClient;
RecyclerView rv_client;
AdapterClient adapterClient;


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        tvToolbar =findViewById(R.id.tv_judul);
        tvToolbar.setText("Client");
    navClient = findViewById(R.id.nav_Client);
    navClient.setNavigationItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case R.id.search:

                MenuItem searchItem = item;
                SearchView searchView = (SearchView) searchItem.getActionView();


                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        filterList(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Action saat teks dalam SearchView berubah
                        // Misalnya, lakukan update otomatis saat pengguna mengetik
                        return false;
                    }
                });
                break;
        }
        return true;
    });




//    svClient.findViewById(R.id.search);
//        svClient.clearFocus();
//        svClient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String search) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String search) {
//                filterList(search);
//                return true;
//            }
//        });
        rv_client=findViewById(R.id.rv_client);
        rv_client.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ModelClient> options =
                new FirebaseRecyclerOptions.Builder<ModelClient>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Client"), ModelClient.class)
                        .build();

     adapterClient = new AdapterClient(options);
     rv_client.setAdapter(adapterClient);

    }



    private void filterList(String search) {
        FirebaseRecyclerOptions<ModelClient> filterList =
                new FirebaseRecyclerOptions.Builder<ModelClient>()
                        .build();
        List<ModelClient> filteredList = new ArrayList<>();
        for (ModelClient modelClient: filteredList){
            if (modelClient.getNama().toLowerCase().contains(search.toLowerCase())){
                filteredList.add(modelClient);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this, "Nama Client Tidak Ditemukan", Toast.LENGTH_SHORT).show();
        }
        else {
            adapterClient.setFilteredList(filterList);
        }

    }

    @Override
    protected void onStart() {
      adapterClient.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapterClient.stopListening();
        super.onStop();
    }

//        @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search, menu);
//        MenuItem item = menu.findItem(R.id.search);
//        SearchView sv = (SearchView) item.getActionView();
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                txtSearch(s);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                txtSearch(s);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private void txtSearch (String str){
//        FirebaseRecyclerOptions<ModelClient> options =
//        new FirebaseRecyclerOptions.Builder<ModelClient>()
//                .setQuery(FirebaseDatabase.getInstance().getReference().child("Client"), ModelClient.class)
//                .build();
//        adapterClient = new AdapterClient(options);
//        adapterClient.startListening();
//        rv_client.setAdapter(adapterClient);
//
//    }

}