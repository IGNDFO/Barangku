package com.example.barangku.activity.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barangku.R;
import com.example.barangku.activity.adapter.AdapterClient;
import com.example.barangku.activity.model.ModelClient;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Client extends AppCompatActivity {
    private TextView tvToolbar;
    private SearchView searchView;
    private ImageView ivback;
    private ProgressDialog pd;
    RecyclerView rv_client;
    AdapterClient adapterClient;
    private List<ModelClient> list_client=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        tvToolbar = findViewById(R.id.tv_judul);
        tvToolbar.setText("Client");

        searchView = findViewById(R.id.search);
        rv_client=findViewById(R.id.rv_client);

        ivback=findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Client.this, MainActivity.class);
                startActivity(intent);
            }
        });

        pd=new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        adapterClient =new AdapterClient(Client.this,list_client);
        LinearLayoutManager lm_client = new  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_client.setLayoutManager(lm_client);
        rv_client.setAdapter(adapterClient);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Client");
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                     ModelClient mc = snapshot.getValue(ModelClient.class);
                     list_client.add(mc);
                 }
                 pd.dismiss();
                adapterClient.notifyDataSetChanged();
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
             }
         });
    }

    private void filterList(String query) {
        List<ModelClient> filteredList = new ArrayList<>();
        String queryInLower = query.toLowerCase();
        for (ModelClient mc : list_client) {
            if (mc.getNama().toLowerCase().contains(queryInLower)) {
                filteredList.add(mc);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No clients found", Toast.LENGTH_SHORT).show();
        } else {
            adapterClient.setFilter(filteredList);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

//    navClient.setNavigationItemSelectedListener(item -> {
//        switch (item.getItemId()) {
//            case R.id.search:
//
//                MenuItem searchItem = item;
//                SearchView searchView = (SearchView) searchItem.getActionView();
//
//        ivback=findViewById(R.id.iv_back);
//        tv_toolbar = findViewById(R.id.tv_judul);
//        tv_toolbar.setText("Client");
//
//
//
//                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//                        filterList(query);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String newText) {
//                        // Action saat teks dalam SearchView berubah
//                        // Misalnya, lakukan update otomatis saat pengguna mengetik
//                        return false;
//                    }
//                });
//                break;
//        }
//        return true;
//    });
//        searchView = (SearchView) MenuItemCompat.getActionView(navClient.getMenu().findItem(R.id.search));
//
//        // Set up a listener for the search button
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Do something when the search button is pressed
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Do something when the user types in the search bar
//                return false;
//            }
//        });
//
//        // Set up a listener for the search icon
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Do something when the search icon is clicked
//            }
//        });
//
//        // Set up a listener for the clear button
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                // Do something when the clear button is clicked
//                return false;
//            }
//        });







//        FirebaseRecyclerOptions<ModelClient> options =
//                new FirebaseRecyclerOptions.Builder<ModelClient>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Client"), ModelClient.class)
//                        .build();

