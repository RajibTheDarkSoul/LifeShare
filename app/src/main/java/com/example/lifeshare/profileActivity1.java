package com.example.lifeshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity1 extends AppCompatActivity {
    String username;
    String phoneNumber;
    String profileUrl;
    String userId;
    CircleImageView profile;
    ImageView nidBanner;
    TextView bloodGroup;
    Button call,documentView,message;
    TextView address,user;
    int track=0;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile1);

        userId=getIntent().getStringExtra("userId");
        username=getIntent().getStringExtra("userName");
        phoneNumber=getIntent().getStringExtra("phoneNumber");
        profileUrl=getIntent().getStringExtra("profilepic");

        if (getIntent().getStringExtra("track")!=null)
        {
            track=1;
        }



        user=findViewById(R.id.userName);
        profile=findViewById(R.id.profilepic);
        nidBanner=findViewById(R.id.Nidbanner);
        bloodGroup=findViewById(R.id.bloodGroupU);
        call=findViewById(R.id.callButtonU);
        address=findViewById(R.id.addressU);
        documentView=findViewById(R.id.Documents);
        message=findViewById(R.id.MessageButtonN);

        if(track==1)
        {
            documentView.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
        }

        user.setText(username);
        Picasso.get().load(profileUrl).into(profile);


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(phoneNumber);
            }
        });

        documentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDocuments();
            }
        });

        loadData();
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(profileActivity1.this,MyChatActivity.class);
                intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("otherUserId",userId);
                startActivity(intent);
            }
        });


    }

    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    void loadData()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bloodGroup.setText(snapshot.child("bloodType").getValue(String.class));
                String l=snapshot.child("certiImage").getValue(String.class);
                if (l!=null)
                {
                    Picasso.get().load(l).into(nidBanner);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference add=FirebaseDatabase.getInstance().getReference("Address").child(userId);
        add.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String upa=snapshot.child("upazilla").getValue(String.class);
                String dis=snapshot.child("district").getValue(String.class);
                address.setText(upa+", "+dis);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void viewDocuments()
    {
        Intent i=new Intent(profileActivity1.this,documentActivity1.class);
        i.putExtra("userId",userId);
        startActivity(i);
    }
}