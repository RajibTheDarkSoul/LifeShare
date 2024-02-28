package com.example.lifeshare;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifeshare.InterestedUser;
import com.example.lifeshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class InterestedUsersAdapter extends RecyclerView.Adapter<InterestedUsersAdapter.InterestedUserViewHolder> {

    private List<InterestedUser> interestedUsersList;
    private Context context;
  //  private OnItemClickListener itemClickListener;

    // Constructor
    public InterestedUsersAdapter(Context context,List<InterestedUser> interestedUsersList) {
        this.interestedUsersList = interestedUsersList;
        this.context=context;
       // this.itemClickListener = itemClickListener;
    }

    // ViewHolder class
    public static class InterestedUserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView profilePic;
        ImageButton callNow;
        ImageView messageNow;

        public InterestedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textViewUserName);
            profilePic = itemView.findViewById(R.id.imageViewProfilePic);
            callNow=itemView.findViewById(R.id.callButton);
            messageNow=itemView.findViewById(R.id.messageButton);
        }
    }

    @NonNull
    @Override
    public InterestedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interesteduser, parent, false);
        return new InterestedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestedUserViewHolder holder, int position) {
        InterestedUser currentUser = interestedUsersList.get(position);
        holder.userName.setText(currentUser.getUserName());

        // Load profile picture using Picasso (replace with your image loading library)
        Picasso.get().load(currentUser.getProfilePicUrl()).into(holder.profilePic);

        // Set an onClickListener for the entire item
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Get the clicked user
//                InterestedUser clickedUser = interestedUsersList.get(holder.getAdapterPosition());
//                // Notify the listener
//                itemClickListener.onItemClick(clickedUser);
//            }
//        });

        holder.callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(currentUser.getPhoneNumber());

            }
        });

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfile(currentUser);
            }
        });

        holder.messageNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserName(currentUser);
               // startChat(currentUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return interestedUsersList.size();
    }

    // Interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(InterestedUser interestedUser);
    }
    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    private void gotoProfile(InterestedUser cur)
    {
        Intent i=new Intent(context,profileActivity1.class);
        i.putExtra("userId",cur.getUserId());
        i.putExtra("userName",cur.getUserName());
        i.putExtra("profilepic",cur.getProfilePicUrl());
        i.putExtra("phoneNumber",cur.getPhoneNumber());
        context.startActivity(i);
    }

    void startChat(InterestedUser currentUser,String userId)
    {

        Intent intent = new Intent(context, MyChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("otherUserId", currentUser.getUserId());
        context.startActivity(intent);
    }
    void getUserName(InterestedUser currentUser)
    {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser cur=auth.getCurrentUser();

       // DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(cur.getUid());
        startChat(currentUser,cur.getUid());
    }
}
