package com.example.lifeshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

// InterestedUsersActivity.java
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InterestedUsersActivity extends AppCompatActivity implements InterestedUsersAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    private InterestedUsersAdapter adapter;
    private List<InterestedUser> interestedUsersList;
    private DatabaseReference interestedUsersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_users);
        shimmerFrameLayout=findViewById(R.id.shimmer_interested);
        shimmerFrameLayout.startShimmerAnimation();

        // Initialize RecyclerView and its layout manager
        recyclerView = findViewById(R.id.recyclerViewInterestedUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list to hold interested users
        interestedUsersList = new ArrayList<>();

        // Initialize the adapter with the list and the click listener
        adapter = new InterestedUsersAdapter(this,interestedUsersList);
        recyclerView.setAdapter(adapter);

        // Get postId from the intent
        String postId = getIntent().getStringExtra("postId");

        // Initialize Firebase
        interestedUsersRef = FirebaseDatabase.getInstance().getReference("interested").child(postId);

        // Retrieve interested users from Firebase
        retrieveInterestedUsers(postId);
    }

    private void retrieveInterestedUsers(String postId) {
        interestedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                interestedUsersList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    Boolean isInterested = userSnapshot.getValue(Boolean.class);
                    if (isInterested != null && isInterested) {
                        // If the user is interested, fetch user details from Users node
                        fetchUserDetails(userId);
                    }
                }

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchUserDetails(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("userName").getValue(String.class);
                    String profilePicUrl = snapshot.child("dpImage").getValue(String.class);
                    String phone=snapshot.child("phoneNumber").getValue(String.class);

                    InterestedUser interestedUser = new InterestedUser(userId, userName, profilePicUrl,phone);
                    interestedUsersList.add(interestedUser);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onItemClick(InterestedUser interestedUser) {
        // Handle item click, if needed
    }
}
