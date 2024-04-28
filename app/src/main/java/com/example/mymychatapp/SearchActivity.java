package com.example.mymychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mymychatapp.adapter.SearchUserAdapter;
import com.example.mymychatapp.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchBtn, backBtn;
    RecyclerView recyclerView;

    SearchUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchUsernameInput);
        searchBtn = findViewById(R.id.searchUserBtn);
        recyclerView = findViewById(R.id.searchUser_recyclerV);
        backBtn = findViewById(R.id.backBtn);

        searchInput.requestFocus();

        backBtn.setOnClickListener((v -> {
            onBackPressed();
        }));
        searchBtn.setOnClickListener(v-> {
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty()|| searchTerm.length()<3){
                searchInput.setError("The username is invalid");
                return;
            }
            setUpSearchRV(searchTerm);
        });
    }

    void setUpSearchRV(String searchTerm){

        //To search by username in Firebase
        Query query = Utility.allUserCollectionReference().whereGreaterThanOrEqualTo("username", searchTerm);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build();

        adapter = new SearchUserAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.startListening();
    }
}