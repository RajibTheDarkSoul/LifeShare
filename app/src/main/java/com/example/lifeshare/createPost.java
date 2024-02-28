package com.example.lifeshare;
import static com.example.lifeshare.FcmMessageSender.sendFcmMessage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class createPost extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    ProgressBar progressBar;

    String date;

    private EditText editTextNumOfBags, editTextReqDate, editTextLocation, editTextDetails;
    private Spinner spinnerBloodGroup;
    private ImageView imageViewMedicalCertification;
    private Button imageButtonSelectImage;
    private Button buttonCreatePost;

    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    private String selectedImagePath;
    Uri selectedImageUri;
    private Geocoder geocoder;
    private int iterLimit = 10000;
    int numOfBags=-1;
    String posI;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        postsRef = database.getReference("posts");
        geocoder = new Geocoder(this);


        // Initialize Views
        editTextNumOfBags = findViewById(R.id.editTextNumOfBags);
    //    editTextReqDate = findViewById(R.id.editTextReqDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDetails = findViewById(R.id.editTextDetails);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        imageViewMedicalCertification = findViewById(R.id.imageViewMedicalCertification);
        imageButtonSelectImage = findViewById(R.id.imageButtonSelectImage);
        buttonCreatePost = findViewById(R.id.buttonCreatePost);
        progressBar=findViewById(R.id.progressButton3);

        editTextNumOfBags.setText("1");

        date=getTodaysDate();
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        // Populate the spinner with blood groups
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        // Set up a listener for the spinner item selection
        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected blood group
                String selectedBloodGroup = parentView.getItemAtPosition(position).toString();
                // Do something with the selected blood group
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Set up a listener for the Create Post button
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCreatePost.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                createPostF();

            }
        });

        // Set up a listener for the image selection button
        imageButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an image picker
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            selectedImageUri = data.getData();
            // Set the selected image path
            selectedImagePath = selectedImageUri.toString();
            // Display the selected image in the ImageView
            imageViewMedicalCertification.setImageURI(selectedImageUri);
        }
    }

    void createPostF() {
        // Get other data from UI elements

        String val=editTextNumOfBags.getText().toString();
        if (val!=null || !val.equals(""))
        {
            numOfBags = Integer.parseInt(val);

        }
     //   String reqDate = editTextReqDate.getText().toString();
        String reqDate=date;
        String location = editTextLocation.getText().toString();
        String details = editTextDetails.getText().toString();
        String selectedBloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        if(numOfBags<1 || reqDate==null || location==null||details==null||selectedBloodGroup==null||selectedImageUri==null)
        {

            Toast.makeText(this, "You have to fill every field with valid information.", Toast.LENGTH_LONG).show();
            buttonCreatePost.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Generate a unique ID for the image
        UUID imageID = UUID.randomUUID();
        String imageName = "images/" + imageID + ".jpg";

        // Get the image URI
//        Uri imageUri = Uri.parse("content://path/to/your/image.jpg"); // Replace with the actual image URI
        Uri imageUri = selectedImageUri;

        // Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageName);

        // Upload the image to Firebase Storage
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // The download URI is the URL of the uploaded image
                        String imageDownloadUrl = downloadUri.toString();

                        // Create a new Post instance
                        Post newPost = new Post();
                        newPost.setUserId(userId);
                        newPost.setProfilepic(""); // Replace with actual profile pic
                        newPost.setTimeAgo(String.valueOf(new Date().getTime()));
                        newPost.setBloodGroup(selectedBloodGroup);
                        newPost.setNumOfBags(numOfBags);
                        newPost.setReqDate(reqDate);
                        newPost.setLocation(location);
                        newPost.setDetails(details);
                        newPost.setMedicalCertificationImage(imageDownloadUrl);
                        newPost.setInterestedUsers(new ArrayList<>()); // Initialize with an empty list

                        // Push the new post to Firebase with a unique ID
                        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                        DatabaseReference newPostRef = postsRef.push();
                        String postId = newPostRef.getKey();
                        newPost.setPostId(postId);

                        newPostRef.setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //posI= newPost.getPostId();
                                posI=postId;
                                checkAndSendNotifications(newPost);
                                Toast.makeText(createPost.this, "A request has been made.", Toast.LENGTH_SHORT).show();
                                // Post created successfully
                                // Add any additional logic or UI updates as needed
                                finish(); // Finish the activity or navigate to another activity
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buttonCreatePost.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                // Handle failure
                                Toast.makeText(createPost.this, "Couldn't make the request. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure during image upload
                Toast.makeText(createPost.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
















    public double calculateDistanceVincenty(String originUpazila, String destinationUpazila) {
        LatLng originLatLng = getLatLng(originUpazila);
        LatLng destinationLatLng = getLatLng(destinationUpazila);

        try {
            double distance = calculateVincentyDistance(
                    originLatLng.latitude, originLatLng.longitude,
                    destinationLatLng.latitude, destinationLatLng.longitude);

            return distance;
        } catch (Exception e) {
            Toast.makeText(this, "Error calculating distance", Toast.LENGTH_SHORT).show();
            return -1.0;
        }
    }

    private double calculateVincentyDistance(double lat1, double lon1, double lat2, double lon2) {
        // Vincenty's formulae implementation
        double a = 6378137; // semi-major axis of the ellipsoid in meters
        double f = 1 / 298.257223563; // flattening
        double b = (1 - f) * a; // semi-minor axis

        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double L = Math.toRadians(lon2 - lon1);
        double lambda = L;
        double sinU1 = Math.sin(U1);
        double cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2);
        double cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double a2, b2, uSq, A, B, C, deltaSigma, s;

        double deltaLambda;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) {
                return 0; // co-incident points
            }
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM)) {
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            }
            C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            double lambdaPrev = lambda;
            lambda = L + (1 - C) * f * sinAlpha *
                    (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
            deltaLambda = lambda - lambdaPrev;
        } while (Math.abs(deltaLambda) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) {
            return Double.NaN; // formula failed to converge
        }

        uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        deltaSigma = B * sinSigma *
                (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
                        B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        s = b * A * (sigma - deltaSigma);

        return s;
    }

    private LatLng getLatLng(String upazilaName) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(upazilaName, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void checkAndSendNotifications(Post newPost) {
        // Get the user's address and blood group from Firebase based on their userId
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // String userBloodGroup = snapshot.child("bloodGroup").getValue(String.class);
               // String userUpazila = snapshot.child("upazila").getValue(String.class);

             //   String userBloodGroup = snapshot.child("bloodType").getValue(String.class);
                // String userUpazila = snapshot.child("upazilla").getValue(String.class);

                // Iterate through all users to check for nearby users
                DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Address");
                addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot addressSnapshot) {
                        for (DataSnapshot userAddressSnapshot : addressSnapshot.getChildren()) {
                            String otherUserId = userAddressSnapshot.getKey();
                            String otherUserBlood=snapshot.child(otherUserId).child("bloodType").getValue(String.class);
                            Log.d("RETRIEVED BLOOD: ",otherUserBlood);
                            String otherUserUpazilla=addressSnapshot.child(otherUserId).child("upazilla").getValue(String.class);
                            String otherUserDistrict=addressSnapshot.child(otherUserId).child("district").getValue(String.class);



                            if (!otherUserId.equals(newPost.getUserId()) && otherUserBlood.equals(newPost.getBloodGroup())) { // Exclude the current user
                       //         String otherUserUpazila = userAddressSnapshot.child("upazilla").getValue(String.class);

                                double distance = calculateDistanceVincenty(otherUserUpazilla+", "+otherUserDistrict, newPost.getLocation());
                                Log.d("CALCULATED DISTANCE", String.valueOf(distance));

                                // Calculate distance between the post location and other user's location
                               // double distance = calculateDistanceVincenty(userUpazila, otherUserUpazila);

                                // Check blood group and distance conditions
                            //    if (userBloodGroup.equals(newPost.getBloodGroup()) && distance < 30.0) {
                                    if ( distance < 30000.0 && distance!=-1) {
                                    // Notify the other user with a push notification
                                    sendPushNotification(otherUserId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    private void sendPushNotification(String otherUserId) {
        Toast.makeText(this, "Inside sendpush", Toast.LENGTH_SHORT).show();
        // Retrieve the device token from the 'tokens' node
        DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("tokens").child(otherUserId);
        tokensRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deviceToken = snapshot.child("token").getValue(String.class);

                Toast.makeText(createPost.this, "generated token:"+deviceToken, Toast.LENGTH_SHORT).show();
                if (deviceToken != null) {
                    // Build the FCM message
                    Map<String, String> data = new HashMap<>();
                    data.put("title", "Blood Request");
                    data.put("body", "An user in your location requires blood. Check the post for details.");

                    Log.d("Putting PostId: ",posI);
                    data.put("postId",posI);

                    FcmMessageSender.FcmMessage fcmMessage = new FcmMessageSender.FcmMessage(deviceToken, data);

                    // Send the FCM message
                    Toast.makeText(createPost.this, "Sending noti", Toast.LENGTH_SHORT).show();
                  //  sendFcmMessage(fcmMessage);
                    FcmMessageSender.sendFcmMessage(fcmMessage);

                    // Use FCM to send a push notification
                    // You need to implement the FCM logic, possibly using a server or Cloud Function.
                    // Refer to Firebase documentation for sending notifications: https://firebase.google.com/docs/cloud-messaging
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }


    }
