package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.Permission;
import java.util.Calendar;

public class BookingSummary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        // Create a new Event object to assign event values
        Events event = new Events();

        // Assigning the buttons
        TextView EventName = (TextView)findViewById(R.id.BookingConfirmationEvent);
        TextView Name = (TextView)findViewById(R.id.ConfirmationEnteredName);
        TextView UserEmail = (TextView)findViewById(R.id.ConfirmationEnteredEmail);
        TextView ContactNum = (TextView)findViewById(R.id.ConfirmationEnteredContact);
        TextView NumofTix = (TextView)findViewById(R.id.ConfirmationEnteredTickets);
        ImageView eventPicture = (ImageView)findViewById(R.id.bookingSummaryView);

        Button Close = (Button)findViewById(R.id.ConfirmationClose);
        Button AddtoCalender = (Button)findViewById(R.id.BookingAddCalander);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UserId = user.getUid();



        // Get the intent from the previous activity and setting the values
        Bundle autofill = getIntent().getExtras();
        String eventid = autofill.getString("EventID");
        String Email = user.getEmail();
        String nameFromBundle = autofill.getString("Name");
        String userEmailFromBundle = autofill.getString("UserEmail");
        Integer contactNoFromBundle = autofill.getInt("ContactNum");
        Integer numberOfTixFromBundle = autofill.getInt("NumberofTix");
        String eventName = autofill.getString("EventName");
        String imageLink = autofill.getString("EventImage");

        Log.d("debug",userEmailFromBundle);
        Log.d("debug",numberOfTixFromBundle.toString());

        // Set the image of the event
        try {
            Glide.with(BookingSummary.this).load(imageLink).into(eventPicture);
        }
        catch (Exception e){

        }

        EventName.setText(eventName);
        Name.setText("Name : " + nameFromBundle);
        UserEmail.setText("Email : " + userEmailFromBundle);
        ContactNum.setText("Contact Number : " + contactNoFromBundle.toString());
        NumofTix.setText("Tickets Booked : " + numberOfTixFromBundle.toString());


        // End the activity and go back to the profile page
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(BookingSummary.this, NavDrawer.class);
                i2.putExtra("Email", Email);
                i2.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i2);
                finish(); // should not be able to go back
            }
        });

        // Add the Event and Event details to the Calender application
        AddtoCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Check if we have write permission
                    int permission = ActivityCompat.checkSelfPermission(BookingSummary.this, Manifest.permission.WRITE_CALENDAR);

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        ActivityCompat.requestPermissions(
                                BookingSummary.this,
                                new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
                    }

                    // Connect Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference Ref = database.getReference("Event");
                    DatabaseReference book = database.getReference("Booking");
                    DatabaseReference user = database.getReference("Users");


                    // Query the database in the Events folders in the Event Details
                    Ref.orderByChild("event_ID").equalTo(eventid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Check for event ID
                            if (snapshot.exists()){
                                Ref.child(eventid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data. Please reload.", task.getException());
                                        }
                                        else {
                                            // Assigning value from the event database to get the information
                                            Log.d("firebase", String.valueOf(task.getResult().child("username").getValue()));
                                            String eventname = task.getResult().child("event_Name").getValue(String.class);
                                            String eventdescription = task.getResult().child("event_Description").getValue(String.class);
                                            String eventlocation = task.getResult().child("event_Location").getValue(String.class);
                                            String eventStartTime = task.getResult().child("event_StartTime").getValue(String.class);
                                            String eventEndTime = task.getResult().child("event_EndTime").getValue(String.class);
                                            String eventdate = task.getResult().child("event_Date").getValue(String.class);
                                            String [] datedetails = eventdate.trim().split("/");
                                            String [] timestart = eventStartTime.trim().split(":");
                                            String [] timestartmin = timestart[1].trim().split(" ");
                                            String [] timeend = eventEndTime.trim().split(":");
                                            String [] timeendmin = timeend[1].trim().split(" ");


                                            long startMillis = 0;
                                            long endMillis = 0;
                                            Calendar beginTime = Calendar.getInstance();
                                            beginTime.set(Integer.parseInt(datedetails[2]), Integer.parseInt(datedetails[1]) - 1, Integer.parseInt(datedetails[0]), Integer.parseInt(timestart[0]), Integer.parseInt(timestartmin[0]));
                                            startMillis = beginTime.getTimeInMillis();
                                            Calendar endTime = Calendar.getInstance();
                                            endTime.set(Integer.parseInt(datedetails[2]), Integer.parseInt(datedetails[1]) - 1, Integer.parseInt(datedetails[0]), Integer.parseInt(timeend[0]), Integer.parseInt(timeendmin[0]));
                                            endMillis = endTime.getTimeInMillis();

                                            // Assigning the value to put in the Calendar
                                            Intent Calender = new Intent(Intent.ACTION_INSERT);
                                            Calender.setData(CalendarContract.Events.CONTENT_URI);
                                            Calender.putExtra(CalendarContract.Events.TITLE, eventname);
                                            //Calender.putExtra(CalendarContract.Events.ALL_DAY, true);
                                            Calender.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
                                            Calender.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);
                                            //Calender.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStartTime);
                                            //Calender.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndTime);
                                            Calender.putExtra(CalendarContract.Events.EVENT_LOCATION, eventlocation);
                                            Calender.putExtra(CalendarContract.Events.DESCRIPTION, eventdescription);
                                            Calender.putExtra(CalendarContract.Events.AVAILABILITY, "Busy");

                                            if (Calender.resolveActivity(getPackageManager()) != null){
                                                Calender.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(Calender);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(BookingSummary.this, "There is no application that is able to support this feature", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Unable to add event to calendar", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Let user know that Booking is Unsuccessful
                            Toast.makeText(getApplicationContext(), "Unable to add event to calendar", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Insufficient Permission to add event to calendar", Toast.LENGTH_LONG).show();
                }
            };
        });
    }
}