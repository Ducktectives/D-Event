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
import android.content.ContentResolver;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventFormActivity extends AppCompatActivity{
    TextView imgPath, updateAddress, retrieveAddress;
    private static final int PICK_IMAGE_REQUEST = 9544;
    ImageView image;



    //  Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, String event_Picture, boolean bookmarked)
    Uri selectedImage; // event_Picture
    EditText et_date, et_location, et_eventDescription, et_eventName, et_eventDetail, et_eventStartTime, et_eventStopTime , et_eventPricing; // event_Date, event_Location, event_Description

    private int _day, _month, _birthYear;

    // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
    // Declaring the variables to upload the values to firebase
    private String event_ID, event_Name, event_Location, event_Date, event_Description, userID, storageReference_ID, event_Detail, download_ImageUrl, event_StartTime, event_StopTime ;
    private Double event_TicketPrice;
    private Boolean bookmarked;

    private FirebaseUser user;


    private ProgressBar progressBar;

    // Create a event-defined object
    Events event = new Events();



    // Firebase for storing Image
    private StorageReference reference;
    FirebaseStorage firebaseStorage;
    private FirebaseDatabase database;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);
        imgPath = findViewById(R.id.item_img);
        image = findViewById(R.id.img);

        // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked, Double event_TicketPrice)
        // EDITTEXT
        et_eventName = (EditText) findViewById(R.id.txt_event_form_name);
        et_location = (EditText) findViewById(R.id.txt_event_form_location);
        et_date = (EditText) findViewById(R.id.txt_Date);
        et_eventDescription = (EditText) findViewById(R.id.txt_Event_Description);
        et_eventDetail = (EditText) findViewById(R.id.txt_Event_Details);
        et_eventStartTime = (EditText) findViewById(R.id.txt_event_StartTime);
        et_eventStopTime = (EditText) findViewById(R.id.txt_event_EndTime);
        et_eventPricing = (EditText) findViewById(R.id.txt_Event_Ticket_Price);

        //set the eventsPricing input keyboard to be numbers only
        et_eventPricing.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_eventPricing.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)}); //set ticket price -  2f


        retrieveAddress = (TextView) findViewById(R.id.locate_address);
        updateAddress = (TextView) findViewById(R.id.event_form_address);



        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // [START storage_field_initialization]
        firebaseStorage = FirebaseStorage.getInstance();
        // [END storage_field_initialization]

        firebaseStorage.getMaxUploadRetryTimeMillis();


        Toast.makeText(this, "User ID : " + userID.toString(), Toast.LENGTH_SHORT).show();


        Geocoder geocoder = new Geocoder(EventFormActivity.this, Locale.getDefault());



        et_date.setOnClickListener(new View.OnClickListener() {
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
                    List<Address> addresses = geocoder.getFromLocationName(et_location.getText().toString(), 1);
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






    }


    // Method for starting the activity for selecting image from phone storage
    public void pick(View view) {

//            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            galleryIntent.setType("image/*");
//            launcher.launch("image/*");
        // here, the image is successfully selected from the Gallery
        if (ActivityCompat.checkSelfPermission(EventFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EventFormActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            launcher.launch(intent);
        }
    }

    public void submit_form(View view){

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


//    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent data = result.getData();
//                        selectedImage = data.getData(); // get the file uri
//                        if (selectedImage != null) {
//                            try{
//
//                                // update the preview image in the layout
////                            image.setImageURI(selectedImage);
//
//                                // Glide is an API that supports fetching of images.
//                                // here we are setting image on image view using Glide
//                                Glide.with(EventFormActivity.this).load(selectedImage).into(image);
//                            }catch (Exception ex){
//                                Log.d("Image upload error", String.valueOf(ex));
//                            }
//                        }
//                    }
//                }
//            }
//    );


    private void updateDateTxt(){
        et_date.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));

    }


    // getting file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    private void uploadForm(){

        // get all the event attributes
        event_ID = UUID.randomUUID().toString();
        event_Name  = et_eventName.getText().toString();
        event_Location  = et_location.getText().toString();
        event_Date  = et_date.getText().toString();
        event_Description  = et_eventDescription.getText().toString();
        event_Detail = et_eventDetail.getText().toString();
        event_StartTime = et_eventStartTime.getText().toString();
        event_StopTime = et_eventStopTime.getText().toString();
        event_TicketPrice = Double.valueOf(et_eventPricing.getText().toString());


        bookmarked = false;


        if (event_Name.isEmpty()){
            et_eventName.setError("This field is required");
            et_eventName.requestFocus();
            return;
        }
        if (event_Date.isEmpty()){
            et_date.setError("This field is required");
            et_date.requestFocus();
            return;
        }
        if (event_Location.isEmpty()){
            et_location.setError("This field is required");
            et_location.requestFocus();
            return;
        }
        if (event_Description.isEmpty()){
            et_eventDescription.setError("This field is required");
            et_eventDescription.requestFocus();
            return;
        }
        if (event_StartTime.isEmpty()){
            et_eventStartTime.setError("This field is required");
            et_eventStartTime.requestFocus();
            return;
        }
        if (event_StopTime.isEmpty()){
            et_eventStopTime.setError("This field is required");
            et_eventStopTime.requestFocus();
            return;
        }
        if (et_eventPricing.getText() == null || et_eventPricing.getText().toString() == ""){
            et_eventPricing.setError("This field is required");
            et_eventPricing.requestFocus();
            return;
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            Date dateStr = dateFormat.parse(event_Date);
            if (new Date().before(dateStr)) {
                Toast.makeText(EventFormActivity.this, "Date must be created after today", Toast.LENGTH_SHORT).show();

            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(EventFormActivity.this, "Date is invalid", Toast.LENGTH_SHORT).show();

        }




        firebaseStorage = firebaseStorage.getInstance("gs://dvent---ducktectives.appspot.com/");

        reference = firebaseStorage.getReference();


        // Defining the child of storageReference
        //selectedImage => URI
        // SorageReference represents a reference to Google Cloud Storage Object
        StorageReference ref = reference.child("images").child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));

        // progressDialogue while uploading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        DatabaseReference reference ;
        reference = database.getReference();

     /*   Toast.makeText(EventFormActivity.this, "Event Stop Time" + event_StopTime, Toast.LENGTH_SHORT).show();
*/

        ref.putFile(selectedImage).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e){
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(EventFormActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                progressDialog.dismiss();
                Toast.makeText(EventFormActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        Events event = new Events( event_ID,  event_Name,  event_Location,  event_Date,  event_Description,  event_Detail,  event_StartTime, event_StopTime, userID, downloadUrl.toString(),  bookmarked , event_TicketPrice);


                        progressDialog.show();
                        reference.child("Event").child(event_ID).setValue(event);

                        image.setImageDrawable(getResources().getDrawable(R.drawable.file_upload_image_border));
                        et_eventName.setText("");
                        et_location.setText("");
                        et_date.setText("");
                        et_eventDescription.setText("");
                        et_eventDetail.setText("");
                        et_eventStartTime.setText("");
                        et_eventStopTime.setText("");
                        et_eventPricing.setText(""); //reset

                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.dismiss();
                    }
                });
            }


//                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                            @Override
//                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                if (!task.isSuccessful()) {
//                                    throw task.getException();
//                                }
//
//                                // Continue with the task to get the download URL
//                                return ref.getDownloadUrl();
//                            }
//                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//                                download_ImageUrl = task.getResult().toString();
//                            } else {
//                                // Handle failures
//                                // ...
//                            }
//                        }
//                    });


//                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            download_ImageUrl = task.getResult().toString();
//
//                        }
//                    });


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




//        Toast.makeText(EventFormActivity.this, "Failed to write to database!" + download_ImageUrl, Toast.LENGTH_LONG).show();



        // Create an object of Firebase Database Reference
//        DatabaseReference reference ;
//        reference = database.getReference();
//        event = new Events( event_ID,  event_Name,  event_Location,  event_Date,  event_Description,  event_Detail,  userID, download_ImageUrl,  bookmarked);
//        // Insert the user-defined object to the database
//        reference.child("Event").push().setValue(event).addOnSuccessListener(
//                new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(EventFormActivity.this, "Success !!!", Toast.LENGTH_SHORT).show();
//
//
//
//                    }
//                }
//        ).addOnFailureListener(
//                new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(EventFormActivity.this, "Failed to write to database!", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//        );



    }




    // uploadimage method
//    public void uploadImage(){
//
//
//        firebaseStorage = firebaseStorage.getInstance("gs://authentication-b12c2.appspot.com");
//
//        reference = firebaseStorage.getReference();
//
//
//        ImageView imageView = (ImageView)findViewById(R.id.img);
//
//        imageView.setDrawingCacheEnabled(true);
//        imageView.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        // Defining the child of storageReference
//        //selectedImage => URI
//        // SorageReference represents a reference to Google Cloud Storage Object
//        StorageReference ref = reference.child("images").child(System.currentTimeMillis() + '.' + getFileExtension(selectedImage));
//
//
//            // progressDialogue while uploading
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//
//
//
//
//            UploadTask uploadTask = ref.putFile(selectedImage);
//            // adding listeners on event progression of image upload
//            uploadTask.addOnFailureListener(new OnFailureListener(){
//                @Override
//                public void onFailure(@NonNull Exception e){
//                    // Error, Image not uploaded
//                    progressDialog.dismiss();
//                    Toast.makeText(EventFormActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    // Image uploaded successfully
//                    progressDialog.dismiss();
//                    Toast.makeText(EventFormActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();
//
//
//                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            download_ImageUrl = task.getResult().toString();
//
//                        }
//                    });
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                // Progress listneer for loading on the dialog box
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    double progress = (100.0 * snapshot.getBytesTransferred()
//                            / snapshot.getTotalByteCount());
//                    progressDialog.setMessage(
//                            "Uploaded " + (int) progress + "%"
//                    );
//                }
//            });
//
//
//        }



}

//Creating the Input Filter to ensure that the Ticket Price does not run past 2dp
class DecimalDigitsInputFilter implements InputFilter{
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digitsBeforePoint,int digitsAfterPoint){
       mPattern = Pattern.compile("[0-9]{0," + (digitsBeforePoint- 1) + "}+((\\.[0-9]{0," + (digitsAfterPoint - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned toBeFiltered, int i2, int i3) {
        Matcher matchThePattern = mPattern.matcher(toBeFiltered);
        if (!matchThePattern.matches()){
            return "";
        }
        return null;
    }
}

