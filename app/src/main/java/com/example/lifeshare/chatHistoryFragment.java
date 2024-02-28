package com.example.lifeshare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class chatHistoryFragment extends Fragment {
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView rv;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseDatabase database;

    String userName;
    List<User> list;
    List<ChatMessage>list2;
    UsersAdapter adapter;


    public chatHistoryFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat_history, container, false);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_chats);

        shimmerFrameLayout.startShimmerAnimation();

        rv=view.findViewById(R.id.recyclerViewHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        //Now have yo create adapter class
        list=new ArrayList<>();
        list2=new ArrayList<>();


        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        adapter=new UsersAdapter(list2,userName,getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        getUsers();
        rv.setAdapter(adapter);



//        reference.child("Users").child(user.getUid()).child("userName")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        userName=snapshot.getValue().toString();
//                        getUsers();
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        return view;
    }



        // ... existing code

        void getUsers() {
            DatabaseReference chatHistoryRef = database.getReference("LastMessages").child(user.getUid());


//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    list.clear(); // Clear the list before adding new entries
//
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        String otherUserId = userSnapshot.getKey();
//
//                        if (!otherUserId.equals(user.getUid())) {
//                            // Find the latest message timestamp
//                            long latestTimestamp = 0;
//                            for (DataSnapshot chatSnapshot : userSnapshot.getChildren()) {
//                                long timestamp = chatSnapshot.child("timestamp").getValue(Long.class);
//                                if (timestamp > latestTimestamp) {
//                                    latestTimestamp = timestamp;
//                                }
//                            }
//
//                            // Create a User object with user ID and latest timestamp
//                            User user = new User(otherUserId, latestTimestamp);
//
//                            // Add users with chat history to the list
//                            list.add(user);
//                        }
//                    }
//
//                    // Sort the list based on the latest message timestamp
//                    Collections.sort(list, (user1, user2) ->
//                            Long.compare(user2.getLatestTimestamp(), user1.getLatestTimestamp()));
//
//                    // Notify the adapter that the data set has changed
//                    adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle the error
//                }
            chatHistoryRef.addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
    list2.clear(); // Clear the list before adding new entries

//    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//        String otherUserId = userSnapshot.getKey();
//
//        if (!otherUserId.equals(user.getUid())) {
//            DatabaseReference lastMessageRef = userSnapshot.child(otherUserId).getRef();
//
//            lastMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot messageSnapshot) {
//                    // Find the latest message timestamp
//                    long latestTimestamp = 0;
//                    for (DataSnapshot chatSnapshot : messageSnapshot.getChildren()) {
//                        long timestamp = chatSnapshot.child("timestamp").getValue(Long.class);
//                        if (timestamp > latestTimestamp) {
//                            latestTimestamp = timestamp;
//                        }
//                    }
//
//                    // Create a User object with user ID and latest timestamp
//                    User user = new User(otherUserId, latestTimestamp);
//
//                    // Add users with chat history to the list
//                    list.add(user);
//
//                    // Sort the list based on the latest message timestamp
//                    Collections.sort(list, (user1, user2) ->
//                            Long.compare(user2.getTimestamp(), user1.getTimestamp()));
//
//                    // Set the adapter if it's not set
//                    if (adapter == null) {
//                        adapter = new UsersAdapter(list, userName, getContext());
//                        rv.setAdapter(adapter);
//                    } else {
//                        // Notify the adapter that the data set has changed
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle the error
//                }
//            });
//        }
//    }
    // Iterate through other users
    for (DataSnapshot otherUserSnapshot : snapshot.getChildren()) {
        String otherUserId = otherUserSnapshot.getKey();

        long timestamp = snapshot.child(otherUserId).child("timestamp").getValue(Long.class);
        String message = snapshot.child(otherUserId).child("message").getValue(String.class);

        // Create a ChatMessage object or process the data as needed
        ChatMessage chatMessage = new ChatMessage(timestamp, message,otherUserId);
        list2.add(chatMessage);

        // Iterate through messages of the other user
//        for (DataSnapshot messageSnapshot : otherUserSnapshot.getChildren()) {
//            String timestamp = messageSnapshot.child("timestamp").getValue(String.class);
//            String message = messageSnapshot.child("message").getValue(String.class);
//
//            // Create a ChatMessage object or process the data as needed
//            ChatMessage chatMessage = new ChatMessage(timestamp, message,otherUserId);
//            list2.add(chatMessage);
//
//
//            // Print or handle the data
////            System.out.println("User ID: " + otherUserId);
////            System.out.println("Timestamp: " + chatMessage.getTimestamp());
////            System.out.println("Message: " + chatMessage.getMessage());
//        }
    }
  //  Collections.sort(list2,);
//    students.sort(Comparator.comparing(Student::getGpa).reversed());
  //  list2.sort(Comparator.comparing(ChatMessage::getTimestamp));
    list2.sort(Comparator.comparing(ChatMessage::getTimestamp));
    adapter.notifyDataSetChanged();

    shimmerFrameLayout.stopShimmerAnimation();
    shimmerFrameLayout.setVisibility(View.GONE);
    rv.setVisibility(View.VISIBLE);
}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });
        }

        // ... rest of the code


    void getOthers(User user)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(user.getUserId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.setUserName(snapshot.child("userName").getValue(String.class));
                user.setImage(snapshot.child("dpImage").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}






