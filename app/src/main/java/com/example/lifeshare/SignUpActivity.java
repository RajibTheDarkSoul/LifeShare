package com.example.lifeshare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dubd.bdlocationchooser.BdLocationChooser;
import com.dubd.bdlocationchooser.District;
import com.dubd.bdlocationchooser.Division;
import com.dubd.bdlocationchooser.LocationChooseListener;
import com.dubd.bdlocationchooser.Upazila;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    TextView address,Upazilla,District,Division;
    String upazillaS,districtS,divisionS,Name,Email,Phone,BloodType,DDay,Dmonth,Dyear,Password;
    String tmp="";
    EditText day,month,year,name,email,phone,password;
    LinearLayout lAddress;
    Spinner spinner;
    Button registerNext;
    CircleImageView profilePhoto;
    Uri imageUri;
    boolean imageControl=false;

    ArrayList<String> bloodTypes = new ArrayList<>();
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser cur;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        if(getIntent()!=null){
            Log.d("checking onclick: ","checking");
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
               String postId= bundle.getString("postId");
               Log.d("Retrieved apostid from noti: ",postId);
                Intent i=new Intent(SignUpActivity.this,showPostActivity.class);
                i.putExtra("postId",postId);
                Log.d("Starting activity","satrting showpost");
                startActivity(i);
                finish();

            }
        }



        //checking if already logged in
        cur=auth.getCurrentUser();
        if(cur!=null)
        {
            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
            //inent.putExtra(t"userName",userName);
            startActivity(intent);
            finish();
        }

        address=findViewById(R.id.address);
        Upazilla=findViewById(R.id.upazilla);
        District=findViewById(R.id.district);
        Division=findViewById(R.id.division);
        lAddress=findViewById(R.id.linearAddress);
        spinner=findViewById(R.id.spinner);
        day=findViewById(R.id.editTextDate);
        month=findViewById(R.id.editTextMonth);
        year=findViewById(R.id.editTextYear);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        profilePhoto=findViewById(R.id.profilePhoto);
        registerNext=findViewById(R.id.registerButtonNext);
        password=findViewById(R.id.password);


        setEditTextFilter();


        // Adding blood types to the list
        bloodTypes.add("A+");
        bloodTypes.add("A-");
        bloodTypes.add("B+");
        bloodTypes.add("B-");
        bloodTypes.add("AB+");
        bloodTypes.add("AB-");
        bloodTypes.add("O+");
        bloodTypes.add("O-");
        getBLood();

        //Adding list to the spinner
        @SuppressLint("ResourceType") ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (SignUpActivity.this,R.layout.sample_spinner,R.id.textViewSpinner,bloodTypes);

        spinner.setAdapter(adapter);


        address.setOnClickListener(this);
        profilePhoto.setOnClickListener(this);
        registerNext.setOnClickListener(this);
    }

    private void setEditTextFilter() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(2);
        day.setFilters(filters);
        month.setFilters(filters);
        year.setFilters(filters);
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.address)
        {

            getUserLocation();

        }

        if(view.getId()==R.id.registerButtonNext)
        {

            getUserData();
            if(!isValidData())
            {
                Log.d("SignUpActivity", "Before starting second activity");
                return;
            }

            if(imageUri==null)
            {
                Toast.makeText(this, "Select a profile picture.", Toast.LENGTH_SHORT).show();
                return;
            }



            Intent intent=new Intent(SignUpActivity.this,signup2.class);

            // Add data to the intent
            intent.putExtra("EXTRA_UPAZILLA", upazillaS);
            intent.putExtra("EXTRA_DISTRICT", districtS);
            intent.putExtra("EXTRA_DIVISION", divisionS);
            intent.putExtra("EXTRA_NAME", Name);
            intent.putExtra("EXTRA_EMAIL", Email);
            intent.putExtra("EXTRA_PHONE", Phone);
            intent.putExtra("EXTRA_BLOOD_TYPE", BloodType);
            intent.putExtra("EXTRA_D_DAY", DDay);
            intent.putExtra("EXTRA_D_MONTH", Dmonth);
            intent.putExtra("EXTRA_D_YEAR", Dyear);
            intent.putExtra("EXTRA_PASSWORD", Password);
            intent.putExtra("EXTRA_CONTROL",imageControl);

            if(imageControl){
                intent.putExtra("EXTRA_IMAGE_URI", imageUri.toString());
            }


            // Start the second activity
            startActivity(intent);


        }


        if(view.getId()==R.id.profilePhoto)
        {
            imageChooser();
        }


    }

    private boolean isValidData() {
        boolean isO=true;

//        if(DDay.equals("") || Dmonth.equals("")||Dyear.equals(""))
//        {
//            isOK=false;
//            year.setError("Enter");
//
//        }
        if(upazillaS==null || upazillaS.equals("")|| districtS.equals("")||divisionS.equals(""))
        {
            isO=false;
            address.setError("Click!");

        }

        if(Phone.length()<11)
        {
            isO=false;
            phone.setError("Enter a valid phone number.");
            phone.requestFocus();
        }

        if(BloodType.equals(""))
        {
            isO=false;

        }



        if(!isValidPassword(Password))
        {
            isO=false;
            password.setError("Enter a valid Password.");
            password.requestFocus();
        }

        if(Phone.equals(""))
        {
            isO=false;
            phone.setError("Enter a number.");
            phone.requestFocus();
        }

        if(!isValidEmail(Email))
        {
            isO=false;
            email.setError("Enter a valid Email.");
            email.requestFocus();
        }


        if(Name.equals(""))
        {
            name.setError("Enter a name.");
            isO=false;
            name.requestFocus();
        }







        return isO;

    }

    private void getUserData() {
        Name=name.getText().toString();
        Email=email.getText().toString();
        Phone=phone.getText().toString();

        Log.d("GetuserData", "Before getBLood function");
        BloodType=getBLood();
        DDay=day.getText().toString();
        Dmonth=month.getText().toString();
        Dyear=year.getText().toString();
        Password=password.getText().toString();

    }

    private String getBLood() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tmp = bloodTypes.get(i);
                Log.d("Spinner", "ItemSelected: "+bloodTypes.get(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tmp=bloodTypes.get(0);
            }
        });
        return  tmp ;
    }


    //Using BDLocationChooser library to get the address
    private void getUserLocation() {

        new BdLocationChooser.Create(this)
                .setPickerTitle("Choose a location")
                .setLanguage("english")     // Default Bangla
                .setListener(new LocationChooseListener() {
                    @Override
                    public void onLocationChoose(@NonNull Division division, @NonNull District district, @NonNull Upazila upazila) {
                        lAddress.setVisibility(View.VISIBLE);
                        Division.setText(division.getName());
                        District.setText(district.getName());
                        Upazilla.setText(upazila.getName());

                        upazillaS=upazila.getName();
                        districtS=district.getName();
                        divisionS=division.getName();
                        Toast.makeText(SignUpActivity.this, division.getName()+","+district.getName()+","+upazila.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled() {

                    }
                }).showDialog();
    }

    public boolean isValidEmail(String email) {
        if (email=="")
        {
            return false;
        }
        // Use the android.util.Patterns.EMAIL_ADDRESS pattern for email validation
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        if(password=="")
        {
            return false;
        }
        // Define your password criteria
        int minLength = 6; // Minimum password length

        // Check if the password meets the criteria
        return password.length() >= minLength;
    }
    public void imageChooser()
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(profilePhoto);
            imageControl=true;


        }
        else
        {
            imageControl=false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String postId=intent.getStringExtra("postId");
        Log.d("onNewIntent: ","postdId is : "+postId);
        if(postId!=null)
        {
            Intent i=new Intent(SignUpActivity.this,showPostActivity.class);
            i.putExtra("postId",postId);
            startActivity(i);
        }
    }


}