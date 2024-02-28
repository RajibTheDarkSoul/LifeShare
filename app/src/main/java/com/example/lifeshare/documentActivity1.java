package com.example.lifeshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class documentActivity1 extends AppCompatActivity {
    String userId;
    String nidUrl,DocUrl;
    ImageView nid,doc;
    TextView nd,dc;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document1);

        userId=getIntent().getStringExtra("userId");
        nid=findViewById(R.id.nidCardD);
        doc=findViewById(R.id.mediDocD);
        dc=findViewById(R.id.textdc);
        nd=findViewById(R.id.textnd);

        loadData();

        nid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(documentActivity1.this,singleImageActivity.class);
                i.putExtra("urlImage",nidUrl);
                startActivity(i);
            }
        });

        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(documentActivity1.this,singleImageActivity.class);
                i.putExtra("urlImage",DocUrl);
                startActivity(i);
            }
        });
    }

    void loadData()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                nidUrl=snapshot.child("nidImage").getValue(String.class);
                DocUrl=snapshot.child("certiImage").getValue(String.class);
                if(nidUrl!=null)
                {
                    Picasso.get().load(nidUrl).into(nid);
                }
                else {
                    nd.setVisibility(View.GONE);
                    nid.setVisibility(View.GONE);
                }
                if(DocUrl!=null)
                {
                    Picasso.get().load(DocUrl).into(doc);
                }
                else {
                    dc.setVisibility(View.GONE);
                    doc.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}