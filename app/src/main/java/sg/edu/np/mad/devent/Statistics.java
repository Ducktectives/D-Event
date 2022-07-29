package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
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

import sg.edu.np.mad.devent.ui.home.HomeFragment;

public class Statistics extends AppCompatActivity {

    TextView noOfEvents;
    TextView noOfTickets;

    BarChart barChart;


    // Firebase stuff
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference("Users");
    DatabaseReference bookings_path = database.getReference("Booking");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();

    static List<String> eventsIDList = new ArrayList<>();
    static List<Events> createdEvents = new ArrayList<>();
    static List<String> createdEventsID = new ArrayList<>();
    Integer totalPax = 0;
    Date orderedDate;
    long dayDifference;
    Boolean flag = false;
    static List<Events> eventsList = new ArrayList<>();
    twoLists twoLists = new twoLists();
    static List<Integer> paxPerDay = new ArrayList<>(7);
    static ArrayList<String> dates = new ArrayList<>();
    Integer pax;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        noOfEvents = (TextView) findViewById(R.id.NoNumberOfEvents);
        noOfTickets = (TextView) findViewById(R.id.NoNumberOfTickets);
        // Looking through all events
        event_path.orderByChild("event_ID").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String eventID = snapshot.child("event_ID").getValue(String.class);

                String eventUserID = snapshot.child("event_UserID").getValue(String.class);

                pax += snapshot.child("totalPax").getValue(Integer.class);


                List<String> eventTypes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.child("eventTypes").getChildren()) {
                    eventTypes.add(dataSnapshot.getValue(String.class));
                }
                ;
                Log.d("eventUserID", eventUserID);
                Log.d("userID", userID);

                // Meant to prevent duplication of data display in gridAdapter
                if (eventsIDList.contains(eventID)) return;


//                Events event = new Events(eventID, eventTitle, eventLoc, eventDate, eventDesc,
//                        eventDetail, eventStartTime, eventEndTime, eventUserID, eventStorageID, eventBooked, eventTicketPrice, eventTypes);
//
//                eventsIDList.add(eventID);
//                eventsList.add(event);

                if (String.valueOf(eventUserID).equals(String.valueOf(userID))) {
//                        createdEvents.add(event);
                    createdEventsID.add(eventID);
                    Log.d("createdEvents", "" + createdEvents.size());
                    Log.d("createdEventsID", "" + createdEventsID.size());
                    Log.d("sizelmao", "" + createdEventsID);
                    noOfEvents.setText(String.valueOf(createdEventsID.size()));
                    noOfTickets.setText(String.valueOf(totalPax));


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

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date lastWeek = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        Date today = new Date();
        Calendar lastWeekCal = toCalendar(lastWeek);
        Calendar todayCal = toCalendar(today);
//         Get last weeks date and todays date

        // Create list with all the dates in between
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        while (!lastWeekCal.after(todayCal)) {
            // adding this if statement breaks it for some reason
//            if(!lastWeekCal.equals(todayCal)){
            try {
                Log.d("lastweek", "" + sdf.parse(sdf.format(lastWeekCal.getTime())));
                Log.d("lastweek2", "" + (sdf.format(lastWeekCal.getTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dates.add(sdf.format(lastWeekCal.getTime()));
            lastWeekCal.add(Calendar.DATE, 1);
//            }
        }
        // Create list for all the tickets
        dayDifference = today.getTime() - lastWeek.getTime();
        dayDifference = TimeUnit.DAYS.convert(dayDifference, TimeUnit.MILLISECONDS); // why did i do this


        Log.d("createdEventsID", "" + createdEventsID);
        for (String eventID : createdEventsID) {
            Log.d("eventIDis", "" + eventID);
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
                    if (dates.contains(orderedDate)) {
                        dayDifference = TimeUnit.DAYS.convert(dayDifference, TimeUnit.MILLISECONDS);
                        paxPerDay.set((int) dayDifference, paxPerDay.get((int) dayDifference) + pax);
                    }
                    totalPax += pax;
                    Log.d("totalPax", "" + totalPax);


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



//        twoLists datas = null;
//        createdEventsID = getEventData();
//        datas = getBookingData();

//        List<Integer> paxPerDay = datas.paxPerDays;
//        ArrayList<String> dates = datas.datess;



            barChart = (BarChart) findViewById(R.id.statsgraph);
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0; i < paxPerDay.size(); i++) {
                barEntries.add(new BarEntry(paxPerDay.get(i), i));
            }
            BarDataSet barDataSet = new BarDataSet(barEntries, "Tickets sold");

            BarData theData = new BarData(dates, barDataSet);
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

    public class twoLists{
        List<Integer> paxPerDays;
        ArrayList<String> datess;
    }


//    public twoLists getBookingData(){
//
//        twoLists twoLists = new twoLists();
//        List<Integer> paxPerDay = new ArrayList<>(7);
//        ArrayList<String> dates = new ArrayList<>();
////        synchronized (this) {
////                eventsIDList = getEventData();
////                flag = !flag;
////        }
////        synchronized (this) {
////            while (!flag) {
////                wait();
////            }
//
//
//            //         Looking through all bookings that user has created
////         Find number of tickets sold from all events
//
////         Get last weeks date and todays date
//            long DAY_IN_MS = 1000 * 60 * 60 * 24;
//            Date lastWeek = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
//            Date today = new Date();
//            Calendar lastWeekCal = toCalendar(lastWeek);
//            Calendar todayCal = toCalendar(today);
////         Get last weeks date and todays date
//
//            // Create list with all the dates in between
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            while (!lastWeekCal.after(todayCal)) {
//                // adding this if statement breaks it for some reason
////            if(!lastWeekCal.equals(todayCal)){
//                try {
//                    Log.d("lastweek", "" + sdf.parse(sdf.format(lastWeekCal.getTime())));
//                    Log.d("lastweek2", "" + (sdf.format(lastWeekCal.getTime())));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                dates.add(sdf.format(lastWeekCal.getTime()));
//                lastWeekCal.add(Calendar.DATE, 1);
////            }
//            }
//            // Create list for all the tickets
//            dayDifference = today.getTime() - lastWeek.getTime();
//            dayDifference = TimeUnit.DAYS.convert(dayDifference, TimeUnit.MILLISECONDS); // why did i do this
//
//
//            Log.d("createdEventsID", "" + createdEventsID);
//            for (String eventID : createdEventsID) {
//                Log.d("eventIDis", "" + eventID);
//                bookings_path.child(eventID).orderByChild("Pax").addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Integer pax = snapshot.child("Pax").getValue(Integer.class);
//                        String orderedDateString = snapshot.child("DayOrdered").getValue(String.class);
//                        try {
//                            orderedDate = sdf.parse(orderedDateString);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        if (dates.contains(orderedDate)) {
//                            dayDifference = TimeUnit.DAYS.convert(dayDifference, TimeUnit.MILLISECONDS);
//                            paxPerDay.set((int) dayDifference, paxPerDay.get((int) dayDifference) + pax);
//                        }
//                        totalPax += pax;
//                        Log.d("totalPax", "" + totalPax);
//                        noOfTickets.setText(String.valueOf(totalPax));
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//            twoLists.paxPerDays = paxPerDay;
//            twoLists.datess = dates;
//            return twoLists;
//        }
//
//
//
//
//    public List<String> getEventData(){
//
//        // Looking through all events
//        event_path.orderByChild("event_ID").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                String eventID = snapshot.child("event_ID").getValue(String.class);
//
//                String eventUserID = snapshot.child("event_UserID").getValue(String.class);
//
//
//
//                List<String> eventTypes = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : snapshot.child("eventTypes").getChildren()) {
//                    eventTypes.add(dataSnapshot.getValue(String.class));
//                }
//                ;
//                Log.d("eventUserID", eventUserID);
//                Log.d("userID", userID);
//
//                // Meant to prevent duplication of data display in gridAdapter
//                if (eventsIDList.contains(eventID)) return;
//
//
////                Events event = new Events(eventID, eventTitle, eventLoc, eventDate, eventDesc,
////                        eventDetail, eventStartTime, eventEndTime, eventUserID, eventStorageID, eventBooked, eventTicketPrice, eventTypes);
////
////                eventsIDList.add(eventID);
////                eventsList.add(event);
//
//                    if (String.valueOf(eventUserID).equals(String.valueOf(userID))) {
////                        createdEvents.add(event);
//                        createdEventsID.add(eventID);
//                        Log.d("createdEvents", "" + createdEvents.size());
//                        Log.d("createdEventsID", "" + createdEventsID.size());
//                        Log.d("sizelmao", "" + createdEventsID);
//                        noOfEvents.setText(String.valueOf(createdEventsID.size()));
//
//                   }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });
//
//        Log.d("returnCreate", "" + createdEventsID);
//            return createdEventsID;
//
//    }

}