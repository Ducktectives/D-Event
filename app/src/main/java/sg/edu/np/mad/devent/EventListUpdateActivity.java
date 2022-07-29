package sg.edu.np.mad.devent;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EventListUpdateActivity extends AppCompatActivity{
    TextView imgPath, updateAddress, retrieveAddress;
    private static final int PICK_IMAGE_REQUEST = 9544;
    ImageView image;



    //  Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, String event_Picture, boolean bookmarked)
    Uri selectedImage; // event_Picture
    EditText et_date, et_location, et_eventDescription, et_eventName, et_eventDetail, et_eventStartTime, et_eventStopTime, et_eventTicketPrice; // event_Date, event_Location, event_Description

    private int _day, _month, _birthYear;

    // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
    // Declaring the variables to upload the values to firebase
    private String event_ID, event_Name, event_Location, event_Date, event_Description, userID, storageReference_ID, event_Detail, download_ImageUrl, event_StartTime, event_StopTime;
    private Boolean bookmarked;

    private Double event_TicketPrice;

    private FirebaseUser user;


    private ProgressBar progressBar;

    // Create a event-defined object
    Events event = new Events();



    // Firebase for storing Image
    private StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    private FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_update);

        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event_ID = extras.getString("Event_ID");
            storageReference_ID = extras.getString("Event_Storage");
            /*Toast.makeText(this, "Event ID " + event_ID, Toast.LENGTH_LONG).show();*/
            /*Toast.makeText(this, "Storage ID " + storageReference_ID, Toast.LENGTH_LONG).show();*/

            //The key argument here must match that used in the other activity
        }



        imgPath = findViewById(R.id.item_img);
        image = findViewById(R.id.img);

        // Events(String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, boolean bookmarked)
        // EDITTEXT
        et_eventName = (EditText) findViewById(R.id.txt_event_form_name);
        et_location = (EditText) findViewById(R.id.txt_event_form_location);
        et_date = (EditText) findViewById(R.id.txt_Date);
        et_eventDescription = (EditText) findViewById(R.id.txt_Event_Description);

        et_eventDetail = (EditText) findViewById(R.id.txt_Event_Details);

        et_eventTicketPrice = (EditText) findViewById(R.id.txt_Event_Ticket_Price);


        et_eventStartTime = (EditText) findViewById(R.id.txt_event_StartTime);
        et_eventStopTime = (EditText) findViewById(R.id.txt_event_EndTime);



        retrieveAddress = (TextView) findViewById(R.id.locate_address);
        updateAddress = (TextView) findViewById(R.id.event_form_address);



        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //set the eventsPricing input keyboard to be numbers only
        et_eventTicketPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_eventTicketPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)}); //set ticket price -  2f


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // [START storage_field_initialization]
        firebaseStorage = FirebaseStorage.getInstance();
        // [END storage_field_initialization]

        firebaseStorage.getMaxUploadRetryTimeMillis();




        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        DatabaseReference reference ;
        reference = database.getInstance().getReference();


        /* 26/07 - Set the form when user comes in */
        // Get username to send in activity

        reference.child("Event").child(event_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Events events = dataSnapshot.getValue(Events.class);
                event_ID = events.Event_ID;
                event_Name = events.Event_Name;
                event_Location = events.Event_Location;
                event_Date = events.Event_Date;
                event_Description = events.Event_Description;
                storageReference_ID = events.Event_StorageReferenceID;
                event_Detail = events.Event_Detail;
                event_TicketPrice = events.Event_TicketPrice;
                event_StartTime = events.Event_StartTime;
                event_StopTime = events.Event_EndTime;



                et_eventName.setText(event_Name);
                et_location.setText(event_Location);
                et_date.setText(event_Date);
                et_eventDescription.setText(event_Description);
                et_location.setText(event_Location);
                et_eventDetail.setText(event_Detail);
                et_eventTicketPrice.setText(event_TicketPrice.toString());
                et_eventStartTime.setText(event_StartTime);
                et_eventStopTime.setText(event_StopTime);

                try{

                    Glide.with(EventListUpdateActivity.this).load(storageReference_ID).into(image);
                    selectedImage = Uri.parse(storageReference_ID);

                }catch (Exception ex){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventListUpdateActivity.this,

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
                        Toast.makeText(EventListUpdateActivity.this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
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
                        EventListUpdateActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                        EventListUpdateActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
        // here, the image is successfully selected from the Gallery
        if (ActivityCompat.checkSelfPermission(EventListUpdateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EventListUpdateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            launcher.launch(intent);
        }
    }

    public void submit_form(View view){

        uploadForm();

        Intent submitform = new Intent(EventListUpdateActivity.this, NavDrawer.class);
        (EventListUpdateActivity.this).finish();
        startActivity(submitform);
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
                                Glide.with(EventListUpdateActivity.this).load(selectedImage).into(image);
                            }catch (Exception ex){
                                Log.d("Image upload error", String.valueOf(ex));
                            }
                        }
                    }
                }
            }
    );




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
//        event_ID = UUID.randomUUID().toString();
        event_Name  = et_eventName.getText().toString();
        event_Location  = et_location.getText().toString();
        event_Date  = et_date.getText().toString();
        event_Description  = et_eventDescription.getText().toString();
        event_Detail = et_eventDetail.getText().toString();

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            Date dateStr = dateFormat.parse(event_Date);
            if (new Date().before(dateStr)) {
                Toast.makeText(EventListUpdateActivity.this, "Date must be created after today", Toast.LENGTH_SHORT).show();

            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(EventListUpdateActivity.this, "Date is invalid", Toast.LENGTH_SHORT).show();

        }




        firebaseStorage = firebaseStorage.getInstance("gs://dvent---ducktectives.appspot.com/");

//        reference = firebaseStorage.getReference();


        // Defining the child of storageReference
        //selectedImage => URI
        // SorageReference represents a reference to Google Cloud Storage Object

        StorageReference ref = firebaseStorage.getReferenceFromUrl(storageReference_ID);




//        StorageReference ref = reference.child("images").child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));

//        reference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));

        // progressDialogue while uploading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        DatabaseReference reference ;
        reference = database.getInstance().getReference();

//        String key = reference.child("Event").push().getKey();




        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        Toast.makeText(EventListUpdateActivity.this, "Selected Image " + selectedImage, Toast.LENGTH_SHORT).show();

        ref.putBytes(data).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e){
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(EventListUpdateActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                progressDialog.dismiss();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Uri downloadUrl = uri;
                        HashMap hashMap = new HashMap();
                        hashMap.put("event_ID", event_ID);
                        hashMap.put("event_Name", event_Name);
                        hashMap.put("event_Location", event_Location);
                        hashMap.put("event_Date", event_Date);
                        hashMap.put("event_Start", event_StartTime);
                        hashMap.put("event_End", event_StopTime);
                        hashMap.put("event_Description", event_Description);
                        hashMap.put("event_Detail", event_Detail);
                        hashMap.put("event_StorageReferenceID", downloadUrl.toString());


                        progressDialog.show();
//                        reference.child("Event").push().setValue(event);


//                        Toast.makeText(EventListUpdateActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();



                        // Attach a listener to read the data at our posts reference
                        reference.child("Event").child(event_ID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Events post = dataSnapshot.getValue(Events.class);
                                System.out.println(post);
                               /* Toast.makeText(EventListUpdateActivity.this, "Event Name" + post.Event_Name, Toast.LENGTH_SHORT).show();
*/
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });

                        reference.child("Event").child(event_ID).updateChildren(hashMap);

//                    orderByChild("event_UserID").equalTo(event_ID);
//                        reference.updateChildren(hashMap);


                        image.setImageDrawable(getResources().getDrawable(R.drawable.file_upload_image_border));
         /*               et_eventName.setText("");
                        et_location.setText("");
                        et_date.setText("");
                        et_eventDescription.setText("");
                        et_eventDetail.setText("");
*/
                        Toast.makeText(EventListUpdateActivity.this, "Form Uploaded", Toast.LENGTH_SHORT).show();

                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.dismiss();
                    }
                });
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

