package com.example.lifeshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyChatActivity extends AppCompatActivity {
    private ImageView imageViewBack;
    private TextView textViewChat;
    private EditText editTextMessage;
    private FloatingActionButton fab;
    private RecyclerView rvChat;
    ImageButton callNow;

    String userId,otherUserId;
    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<ModelClass> list;
    CircleImageView dpPic;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);

        imageViewBack=findViewById(R.id.imageViewBack);
        textViewChat=findViewById(R.id.textViewChat);
        editTextMessage=findViewById(R.id.editTextMessage);
        fab=findViewById(R.id.fab);
        rvChat=findViewById(R.id.rvChat);
        dpPic=findViewById(R.id.dpPic);
        callNow=findViewById(R.id.callButtonC);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        reference= database.getReference();



        userId=getIntent().getStringExtra("userId");
        otherUserId=getIntent().getStringExtra("otherUserId");

       // textViewChat.setText(otherName);
        setTextViewChat(otherUserId);
        loadImage();


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MyChatActivity.this,MainActivity.class);
                startActivity(i);
                finish();;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=editTextMessage.getText().toString();

                if(!message.equals(""))
                {
                    sendMessage(message);
                    editTextMessage.setText("");
                }
            }
        });
        dpPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfile();
            }
        });
        
        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall();
            }
        });

        getMessage();
    }

    private void getMessage() {
        reference.child("Messages").child(userId)
                .child(otherUserId).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ModelClass modelClass=snapshot.getValue(ModelClass.class);
                        list.add(modelClass);
                        adapter.notifyDataSetChanged();

                        rvChat.scrollToPosition(list.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        adapter=new MessageAdapter(list,userId);
        rvChat.setAdapter(adapter);

    }


//    private void sendMessage(String message) {
//
//        String key=reference.child("Messages").child(userName)
//                .child(otherName).push().getKey();
//
//        Map<String,Object>messageMap=new HashMap<>();
//        messageMap.put("message",message);
//        messageMap.put("from",userName);
//        reference.child("Messages").child(userName)
//                .child(otherName).child(key).setValue(messageMap)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        if(task.isSuccessful())
//                        {
//                            Toast.makeText(MyChatActivity.this, "Message send successful to "+otherName, Toast.LENGTH_SHORT).show();
//                            reference.child("Messages").child(otherName).child(userName).child(key)
//                                    .setValue(messageMap);
//                        }
//                        else
//                        {
//
//                        }
//
//                    }
//                });
//
//    }

    private void sendMessage(String message) {
        String key = reference.child("Messages").child(userId).child(otherUserId).push().getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userId);

        reference.child("Messages").child(userId).child(otherUserId).child(key).setValue(messageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update the last message for the sender
                        //    updateLastMessage(userId, otherUserId, message);

                            // Update the last message for the receiver
                        //    updateLastMessage(otherUserId, userId, message);

                            Toast.makeText(MyChatActivity.this, "Message sent successfully ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyChatActivity.this, "Failed to sent message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        reference.child("Messages").child(otherUserId).child(userId).child(key).setValue(messageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update the last message for the sender
                            updateLastMessage(userId, otherUserId, message);

                            // Update the last message for the receiver
                            updateLastMessage(otherUserId, userId, message);

                            Toast.makeText(MyChatActivity.this, "Message delivered successfully ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyChatActivity.this, "Failed to deliver message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateLastMessage(String sender, String receiver, String message) {
        Map<String, Object> lastMessageMap = new HashMap<>();
        lastMessageMap.put("lastMessage", message);
        lastMessageMap.put("timestamp", ServerValue.TIMESTAMP);

        reference.child("LastMessages").child(sender).child(receiver).setValue(lastMessageMap);
        reference.child("LastMessages").child(receiver).child(sender).setValue(lastMessageMap);
    }

    void setTextViewChat(String otherUserId)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(otherUserId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usern=snapshot.child("userName").getValue(String.class);
                textViewChat.setText(usern);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadImage()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users")
                .child(otherUserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String l=snapshot.child("dpImage").getValue(String.class);
                Picasso.get().load(l).into(dpPic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void gotoProfile()
    {
        Intent i=new Intent(MyChatActivity.this,profileActivity1.class);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users")
                .child(otherUserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("userName").getValue(String.class);
                String num=snapshot.child("phoneNumber").getValue(String.class);
                String l=snapshot.child("dpImage").getValue(String.class);

                i.putExtra("userId",otherUserId);
                i.putExtra("userName",name);
                i.putExtra("phoneNumber",num);
                i.putExtra("profilepic",l);
                i.putExtra("track","chat");
                startActivity(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeCall() {
        FirebaseDatabase.getInstance().getReference("Users").child(otherUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String phoneNumber=snapshot.child("phoneNumber").getValue(String.class);
                        if(phoneNumber!=null) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MyChatActivity.this, "Can't make call. PhoneNumber is wrong.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyChatActivity.this, "Can't make call. Something is wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
                
       
    }
}