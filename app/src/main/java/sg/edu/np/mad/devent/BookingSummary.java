package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        Button Close = (Button)findViewById(R.id.ConfirmationClose);
        Button AddtoCalender = (Button)findViewById(R.id.BookingAddCalander);

        // Get the intent from the previous activity and setting the values
        Intent autofill = getIntent();
        String eventid = autofill.getStringExtra("EventID");
        String Email = autofill.getStringExtra("Email");
        EventName.setText(autofill.getStringExtra("EventName"));
        Name.setText(autofill.getStringExtra("Name"));
        UserEmail.setText(autofill.getStringExtra("UserEmail"));
        ContactNum.setText(autofill.getStringExtra("ContactNum"));
        NumofTix.setText(autofill.getStringExtra("NumofTix"));

        // End the activity and go back to the profile page
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(BookingSummary.this, profile_page.class);
                i2.putExtra("Email", Email);
                finishAndRemoveTask();
                startActivity(i2);
            }
        });

        // Add the Event and Event details to the Calender application
        AddtoCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        String eventEndTime = task.getResult().child("event_StartTime").getValue(String.class);

                                        // Assigning the value to put in the Calendar
                                        Intent Calender = new Intent(Intent.ACTION_INSERT);
                                        Calender.setData(CalendarContract.Events.CONTENT_URI);
                                        Calender.putExtra(CalendarContract.Events.TITLE, eventname);
                                        Calender.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStartTime);
                                        Calender.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndTime);
                                        Calender.putExtra(CalendarContract.Events.EVENT_LOCATION, eventlocation);
                                        Calender.putExtra(CalendarContract.Events.DESCRIPTION, eventdescription);
                                        Calender.putExtra(CalendarContract.Events.AVAILABILITY, "Busy");

                                        if (Calender.resolveActivity(getPackageManager()) != null){
                                            startActivity(Calender);
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
            }
        });
    }
}