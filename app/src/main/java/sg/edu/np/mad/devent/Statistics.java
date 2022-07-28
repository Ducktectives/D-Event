package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Statistics extends AppCompatActivity {

    TextView noOfEvents = findViewById(R.id.NoNumberOfEvents);
    TextView noOfTickets = findViewById(R.id.NoNumberOfTickets);
    BarChart barChart;

    // Firebase stuff
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference("Users");
    DatabaseReference bookings_path = database.getReference("Booking");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();

    List<String> eventsIDList = new ArrayList<>();
    List<Events> createdEvents = new ArrayList<>();
    List<String> createdEventsID = new ArrayList<>();
    Integer totalPax = 0;
    Date orderedDate;
    long dayDifference;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        // Looking through all events
        event_path.orderByChild("event_UserID").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String eventID = snapshot.child("event_ID").getValue(String.class);
                String eventTitle = snapshot.child("event_Name").getValue(String.class);
                String eventLoc = snapshot.child("event_Location").getValue(String.class);
                String eventDate = snapshot.child("event_Date").getValue(String.class);
                String eventDesc = snapshot.child("event_Description").getValue(String.class);
                String eventDetail = snapshot.child("event_Detail").getValue(String.class);
                String eventUserID = snapshot.child("event_UserID").getValue(String.class);
                Boolean eventBooked = snapshot.child("bookmarked").getValue(Boolean.class);
                String eventStorageID = snapshot.child("event_StorageReferenceID").getValue(String.class);
                String eventStartTime = snapshot.child("event_StartTime").getValue(String.class);
                String eventEndTime = snapshot.child("event_EndTime").getValue(String.class);

                // Meant to prevent duplication of data display in gridAdapter
                if (eventsIDList.contains(eventID)) return;

                List<String> eventType = Arrays.asList(eventDetail.replaceAll("\\s+","").split(", "));
                // Removes all whitespaces and non-visible characters, (\n, tab) and splits them into a list


                Events event = new Events(eventID,eventTitle, eventLoc, eventDate, eventDesc,
                        eventDetail, eventStartTime, eventEndTime, eventUserID, eventStorageID, eventBooked);

                if(eventUserID == userID){
                    createdEvents.add(event);
                    createdEventsID.add(event.getEvent_ID());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Looking through all bookings that user has created
        // Find number of tickets sold from all events

        // Get last weeks date and todays date
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date lastWeek = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        Date today = new Date();

        // Create list with all the dates in between
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<Date> dates = new ArrayList<>();
        while(!lastWeek.after(today)){
            dates.add(lastWeek);
            toCalendar(lastWeek).add(Calendar.DATE,1);
        }

        // Create list for all the tickets
        dayDifference = today.getTime()-lastWeek.getTime();
        dayDifference = TimeUnit.DAYS.convert(dayDifference,TimeUnit.MILLISECONDS); // why did i do this
        List<Integer> paxPerDay = new ArrayList<>(7);

        for(String eventID : eventsIDList){
            bookings_path.child(eventID).orderByChild("Pax").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Integer pax = snapshot.child("Pax").getValue(Integer.class);
                    String orderedDateString = snapshot.child("DayOrdered").getValue(String.class);
                    try {
                        orderedDate = sdf.parse(orderedDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(dates.contains(orderedDate)){
                        dayDifference = TimeUnit.DAYS.convert(dayDifference,TimeUnit.MILLISECONDS);
                        paxPerDay.set((int) dayDifference,paxPerDay.get((int) dayDifference) + pax);
                    }
                    totalPax += pax;
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        noOfEvents.setText(eventsIDList.size());
        noOfTickets.setText(totalPax);

        barChart = (BarChart) findViewById(R.id.statsgraph);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i=0; i < paxPerDay.size(); i ++){
            barEntries.add(new BarEntry(paxPerDay.get(i),i));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Tickets sold");

        BarData theData = new BarData((IBarDataSet) dates,barDataSet);
        barChart.setData(theData);

        barChart.setDragEnabled(true);
        barChart.setTouchEnabled(true);
        barChart.setScaleEnabled(true);


    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

}