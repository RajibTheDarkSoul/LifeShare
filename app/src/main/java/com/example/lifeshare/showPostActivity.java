package com.example.lifeshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class showPostActivity extends AppCompatActivity {

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
    Post current;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        String postId=getIntent().getStringExtra("postId");
 //       Log.d("Retrieved in activity: ",postId);

        userName = findViewById(R.id.profilepicCards);
        timeAgoCard = findViewById(R.id.timeAgoCards);
        bloodGroupCard = findViewById(R.id.bloodGroupCards);
        numOfBagCard = findViewById(R.id.NumOfBagCards);
        reqDateCard = findViewById(R.id.ReqDateCards);
        locationCard = findViewById(R.id.LocationCards);
        detailsCard = findViewById(R.id.detailsCards);
        interestButtonCard = findViewById(R.id.InterestButtonCards);
        mediCer=findViewById(R.id.imageOfDocCards);
        dp=findViewById(R.id.circleImageViews);

        loadData(postId);

        interestButtonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i=new Intent(showPostActivity.this,InterestedUsersActivity.class);
//                i.putExtra("postId",postId);
//                startActivity(i);

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(current.getUserId())) {
                    Intent i=new Intent(showPostActivity.this,InterestedUsersActivity.class);
                    i.putExtra("postId",current.getPostId());
                    startActivity(i);

                } else {

                    // Inside your Adapter class, where you handle the button click

                    String postId = current.getPostId(); // Replace with the actual method to get postId
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();// Get the current user's ID

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
                                                interestButtonCard.setText("Show Interest to donate");
                                                interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                                // User is now interested
                                                //Toast.makeText(context, "You are nno longer interested in this post", Toast.LENGTH_SHORT).show();
                                            } else {
                                                interestButtonCard.setText("Interested to donate");
                                                Drawable drawableRight = getResources().getDrawable(R.drawable.baseline_check_24); // Replace with your drawable resource ID

                                                // Set the drawable to the right of the button
                                                interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);


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
                                              //  Toast.makeText(context, "You are nw interested in this post", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                           // Toast.makeText(context, "Error toggling interest: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error
                           // Toast.makeText(context, "Error checking interest status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    // Handle logic for different user
                    // Example:
                    // Perform actions when a different user clicks the button

                }
            }
        });
    }

    void loadData(String postId)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("posts")
                .child(postId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post cur=snapshot.getValue(Post.class);
                current=cur;
                setData(cur);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void setData(Post currentPost)
    {
        timeAgoCard.setText(calculateTimeDifference(currentPost.getTimeAgo()));
        bloodGroupCard.setText(currentPost.getBloodGroup());
        numOfBagCard.setText(String.valueOf(currentPost.getNumOfBags()) + " Bags");
        reqDateCard.setText(currentPost.getReqDate());
        locationCard.setText(currentPost.getLocation());
        detailsCard.setText(currentPost.getDetails());

        if(!currentPost.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            interestButtonCard.setText("Show Interest to donate");

            getCurrentCondition(currentPost, new ConditionCallback() {
                @Override
                public void onConditionReceived(boolean condition) {
                    if (condition) {
                        interestButtonCard.setText("Interested to donate");
                        Drawable drawableRight = getResources().getDrawable(R.drawable.baseline_check_24);
                        interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
                    } else {
                        interestButtonCard.setText("Show Interest to donate");
                        interestButtonCard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                }
            });
        }


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentPost.getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userNa = snapshot.child("userName").getValue(String.class);
                    String dpIm = snapshot.child("dpImage").getValue(String.class);

                    // Set the user-specific data like profile pic and username
                    userName.setText(userNa);
                    Picasso.get().load(dpIm).into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(showPostActivity.this, "Error retrieving user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        Picasso.get().load(currentPost.getMedicalCertificationImage()).into(mediCer);


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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();// Get the current user's ID

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
                Toast.makeText(showPostActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }


}