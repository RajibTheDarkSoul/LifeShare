package com.example.lifeshare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class myProfileFragement extends Fragment {
    ImageButton logOut;

    String username,blood;
    String phoneNumber;
    String profileUrl;
    String userId;
    CircleImageView profile;
    ImageView nidBanner;
    TextView bloodGroup;;
    TextView address,user,phoneNum,timesNum;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser cur= auth.getCurrentUser();



    private Button createPostB;
    private RecyclerView recyclerViewPosts;
    private List<Post> postsList;
    private PostAdapter postAdapter;

    String uid = cur.getUid();



    public myProfileFragement() {
        // Required empty public constructor
    }

//38406E
    //55636E

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Initialize postsList and postAdapter
        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(postsList, requireContext(), uid); // Replace "currentUserId" with actual user ID

        // Set up Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("posts"); // Replace with your actual posts reference

        // Retrieve posts from Firebase
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear(); // Clear existing posts

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        if (post.getUserId().equals(uid)) {
                            postsList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_profile_fragement, container, false);

     //   createPostB = view.findViewById(R.id.buttonRequestBlood);
        recyclerViewPosts = view.findViewById(R.id.recyclerViews);
        profile=view.findViewById(R.id.profilepics);
        bloodGroup=view.findViewById(R.id.bloodGroupUs);
        address=view.findViewById(R.id.addressUs);
        phoneNum=view.findViewById(R.id.phoneNumber);
        timesNum=view.findViewById(R.id.donatedTimes);
        user=view.findViewById(R.id.userNames);
        logOut=view.findViewById(R.id.logoutButton);

        // Set up RecyclerView
        // recyclerViewPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPosts.setLayoutManager(layoutManager);
        recyclerViewPosts.setAdapter(postAdapter);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(cur.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dpUrl=snapshot.child("dpImage").getValue(String.class);
                Picasso.get().load(dpUrl).into(profile);
                String bg=snapshot.child("bloodType").getValue(String.class);
                bloodGroup.setText(bg);
                String un=snapshot.child("userName").getValue(String.class);
                user.setText(un);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    void logOutUser()
    {
        auth.signOut();

        Intent i=new Intent(getContext(),SignInActivity.class);
        startActivity(i);
        getActivity().finish();

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are really want to Sign Out from LifeShare?");

        // Positive Button
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Code to be executed when the positive button is clicked
                // For example, you can perform some action or dismiss the dialog
                logOutUser();
                dialogInterface.dismiss();
            }
        });

        // Negative Button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Code to be executed when the negative button is clicked
                // For example, you can cancel the operation or dismiss the dialog
                dialogInterface.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}