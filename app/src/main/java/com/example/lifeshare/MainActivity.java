package com.example.lifeshare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.example.lifeshare.databinding.ActivityMainBinding;
import com.example.lifeshare.R;
import com.example.lifeshare.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();

        //checking if clicked from any notification or not
        if (bundle != null) {
            String posi=bundle.getString("postId");

            if(posi!=null)
            {
                Intent i=new Intent(MainActivity.this,showPostActivity.class);
                i.putExtra("postId",posi);
                startActivity(i);
            }

        }



        FirebaseApp.initializeApp(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        FirebaseMessaging.getInstance().subscribeToTopic("abc");

        // Set default selected item
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        replaceFragment(new homeFragment());

        // Set listener for BottomNavigationView and showing corresponding fragment
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new homeFragment());
                    break;

                case R.id.search:
                    replaceFragment(new searchFragment());
                    break;

                case R.id.notifications:
                    replaceFragment(new notificationFragment());
                    break;

                case R.id.myprofile:
                    replaceFragment(new myProfileFragement());
                    break;

                case R.id.chattedUsers:
                    replaceFragment(new chatHistoryFragment());
                    break;
            }

            return true;
        });

        //Allowing notification
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();
                        // Save the FCM token to the database or perform any other action
                        saveTokenToDatabase(fcmToken);
                    }
                });
        subscribeToChannel("fcm_default_channel");


    }

    //Replacing with the corresponding fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Use addToBackStack to remember the previous fragment
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //Getting token, which will be used for sending push notifications

    private void saveTokenToDatabase(String token) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("tokens")
                    .child(userId)
                    .child("token")
                    .setValue(token);
        }
    }


    //funciton to subscribe or allowing notification
    @SuppressLint("NewApi")
    private void subscribeToChannel(String channelId) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.createNotificationChannel(new NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT));
    }


        //Forwarding to the notifying post
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String postId=intent.getStringExtra("postId");
        Log.d("onNewIntent: ","postdId is : "+postId);
        if(postId!=null)
        {
            Intent i=new Intent(MainActivity.this,showPostActivity.class);
            i.putExtra("postId",postId);
            startActivity(i);
        }
    }
}