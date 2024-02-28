package com.example.lifeshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email,password;
    MaterialButton login;
    TextView signUp,textReset,viewForgot;
    ProgressBar pb;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser cur;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String posi=bundle.getString("postId");

            if(posi!=null)
            {
                Intent i=new Intent(SignInActivity.this,showPostActivity.class);
                i.putExtra("postId",posi);
                startActivity(i);
            }

        }

        //Checking is already Signed in or not
        cur=auth.getCurrentUser();
        if(cur!=null)
        {
            Intent intent=new Intent(SignInActivity.this,MainActivity.class);
            //inent.putExtra(t"userName",userName);
            startActivity(intent);
            finish();
        }


        login=findViewById(R.id.resetButton);
        signUp=findViewById(R.id.SignUp);
        pb=findViewById(R.id.progressButton1);
        textReset=findViewById(R.id.reset);
        viewForgot=findViewById(R.id.textReset);

        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.resetmail);
        password=findViewById(R.id.loginpassword);



        //Trying to log in
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        textReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,ResetActivity.class);
                startActivity(intent);
            }
        });

    }


    public void login() {

        pb.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
        textReset.setVisibility(View.GONE);
        viewForgot.setVisibility(View.GONE);

        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        //collecting the password and email
        if (mail.isEmpty())
        {
            email.setError("Enter valid email.");

        }

        if (pass.isEmpty())
        {
            password.setError("Enter valid password.");

        }


        //Checking is the password and the email is valid or not
        if( pass == null || mail==null || pass.isEmpty() || mail.isEmpty())
        {
            pb.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);

            textReset.setVisibility(View.VISIBLE);
            viewForgot.setVisibility(View.VISIBLE);
            return;
        }

        //Authenticating the user
        mAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            Intent i=new Intent(SignInActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {

                            Toast.makeText(SignInActivity.this, "Unable to Sign In. "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                        }

                        pb.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);

                        textReset.setVisibility(View.VISIBLE);
                        viewForgot.setVisibility(View.VISIBLE);

                    }
                });
    }
}