package com.example.lifeshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class singleImageActivity extends AppCompatActivity {
    PhotoView image;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        url=getIntent().getStringExtra("urlImage");
        image=findViewById(R.id.photoView);
        if(url!=null)
        {
            Picasso.get().load(url).into(image);

        }

    }
}