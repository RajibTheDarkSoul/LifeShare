package com.example.lifeshare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

   // List<User> userList;
   List<ChatMessage> userList;

    String userName;
    Context mContext;
    String imageUrl;

    FirebaseDatabase database;
    DatabaseReference reference;

    public UsersAdapter(List<ChatMessage> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage currentUser = userList.get(position);
        getUserData(holder,currentUser);

      //  holder.textViewUsers.setText(currentUser.getUserName());

       // String imageUrl = currentUser.getImage();
//        if (imageUrl.equals("null")) {
//            holder.imageViewUsers.setImageResource(R.drawable.profile1);
//        if (imageUrl==null) {
//            holder.imageViewUsers.setImageResource(R.drawable.profile1);
//        } else {
//            Picasso.get().load(imageUrl).into(holder.imageViewUsers);
//        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyChatActivity.class);
                intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("otherUserId", currentUser.getOtherUserId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewUsers;
        private CircleImageView imageViewUsers;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsers = itemView.findViewById(R.id.textviewUserName);
            imageViewUsers = itemView.findViewById(R.id.imageViewUser);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }

    void getUserData(ViewHolder holder,ChatMessage user)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(user.getOtherUserId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName=snapshot.child("userName").getValue(String.class);
                imageUrl=snapshot.child("dpImage").getValue(String.class);

                holder.textViewUsers.setText(userName);
                Picasso.get().load(imageUrl).into(holder.imageViewUsers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


