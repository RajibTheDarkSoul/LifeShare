package com.example.lifeshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {
//    private Button createPostB;
    ShimmerFrameLayout shimmerFrameLayout;
private FloatingActionButton createPostB;
    private RecyclerView recyclerViewPosts;
    private List<Post> postsList;
    private PostAdapter postAdapter;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser cur= auth.getCurrentUser();
    String uid = cur.getUid();

    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Replace "currentUserId" with actual user ID

         // Replace with your actual posts reference


    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        createPostB = view.findViewById(R.id.buttonRequestBlood);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_posts);

        // Initialize postsList and postAdapter
        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(postsList, requireContext(), uid);

        //Starting shimmer animation
        shimmerFrameLayout.startShimmerAnimation();

        // Set up RecyclerView
       // recyclerViewPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPosts.setLayoutManager(layoutManager);
        recyclerViewPosts.setAdapter(postAdapter);

        // Set up Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("posts");

        // Retrieve posts from Firebase
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear(); // Clear existing posts

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postsList.add(post);
                    }
                }

                //Stopping animationd and showing the posts
                postAdapter.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerViewPosts.setVisibility(View.VISIBLE);
                createPostB.setVisibility(View.VISIBLE );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });


        createPostB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), createPost.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
