package sg.edu.np.mad.devent;


import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventDetailsPage extends AppCompatActivity implements OnMapReadyCallback {
    TextView eventOrg;
    String eventOrganizerUID;
    String imageLink;
    String userEmail;
    String eventID;
    Double eventLat;
    Double eventLong;
    String eventNamedIs;

    List<Events> eventList;

    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        //Creation of notification channel
        //createNotificationChannel();

        //receive Intent information
        Intent receiveEventAct = getIntent();
        eventList = (List<Events>) receiveEventAct.getExtras().getSerializable("event_List");
        eventID = receiveEventAct.getStringExtra("event_Name");
        userEmail = receiveEventAct.getStringExtra("Email");


        //---- Retrieving All callable ids on the EventDetailsPage
        TextView eventName = findViewById(R.id.EventName);
        TextView eventLocation = findViewById(R.id.EventLocation);
        TextView eventDescription = findViewById(R.id.EventDescription);
        TextView eventDetail = findViewById(R.id.eventDetails);
        eventOrg = findViewById(R.id.EventOrganiser);
        ImageView eventPicture = findViewById(R.id.eventPicture);
        TextView eventDateMonth = findViewById(R.id.EventMonth);
        TextView eventDateDay = findViewById(R.id.EventDate);
        TextView eventTime = findViewById(R.id.timing);


        //Goes through the eventList to find the correct event
        for (Events ev : eventList){
            if (String.valueOf(ev.getEvent_ID()).equals(eventID)) {
                //Set the information - Event Name
                String eventNameFromList = ev.getEvent_Name(); //get
                 eventNamedIs= eventNameFromList;
                eventName.setText(eventNameFromList); //set

                //Set the information - Event Description
                String eventDesc = ev.getEvent_Description(); //get
                eventDescription.setText(eventDesc); //set

                //Set the information - Event Location
                String eventLoc = ev.getEvent_Location(); //get

                Geocoder geocoder = new Geocoder(EventDetailsPage.this, Locale.getDefault());

                //convert from postal code to string
                try{
                    List<Address> addressDetails = geocoder.getFromLocationName(eventLoc, 1);
                    List<Address> finalAddress ;
                    Log.d("nom", String.valueOf(addressDetails.get(0)));
                    if (addressDetails != null && !addressDetails.isEmpty()){
                        Address address = addressDetails.get(0);
                        eventLat = address.getLatitude();
                        eventLong = address.getLongitude();
                        finalAddress = geocoder.getFromLocation(eventLat, eventLong, 1);
                        eventLocation.setText("Location: " + finalAddress.get(0).getAddressLine(0));
                    }
                }
                catch (Exception ex){
                    eventLocation.setText("Postal Code: " + eventLoc);
                }


                //Set the information - Event Details
                String eventDet= ev.getEvent_Detail(); //get
                eventDetail.setText(eventDet); //set


                //Set the information - Event Organizer
                eventOrganizerUID = ev.getEvent_UserID(); //get
                if (eventOrganizerUID == null){
                    eventOrg.setText("No user.");
                }
                else {
                    //search for the user with the same email
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference Ref2 = database.getReference("Users");
                    Ref2.child(eventOrganizerUID).get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()){
                                        //Toast.makeText(EventDetailsPage.this,"No such user exists.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        String username = String.valueOf(task.getResult().child("username").getValue());
                                        eventOrg.setText("By " + username); //set
                                    }
                                }
                            });
                }


                //for image
                //For event banner/image
                imageLink = ev.getEvent_StorageReferenceID();
                user = FirebaseAuth.getInstance().getCurrentUser();

                try{
                    Glide.with(EventDetailsPage.this).load(imageLink).into(eventPicture);

                }
                catch (Exception ex){

                }



                //for date of event
                String eventDate = ev.getEvent_Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    //set date as a date rather than a string
                    Date evDate = formatter.parse(eventDate);

                    //get month
                    String eventMonth = new SimpleDateFormat("MMMM").format(evDate);
                    //prepare month to be properly displayed (first three letters)
                    eventMonth = eventMonth.substring(0,3);
                    eventMonth = eventMonth.toUpperCase();

                    //get date
                    String eventDayNumber = new SimpleDateFormat("dd").format(evDate);

                    //Change the month and date of event
                    if (eventMonth != null && eventDayNumber != null){
                        eventDateMonth.setText(eventMonth);
                        eventDateDay.setText(eventDayNumber);
                    }
                    else{
                        if(eventMonth == null && eventDayNumber == null){
                            eventDateMonth.setText("No");
                            eventDateDay.setText("Date");
                        }
                        else{
                            Toast.makeText(EventDetailsPage.this,"Contact event organizer for date.", Toast.LENGTH_LONG).show();
                        }
                    }



                } catch (ParseException e) {

                }

                //for timing of event
                String finalTime = "Timing: " + ev.Event_StartTime + " - " +ev.Event_EndTime;
                eventTime.setText(finalTime);

            }
        }



        //event listener for viewing profile of event organiser
        eventOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(EventDetailsPage.this, profile_page.class);
                profile.putExtra("EventOrganizer", eventOrganizerUID);
                startActivity(profile);
                //no finish because can go back

           }
        });


        //event listener for booking
        Button bookEvent = findViewById(R.id.bookbutton);
        bookEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference Ref = database.getReference("Users");
                Ref.child(user.getUid()).child("event_booked").get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful() || task.getResult().child(eventID).getChildrenCount() == 0){
                                    //if the task is unsucessful - meaning that no event under the reference link is booked
                                    Intent book = new Intent(EventDetailsPage.this,UserBooking.class);
                                    Bundle bookingInfo = new Bundle();
                                    bookingInfo.putString("EventPicture",imageLink);
                                    bookingInfo.putString("User_Email",user.getEmail());
                                    bookingInfo.putSerializable("EventList",(Serializable) eventList);
                                    bookingInfo.putString("Event", eventID);
                                    bookingInfo.putString("EventName",eventNamedIs);
                                    bookingInfo.putString("EventPicture",imageLink);
                                    book.putExtras(bookingInfo);
                                    book.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(book);
                                }
                                else{
                                    //get event booking confirmation page
                                    DatabaseReference Ref2 = database.getReference("Booking");
                                    Ref2.child(eventID).child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            String bookinginfoName = String.valueOf(task.getResult().child("Name").getValue());
                                            String bookinginfoEmail = String.valueOf(task.getResult().child("Email").getValue());
                                            String bookingContact = String.valueOf(task.getResult().child("Contact").getValue());
                                            String bookingPax = String.valueOf(task.getResult().child("Pax").getValue());
                                            Log.d("AAA",bookinginfoEmail);
                                            Log.d("aAA",bookingContact);

                                            Integer bookinginfoPax = Integer.valueOf(bookingPax);
                                            Integer bookinginfoContact = Integer.valueOf(bookingContact);

                                            //set the things into intent
                                            Intent BookingSummaryBundle = new Intent(EventDetailsPage.this, BookingSummary.class);
                                            Bundle BookingSummary = new Bundle();
                                            BookingSummary.putString("EventID",eventID);
                                            BookingSummary.putString("Name",bookinginfoName);
                                            BookingSummary.putString("UserEmail",bookinginfoEmail);
                                            BookingSummary.putInt("ContactNum",bookinginfoContact);
                                            BookingSummary.putInt("NumberofTix",bookinginfoPax);
                                            BookingSummary.putString("EventName",eventNamedIs);
                                            BookingSummary.putString("EventImage",imageLink);
                                            BookingSummaryBundle.putExtras(BookingSummary);
                                            startActivity(BookingSummaryBundle);

                                        }
                                    });


                                }

                            }
                        });


                /*
                Intent book = new Intent(EventDetailsPage.this,UserBooking.class);
                Bundle bookingInfo = new Bundle();
                bookingInfo.putString("EventPicture",imageLink);
                bookingInfo.putString("User_Email",userEmail);
                bookingInfo.putSerializable("EventList",(Serializable) eventList);
                bookingInfo.putString("Event", eventID);
                bookingInfo.putString("EventName",eventNamedIs);
                bookingInfo.putString("EventPicture",imageLink);
                book.putExtras(bookingInfo);
                startActivity(book);
                //users should be able to go back
                /*
                // Code below is used to set up notification for the user on the day of the event
                Toast.makeText(EventDetailsPage.this,"Reminder has been set!", Toast.LENGTH_SHORT).show();
                Intent notifyIntent = new Intent(EventDetailsPage.this, NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(EventDetailsPage.this, 0,
                        notifyIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();
                long tenSeconds = 1000 * 10;

                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSeconds, pendingIntent);
*/


            }
        });


        //google maps
        //checkMyPermission();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        supportMapFragment.getMapAsync(this);


        /*//for bookmark
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference Ref2 = database.getReference("Event");
                Ref2.child(eventID).get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()){
                                    //if the task is unsucessful
                                }
                                else{
                                    //set bookmark value to true/false
                                }
                            }
                        });
            }
        });*/


    }

  /*  private void checkMyPermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                 boolean PermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS); //opens settings of the app.
                Uri uri = Uri.fromParts("package",getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        });
    }*/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                try {
                    //add a marker on the google map
                    MarkerOptions marker1 = new MarkerOptions();
                    if (eventLat != null && eventLong != null) {
                        marker1.position(new LatLng(eventLat, eventLong));
                        marker1.title(eventNamedIs);
                        googleMap.addMarker(marker1);
                    }

                    //pan the focus of the google maps pin to the center
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(marker1.getPosition());
                    LatLngBounds markerLoc = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(markerLoc,12);
                    googleMap.moveCamera(cu);
                    //googleMap.setLatLngBoundsForCameraTarget(markerLoc);

                    //add zoom controls
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);



                }
                catch (Exception e){

                }
            }
        });
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Devent";
            String description = "Upcoming event!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyEvent", name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}