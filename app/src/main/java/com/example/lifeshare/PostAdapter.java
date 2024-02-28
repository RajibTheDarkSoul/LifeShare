package com.example.lifeshare;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postsList;
    boolean curCondition=false;
    private Context context;
    private String currentUserId;
    String usr,dpu;// Set this value when initializing the adapter

    // Constructor
    public PostAdapter(List<Post> postsList, Context context, String currentUserId) {
        this.postsList = postsList;
        this.context = context;
        this.currentUserId = currentUserId;
    }



    // Inner ViewHolder class
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView timeAgoCard;
        TextView bloodGroupCard;
        TextView numOfBagCard;
        TextView reqDateCard;
        TextView locationCard;
        TextView detailsCard;
        Button interestButtonCard;
        RoundedImageView mediCer;
        CircleImageView dp;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.profilepicCard);
            timeAgoCard = itemView.findViewById(R.id.timeAgoCard);
            bloodGroupCard = itemView.findViewById(R.id.bloodGroupCard);
            numOfBagCard = itemView.findViewById(R.id.NumOfBagCard);
            reqDateCard = itemView.findViewById(R.id.ReqDateCard);
            locationCard = itemView.findViewById(R.id.LocationCard);
            detailsCard = itemView.findViewById(R.id.detailsCard);
            interestButtonCard = itemView.findViewById(R.id.InterestButtonCard);
            mediCer=itemView.findViewById(R.id.imageOfDocCard);
            dp=itemView.findViewById(R.id.circleImageView);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postcardview, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = postsList.get(position);

        // Set the user-specific data like profile pic and username
        // Example:
        // holder.profilepicCard.setImageResource(R.drawable.sample_profile_pic);
        // holder.profilepicCard.setText(currentPost.getUserName());


        holder.timeAgoCard.setText(calculateTimeDifference(currentPost.getTimeAgo()));
        holder.bloodGroupCard.setText(currentPost.getBloodGroup());
        holder.numOfBagCard.setText(String.valueOf(currentPost.getNumOfBags()) + " Bags");
        holder.reqDateCard.setText(currentPost.getReqDate());
        holder.locationCard.setText(currentPost.getLocation());
        holder.detailsCard.setText(currentPost.getDetails());
        //   holder.mediCer.setImageURI(Uri.parse(currentPost.getMedicalCertificationImage()));

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentPost.getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userNa = snapshot.child("userName").getValue(String.class);
                    String dpIm = snapshot.child("dpImage").getValue(String.class);

                    usr=userNa;
                    dpu=dpIm;

                    // Set the user-specific data like profile pic and username
                    holder.userName.setText(userNa);
                    Picasso.get().load(dpIm).into(holder.dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(context, "Error retrieving user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        Picasso.get().load(currentPost.getMedicalCertificationImage()).into(holder.mediCer);
//        holder.mediCer.setIm


        // Update the interestButtonCard text based on the current user's ID and the post's user ID
        if (currentUserId.equals(currentPost.getUserId())) {
            // Same user, show "View Interested Donors"
            holder.interestButtonCard.setText("View Interested Donors");
            Log.d("Same user", " equal issue");

            // Set an onClickListener for the button
//            holder.interestButtonCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Open an activity for the same user
//                    // Example:
//                    // Intent intent = new Intent(context, YourActivity.class);
//                    // context.startActivity(intent);
//                }
//            });
        } else {
            Log.d("Not same user", "Post:" + currentPost.getUserId() + " Reading: " + currentUserId);
            holder.interestButtonCard.setText("Show Interest to donate");
            // Inside onBindViewHolder
            getCurrentCondition(currentPost, new ConditionCallback() {
                @Override
                public void onConditionReceived(boolean condition) {
                    if (condition) {
                        holder.interestButtonCard.setText("Interested to donate");
                        Drawable drawableRight = context.getResources().getDrawable(R.drawable.baseline_check_24);
                        holder.interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
                    } else {
                        holder.interestButtonCard.setText("Show Interest to donate");
                        holder.interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                }
            });


            // Different user, show "Interested to Donate"


            // Set an onClickListener for the button

        }

        holder.interestButtonCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (currentUserId.equals(currentPost.getUserId())) {
                    Intent i=new Intent(context,InterestedUsersActivity.class);
                    i.putExtra("postId",currentPost.getPostId());
                    context.startActivity(i);

                } else {

                    // Inside your Adapter class, where you handle the button click

                    String postId = currentPost.getPostId(); // Replace with the actual method to get postId
                    String userId = currentUserId;// Get the current user's ID

                    DatabaseReference interestedUsersRef = FirebaseDatabase.getInstance().getReference("interested")
                            .child(postId)
                            .child(userId);

                    // Check if the user has already shown interest
                    interestedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isInterested = snapshot.exists() && snapshot.getValue(Boolean.class);

                            // Toggle the interest status (true/false)
                            interestedUsersRef.setValue(!isInterested)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Successfully toggled interest status
                                            if (isInterested) {
                                                holder.interestButtonCard.setText("Show Interest to donate");
                                                holder.interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                                // User is now interested
                                                Toast.makeText(context, "You are nno longer interested in this post", Toast.LENGTH_SHORT).show();
                                            } else {
                                                holder.interestButtonCard.setText("Interested to donate");
                                                Drawable drawableRight = context.getResources().getDrawable(R.drawable.baseline_check_24); // Replace with your drawable resource ID

                                                // Set the drawable to the right of the button
                                                holder.interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);



                                                // Create a notification entry
                                                DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

                                                Notifica notification = new Notifica();
                                                
                                                notification.setSenderUserId(userId);
                                                notification.setPostId(postId);
                                                notification.setTimestamp(System.currentTimeMillis());

                                                // Push the new notification to the "notifications" node with a unique ID
                                                DatabaseReference newNotificationRef = notificationsRef.push();
                                                newNotificationRef.setValue(notification);



                                                // User is no longer interested
                                                Toast.makeText(context, "You are now interested in this post", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                            Toast.makeText(context, "Error toggling interest: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error
                            Toast.makeText(context, "Error checking interest status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    // Handle logic for different user
                    // Example:
                    // Perform actions when a different user clicks the button

                }
            }
        });


        holder.mediCer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,singleImageActivity.class);
                i.putExtra("urlImage",currentPost.getMedicalCertificationImage());
                context.startActivity(i);
            }
        });

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitUser(currentPost);
            }
        });

        holder.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitUser(currentPost);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }









        public  String calculateTimeDifference(String postTime) {
            try {
                long postTimeMillis = Long.parseLong(postTime);
                long currentTimeMillis = System.currentTimeMillis();

                long diffInMillis = currentTimeMillis - postTimeMillis;

                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
                long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                if (minutes==0)
                {
                    return "just now";
                }
                else if (minutes < 60) {
                    return minutes + " minutes ago";
                } else if (hours < 24) {
                    return hours + " hours ago";
                } else {
                    return days + " days ago";
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "Invalid timestamp format";
            }
        }

        void getCurrentCondition(Post currentPost, ConditionCallback callback) {
        String postId = currentPost.getPostId(); // Replace with the actual method to get postId
        String userId = currentUserId;// Get the current user's ID

        DatabaseReference interestedUsersRef = FirebaseDatabase.getInstance().getReference("interested")
                .child(postId)
                .child(userId);

        // Check if the user has already shown interest
        interestedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean condition = snapshot.exists() && snapshot.getValue(Boolean.class);

                // Callback with the result
                callback.onConditionReceived(condition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Toast.makeText(context, "Error checking interest status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void visitUser(Post currentPost)
    {
        Intent i=new Intent(context,profileActivity1.class);
        i.putExtra("userId",currentPost.getUserId());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentPost.getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                       if (snapshot.exists()) {
                                                           String userNa = snapshot.child("userName").getValue(String.class);
                                                           String dpIm = snapshot.child("dpImage").getValue(String.class);

                                                           i.putExtra("userName",userNa);
                                                           i.putExtra("profilepic",dpIm);

                                                           i.putExtra("track","1");
                                                           context.startActivity(i);
                                                       }
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError error) {
                                                       Toast.makeText(context, "Can't visit to the user profile.", Toast.LENGTH_SHORT).show();
                                                   }
                                               });
        
        
        
      //  i.putExtra("phoneNumber",cur.getPhoneNumber());

    }
    }



