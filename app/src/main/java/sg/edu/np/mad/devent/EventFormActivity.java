package sg.edu.np.mad.devent;


import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class EventFormActivity extends AppCompatActivity{
    TextView imgPath, updateAddress, retrieveAddress;
    private static final int PICK_IMAGE_REQUEST = 9544;
    ImageView image;



    //  Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, String event_Picture, boolean bookmarked)
    Uri selectedImage; // event_Picture
    EditText et_date, et_location, et_eventDescription, et_eventName, et_eventDetail, et_eventStartTime, et_eventStopTime , et_eventPricing; // event_Date, event_Location, event_Description
   TextView tvEventTypeError;
    private int _day, _month, _birthYear;

    // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
    // Declaring the variables to upload the values to firebase
    private String event_ID, event_Name, event_Location, event_Date, event_Description, userID, storageReference_ID, event_Detail, download_ImageUrl, event_StartTime, event_StopTime;
    List<String> eventTypes = new ArrayList<>();
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

    CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6;



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
        tvEventTypeError = (TextView) findViewById(R.id.event_form_eventTypeTitle);


        //set the eventsPricing input keyboard to be numbers only
        et_eventPricing.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_eventPricing.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)}); //set ticket price -  2f



        retrieveAddress = (TextView) findViewById(R.id.locate_address);
        updateAddress = (TextView) findViewById(R.id.event_form_address);


        // Finding CheckBox by its unique ID
        checkbox1=(CheckBox)findViewById(R.id.sportsCheckbox);
        checkbox2=(CheckBox)findViewById(R.id.gamingCheckbox);
        checkbox3=(CheckBox)findViewById(R.id.animeCheckbox);
        checkbox4=(CheckBox)findViewById(R.id.musicCheckbox);
        checkbox5=(CheckBox)findViewById(R.id.educationCheckbox);
        checkbox6=(CheckBox)findViewById(R.id.animalsCheckbox);



        /*progressBar = (ProgressBar) findViewById(R.id.progressBar);
*/
        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // [START storage_field_initialization]
        firebaseStorage = FirebaseStorage.getInstance();
        // [END storage_field_initialization]

        firebaseStorage.getMaxUploadRetryTimeMillis();



        Geocoder geocoder = new Geocoder(EventFormActivity.this, Locale.getDefault());

     /*   ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set this to true if you want back button on the actionbar
        // Intents dont send over when you press back on the action bar for some raeason
        // so im stuck
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent i = new Intent(EventFormActivity.this,NavDrawer.class);
                Log.d("backbutton","hey the back button is being pressed");
                i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this,callback);



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

        // Initialise Variables for selecting Start and End Time
        final int[] startHour = new int[1];
        final int[] startMinute = new int[1];
        final int[] endHour = new int[1];
        final int[] endMinute = new int[1];

        et_eventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EventFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourofday, int minofday) {
                                startHour[0] = hourofday;
                                startMinute[0] = minofday;
                                String time = startHour[0] + ":" + startMinute[0];
                                SimpleDateFormat f24hr = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hr.parse(time);
                                    SimpleDateFormat f12hr = new SimpleDateFormat(
                                            "HH:mm aa"
                                    );

                                    et_eventStartTime.setText(f12hr.format(date));
                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(startHour[0], startMinute[0]);
                timePickerDialog.show();
            }
        });

        et_eventStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EventFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourofday, int minofday) {
                                endHour[0] = hourofday;
                                endMinute[0] = minofday;
                                String time = endHour[0] + ":" + endMinute[0];
                                SimpleDateFormat f24hr = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hr.parse(time);
                                    SimpleDateFormat f12hr = new SimpleDateFormat(
                                            "HH:mm aa"
                                    );

                                    et_eventStopTime.setText(f12hr.format(date));
                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(endHour[0], endMinute[0]);
                timePickerDialog.show();
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

    /*
    CheckBox sportsCheck = findViewById(R.id.sportsCheckbox);
    CheckBox gamingCheck = findViewById(R.id.gamingCheckbox);
    CheckBox animeCheck = findViewById(R.id.animeCheckbox);
    CheckBox musicCheck = findViewById(R.id.musicCheckbox);
    CheckBox educationCheck = findViewById(R.id.educationCheckbox);
    CheckBox animalsCheck = findViewById(R.id.animalsCheckbox);

     */

    // Method for checking which eventType checkboxes are selected
    public void checkBoxes(View view){

        CheckBox checkBox = (CheckBox) view;


        if (checkBox.isChecked()){
            eventTypes.add(checkBox.getText().toString());
        }
        else if (checkBox.isChecked() == false){
            eventTypes.remove(checkBox.getText().toString());
        }
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
                .append(_day).append("/").append(_month + 1).append("/").append(_birthYear));

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
        if (eventTypes.size() == 0){
            tvEventTypeError.setError("This field is required");
            tvEventTypeError.requestFocus();
            return;
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            Date dateStr = dateFormat.parse(event_Date);
            if (new Date().before(dateStr)) {
                Toast.makeText(EventFormActivity.this, "Event date has passed", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(EventFormActivity.this, "Image failed to uploaded" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Events event = new Events( event_ID, event_Name, event_Location, event_Date,
                                event_Description, event_Detail, event_StartTime, event_StopTime,
                                userID, downloadUrl.toString(),  bookmarked,  event_TicketPrice, eventTypes);


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


        Intent submitform = new Intent(EventFormActivity.this, NavDrawer.class);
        startActivity(submitform);

    }
}


