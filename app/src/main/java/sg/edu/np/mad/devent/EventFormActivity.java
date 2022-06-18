package sg.edu.np.mad.devent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EventFormActivity extends AppCompatActivity{
    TextView imgPath, updateAddress;
    private static final int PICK_IMAGE_REQUEST = 9544;
    ImageView image;

    //  Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, String event_Picture, boolean bookmarked)
    Uri selectedImage; // event_Picture
    EditText date, location, eventDetail, eventName; // event_Date, event_Location, event_Description
    Button retrieveAddress;

    private int _day, _month, _birthYear;

    // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
    // Declaring the variables to upload the values to firebase
    private String event_ID, event_Name, event_Location, event_Date, event_Description, userID;
    private Boolean bookmarked;


    // Create a event-defined object
    Events event = new Events();

    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Firebase for storing Image
    private StorageReference storageReference;
    private FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);
        imgPath = findViewById(R.id.item_img);
        image = findViewById(R.id.img);

        // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
        // EDITTEXT
        eventName = (EditText) findViewById(R.id.txt_event_form_name);
        location = (EditText) findViewById(R.id.txt_event_form_location);
        date = (EditText) findViewById(R.id.txt_Date);
        eventDetail = (EditText) findViewById(R.id.txt_Event_Details);




        retrieveAddress = findViewById(R.id.locate_address);

        updateAddress = findViewById(R.id.event_form_address);



        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");



        Geocoder geocoder = new Geocoder(EventFormActivity.this, Locale.getDefault());

        // create the GET intent object
        Intent intent = getIntent();

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        userID = intent.getStringExtra("user_id");

        Log.d("Profile ID at EventForm", String.valueOf(userID));


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventFormActivity.this,

                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                _day = day;
                                _month = month;
                                _birthYear = year;
                                updateDateTxt();
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        retrieveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    List<Address> addresses = geocoder.getFromLocationName(location.getText().toString(), 1);
                    Log.d("Geocode Postal", addresses.get(0).toString());


                    if (addresses != null && !addresses.isEmpty()){
                        Address address = addresses.get(0);
                        // Use the address as needed
//                        String message = String.format("Latitude: %f, Longitude: %f",
//                                address.getLatitude(), address.getLongitude());
//                        String message = String.format("Address Line: %f", "Postal Code: %f", address.getAddressLine(0), address.getPostalCode());
                        addresses = geocoder.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
                        updateAddress.setText(addresses.get(0).getAddressLine(0));
                    }else {
                        // Display appropriate message when Geocoder services are not available
                        Toast.makeText(EventFormActivity.this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex){
                    Log.d("Geocode Postal", "HELP");
                }
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();



    }


    // Method for starting the activity for selecting image from phone storage
    public void pick(View view) {
        verifyStoragePermissions(EventFormActivity.this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    public void submit_form(View view){
        uploadImage();
        uploadForm();

    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        selectedImage = data.getData(); // get the file uri
                        if (selectedImage != null) {
                            try{

                                // update the preview image in the layout
//                            image.setImageURI(selectedImage);

                                // Glide is an API that supports fetching of images.
                                // here we are setting image on image view using Glide
                                Glide.with(EventFormActivity.this).load(selectedImage).into(image);
                            }catch (Exception ex){
                                Log.d("Image upload error", String.valueOf(ex));
                            }
                        }
                    }
                }
            }
    );

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private void updateDateTxt(){
        date.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));

    }

    // uploadimage method
    private void uploadImage(){
        if(selectedImage != null){

            // progressDialogue while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            // Defining the child of storageReference
            //
            // SorageReference represents a reference to Google Cloud Storage Object
            StorageReference reference = storageReference.child(
                    // UUID is a class that represents immutable universally unique identifier (UUID)
                    //
                    // A UUID represents a 128-bit value
                    "images/" + UUID.randomUUID().toString()) ;

            // adding listeners on event progression of image upload
            reference.putFile(selectedImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            progressDialog.dismiss();
                            Toast.makeText(EventFormActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e){
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(EventFormActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress listneer for loading on the dialog box
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded " + (int) progress + "%"
                    );
                }
            });


        }
    }

    private void uploadForm(){
        // Create an object of Firebase Database Reference
        DatabaseReference reference ;
        reference = database.getReference();

//        eventName = (EditText) findViewById(R.id.txt_event_form_name);
//        location = (EditText) findViewById(R.id.txt_event_form_location);
//        date = (EditText) findViewById(R.id.txt_Date);
//        eventDetail = (EditText) findViewById(R.id.txt_Event_Details);
        event_ID = UUID.randomUUID().toString();
        event_Name  = eventName.getText().toString();
        String event_Location  = location.getText().toString();
        String event_Date  = date.getText().toString();
        String event_Description  = eventDetail.getText().toString();
        Boolean bookmarked = false;

//        private String event_Name, event_Location, event_Date, event_Description, userID;
//        private Boolean bookmarked;
//            public Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, String event_Picture, boolean bookmarked) {

        event = new Events(event_ID, event_Name, event_Location, event_Date, event_Description, userID, selectedImage.toString(), bookmarked);


        // Insert the user-defined object to the database
        reference.child("Event").push().setValue(event);
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
