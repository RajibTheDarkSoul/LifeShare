package com.example.lifeshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class signup2 extends AppCompatActivity implements View.OnClickListener {
    String upazilla,district,division,name,email,phone,bloodType,dDay,dMonth,dYear,password;
    Uri dpUri,NID,Certi;
    ImageView nidImage,certiImage;
    boolean imageControl=false;
    boolean nidControl=false;
    boolean certiControl=false;
    Button register;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        // In ActivityB
        Intent intent = getIntent();
        retrieveData(intent);

        nidImage=findViewById(R.id.NIDImage);
        certiImage=findViewById(R.id.certiImage);
        register=findViewById(R.id.registerButton);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        progressBar=findViewById(R.id.progressButton4);

        nidImage.setOnClickListener(this);
        certiImage.setOnClickListener(this);
        register.setOnClickListener(this);

    }
    void retrieveData(Intent intent)
    {
        // Retrieve data from the intent
        upazilla = intent.getStringExtra("EXTRA_UPAZILLA");
        district = intent.getStringExtra("EXTRA_DISTRICT");
        division = intent.getStringExtra("EXTRA_DIVISION");
        name = intent.getStringExtra("EXTRA_NAME");
        email = intent.getStringExtra("EXTRA_EMAIL");
        phone = intent.getStringExtra("EXTRA_PHONE");
        bloodType = intent.getStringExtra("EXTRA_BLOOD_TYPE");
        dDay = intent.getStringExtra("EXTRA_D_DAY");
        dMonth = intent.getStringExtra("EXTRA_D_MONTH");
        dYear = intent.getStringExtra("EXTRA_D_YEAR");
        password = intent.getStringExtra("EXTRA_PASSWORD");
        imageControl=intent.getBooleanExtra("EXTRA_CONTROL",false);

        // Retrieve the image Uri from the intent
        String uriString = intent.getStringExtra("EXTRA_IMAGE_URI");

        // Convert the string back to a Uri
        dpUri = Uri.parse(uriString);

        // Now you can use these variables as needed

    }

    public void CertiChooser()
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1010);
    }

    public void NIDChooser()
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1011);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1011 && resultCode==RESULT_OK && data!=null)
        {
            NID=data.getData();
            Picasso.get().load(NID).into(nidImage);
            nidControl=true;


        }
        else
        {
            nidControl=false;
        }

        if(requestCode==1010 && resultCode==RESULT_OK && data!=null)
        {
            Certi=data.getData();
            Picasso.get().load(Certi).into(certiImage);
            certiControl=true;


        }
//        else
//        {
//            certiControl=false;
//        }


    }


    //After clicking register button, authenticating using Firebase
    void signup(String email,String pass,String userName)
    {
        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            reference.child("Users").child(auth.getUid())
                                    .child("userName").setValue(userName);

                            reference.child("Address").child(auth.getUid())
                                    .child("upazilla").setValue(upazilla);

                            reference.child("Address").child(auth.getUid())
                                    .child("district").setValue(district);

//                            reference.child("Address").child(auth.getUid())
//                                    .child("division").setValue(division);

                            reference.child("Users").child(auth.getUid())
                                    .child("bloodType").setValue(bloodType);
                            reference.child("Users").child(auth.getUid())
                                    .child("phoneNumber").setValue(phone);

                            if(!(dDay==null || dMonth==null || dYear==null))
                            {
                                reference.child("LastDonated").child(auth.getUid())
                                        .child("Day").setValue(dDay);

                                reference.child("LastDonated").child(auth.getUid())
                                        .child("Month").setValue(dMonth);

                                reference.child("LastDonated").child(auth.getUid())
                                        .child("Year").setValue(dYear);
                            }
                            else
                            {
                                reference.child("LastDonated").child(auth.getUid())
                                        .child("Day").setValue("null");

                                reference.child("LastDonated").child(auth.getUid())
                                        .child("Month").setValue("null");

                                reference.child("LastDonated").child(auth.getUid())
                                        .child("Year").setValue("null");

                            }


                            if(imageControl)
                            {
                                UUID dpID=UUID.randomUUID();
                                String imageName="image/"+dpID+".jpg";

                                storageReference.child(imageName).putFile(dpUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference myStorageRef= firebaseStorage.getReference(imageName);
                                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String dpPath=uri.toString();

                                                        reference.child("Users").child(auth.getUid())
                                                                .child("dpImage").setValue(dpPath)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(signup2.this, "DP upload successful.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(signup2.this, "Dp Upload failed."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                            }

                            else {
                                reference.child("Users").child(auth.getUid())
                                        .child("dpImage").setValue("null");
                            }


                            if(nidControl)
                            {
                                UUID nidID=UUID.randomUUID();
                                String imageName="image/"+nidID+".jpg";

                                storageReference.child(imageName).putFile(NID)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference myStorageRef= firebaseStorage.getReference(imageName);
                                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String nidPath=uri.toString();

                                                        reference.child("Users").child(auth.getUid())
                                                                .child("nidImage").setValue(nidPath)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(signup2.this, "NID upload successful.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(signup2.this, "NID Upload failed."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                            }

                            else {
                                reference.child("Users").child(auth.getUid())
                                        .child("nidImage").setValue("null");
                            }


                            if(certiControl)
                            {
                                UUID certiID=UUID.randomUUID();
                                String imageName="image/"+certiID+".jpg";

                                storageReference.child(imageName).putFile(Certi)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference myStorageRef= firebaseStorage.getReference(imageName);
                                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String certiPath=uri.toString();

                                                        reference.child("Users").child(auth.getUid())
                                                                .child("certiImage").setValue(certiPath)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(signup2.this, "Certificate upload successful.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(signup2.this, "Certificate Upload failed."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                            }

                            else {
                                reference.child("Users").child(auth.getUid())
                                        .child("certiImage").setValue("null");
                            }

                            //Going to main activity.
                            Intent intent=new Intent(signup2.this,MainActivity.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                            finish();

                            progressBar.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);

                        }



                        else {
                            progressBar.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);
                            Toast.makeText(signup2.this, "Couldn't create account."+task.getException().getMessage()
                                    .toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.NIDImage)
        {
            NIDChooser();
        }
        if (view.getId()==R.id.certiImage)
        {
            CertiChooser();
        }
        if(view.getId()==R.id.registerButton)
        {
            signup(email,password,name);
        }
    }


}