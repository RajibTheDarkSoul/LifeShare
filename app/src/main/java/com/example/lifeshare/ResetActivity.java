package com.example.lifeshare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    Button reset;
    EditText resetMail;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        reset=findViewById(R.id.resetButton);
        resetMail=findViewById(R.id.resetmail);
        progressBar=findViewById(R.id.progressButton2);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                reset.setVisibility(View.GONE);
                sendMail();
            }
        });
    }

    void sendMail()
    {
        String mail=resetMail.getText().toString();
        if (mail.isEmpty())
        {
            resetMail.setError("Enter a valid email.");
            progressBar.setVisibility(View.GONE);
            reset.setVisibility(View.VISIBLE);
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(mail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "A reset email has been sent to your email.", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(ResetActivity.this,SignInActivity.class);
                        startActivity(i);
                        finish();
                    } else {

                        Toast.makeText(this, "Couldn't send reset link. "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        reset.setVisibility(View.VISIBLE);
                    }
                });

    }
}