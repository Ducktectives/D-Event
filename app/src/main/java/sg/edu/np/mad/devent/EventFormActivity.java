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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.ParseException;
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
    EditText date, location, eventDescription, eventName, eventDetail; // event_Date, event_Location, event_Description
    Button retrieveAddress;

    private int _day, _month, _birthYear;

    // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
    // Declaring the variables to upload the values to firebase
    private String event_ID, event_Name, event_Location, event_Date, event_Description, userID, storageReference_ID, event_Detail;
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

    // boolean variable to check whether all the text fields are empty
    boolean isAllFieldsChecked = false;
    // boolean variable to check whether date field is accurate
    boolean isDateFieldChecked = false;

    FirebaseAuth mAuth;

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
        eventDescription = (EditText) findViewById(R.id.txt_Event_Description);
        eventDetail = (EditText) findViewById(R.id.txt_Event_Details);



        retrieveAddress = findViewById(R.id.locate_address);

        updateAddress = findViewById(R.id.event_form_address);



        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");


        // Authenticate
        mAuth = FirebaseAuth.getInstance();


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
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            // do your stuff
            uploadForm();
//        } else {
//            signInAnonymously();
//        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // do your stuff
                        uploadForm();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("TAG", "signInAnonymously:FAILURE", exception);
                    }
                });
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

            storageReference_ID = UUID.randomUUID().toString();

            // Defining the child of storageReference
            //
            // SorageReference represents a reference to Google Cloud Storage Object
            StorageReference reference = storageReference.child(
                    // UUID is a class that represents immutable universally unique identifier (UUID)
                    //
                    // A UUID represents a 128-bit value
                    "images/" + storageReference_ID) ;

            // adding listeners on event progression of image upload
            reference.putFile(selectedImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            progressDialog.dismiss();
                            Toast.makeText(EventFormActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();
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


        // store the returned value of the dedicated function which checks
        // whether the entered data is valid or if any fields are left blank.
        isAllFieldsChecked = checkEmptyFields();
        isDateFieldChecked = checkDate();

        if(isAllFieldsChecked){
            // Create an object of Firebase Database Reference
            DatabaseReference reference ;
            reference = database.getReference();

            event_ID = UUID.randomUUID().toString();
            event_Name  = eventName.getText().toString();
            event_Location  = location.getText().toString();
            event_Date  = date.getText().toString();
            event_Description  = eventDescription.getText().toString();
            event_Detail = eventDetail.getText().toString();
            bookmarked = false;

            event = new Events(event_ID, event_Name, event_Location, event_Date, event_Description, event_Detail, userID, storageReference_ID, bookmarked);

            // Insert the user-defined object to the database
            reference.child("Event").push().setValue(event);

            // progressDialogue while uploading
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();

            Log.d("UPLOAD FORM", "FORM UPLOADEDDDD");


            // Upload image to storage
            uploadImage();

//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                // Add a completion callback
//                // to know when our data has been committed, you can add a completion listener.
//                @Override
//                public void onSuccess(Void aVoid) {
//                    // Form uploaded successfully
//                    progressDialog.dismiss();
//                    Toast.makeText(EventFormActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    // Form uploaded unsuccessfully
//                    progressDialog.dismiss();
//                    Toast.makeText(EventFormActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();
//                }
//            });


        }
    }

    // function which checks all the text fields
    // are filled or not by the user.
    //  EditText date, location, eventDescription, eventName, eventDetail;
    private boolean checkEmptyFields(){
        event_ID = UUID.randomUUID().toString();
        event_Name  = eventName.getText().toString();
        event_Location  = location.getText().toString();
        event_Date  = date.getText().toString();
        event_Description  = eventDescription.getText().toString();
        event_Detail = eventDetail.getText().toString();

          if (event_Name.length() == 0){
              eventName.setError("This field is required");
              return false;
          }
          if (event_Date.length() == 0){
              date.setError("This field is required");
              return false;
          }
        if (event_Location.length() == 0){
            location.setError("This field is required");
            return false;
        }
        if (event_Description.length() == 0){
            eventDescription.setError("This field is required");
            return false;
        }
        // if all validations return True
        return true;
    };

    private boolean checkDate(){
        event_Date  = date.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            Date dateStr = dateFormat.parse(event_Date);
            if (new Date().after(dateStr)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(EventFormActivity.this, "Date is invalid", Toast.LENGTH_SHORT).show();

        }
        // if validation for date is true
        return true;
    }

}
