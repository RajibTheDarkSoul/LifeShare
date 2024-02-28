package com.example.lifeshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class notificationFragment extends Fragment {
    List<Notifica> notificationList = new ArrayList<>();
    NotificationAdapter notificationAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    public notificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_notifica);
        shimmerFrameLayout.startShimmerAnimation();

        // Inside your fragment
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNotifica);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        notificationAdapter = new NotificationAdapter(notificationList, getContext());
        recyclerView.setAdapter(notificationAdapter);

        // Set up a listener to retrieve notifications
        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notifica notification = dataSnapshot.getValue(Notifica.class);
                    if (notification != null) {
                        checkValidNoti(notification);
                    }
                }

                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

        return view;
    }

    void checkValidNoti(Notifica notifica) {
        String postId = notifica.getPostId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String posterId = snapshot.child("userId").getValue(String.class);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (posterId != null && currentUser != null) {
                    String curUserId = currentUser.getUid();
                    if (posterId.equals(curUserId)) {
                        notificationList.add(notifica);
                        // Notify the adapter that the data has changed
                        notificationAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }
}
