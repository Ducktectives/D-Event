package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.annotation.NonNullApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserBooking extends AppCompatActivity {
    String imageLinkfromEventDetails;
    String userEmail;
    String eventid;
    String eventName;
    List<Events> eventList;
    String eventDate;
    String username;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        //Creation of notification channel
        createNotificationChannel();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        Booking userbooking = new Booking();

        //receive intent
        Intent fromEventDetailsPage = getIntent();
        imageLinkfromEventDetails = fromEventDetailsPage.getStringExtra("EventPicture");
        userEmail = user.getEmail();
        eventid = fromEventDetailsPage.getStringExtra("Event");
        eventDate = fromEventDetailsPage.getStringExtra("EventDate");
        eventList = (List<Events>) fromEventDetailsPage.getSerializableExtra("EventList");
        eventName = fromEventDetailsPage.getStringExtra("EventName");

        // Assign the texts, buttons and images to a variable to be called
        ImageView eventimage = (ImageView)findViewById(R.id.eventimage);
        Button confirmbooking = (Button)findViewById(R.id.confirmbooking);
        EditText userinputname = (EditText) findViewById(R.id.UserBookingName);
        EditText userinputemail = (EditText)findViewById(R.id.UserBookingEmail);
        EditText userinputcontact = (EditText)findViewById(R.id.UserBookingPhone);
        TextView userinputpax = (TextView) findViewById(R.id.TicketCounter);
        TextView errormessage = (TextView)findViewById(R.id.Errormessage);
        ImageButton addticket = (ImageButton)findViewById(R.id.TicketAdd);
        ImageButton removeticket = (ImageButton)findViewById(R.id.TicketDeduct);


        String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]";
        String namePattern = "^[a-zA-Z- ]{3,30}";


        // Ticket Adding Button to add tickets to the count
        final Integer[] numoftix = new Integer[1];

        addticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numoftix[0] = Integer.parseInt(userinputpax.getText().toString());
                if (numoftix[0] == 4){
                    numoftix[0] = 4;
                    Toast.makeText(UserBooking.this, "You have hit the maximum number of tickets you can book", Toast.LENGTH_SHORT).show();
                    userinputpax.setText(numoftix[0].toString());
                }
                else {
                    numoftix[0] += 1;
                    userinputpax.setText(numoftix[0].toString());
                }
            }
        });

        // Ticket Removal Button to remove tickets from the count
        removeticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numoftix[0] = Integer.parseInt(userinputpax.getText().toString());
                if (numoftix[0] == 1){
                    numoftix[0] = 1;
                    userinputpax.setText(numoftix[0].toString());
                    Toast.makeText(UserBooking.this, "1 is the minimum number of tickets that can be booked", Toast.LENGTH_SHORT).show();
                }
                else {
                    numoftix[0] -= 1;
                    userinputpax.setText(numoftix[0].toString());
                }
            }
        });

        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Users");

        //Using get to get info from database once, rather than setting an event listener
        // Getting the values required to authenticate the user
        Ref.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data. Please reload.", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().child("contactnum").getValue()));
                    String contactnum = String.valueOf(task.getResult().child("contactnum").getValue());
                    username = String.valueOf(task.getResult().child("username").getValue());

                    userinputname.post(new Runnable(){
                        @Override
                        public void run() {
                            userinputname.setText(username);
                        }
                    });
                    userinputemail.setText(userEmail);
                    userinputcontact.post(new Runnable(){
                        @Override
                        public void run() {
                            userinputcontact.setText(contactnum);
                        }
                    });
                }
            }
        });

        // Get the event image
        //set reference point for Firebase Storage
        StorageReference firebaseStorage= FirebaseStorage.getInstance().getReference("images/" + imageLinkfromEventDetails);

        try {
            File localfile = File.createTempFile("image",".jpg");
            firebaseStorage.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmapImage = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            eventimage.setImageBitmap(bitmapImage);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            eventimage.setImageResource(R.drawable.ducketive);
                        }
                    });

        } catch (IOException e) {

        }

        //Auto Filling of details
        userinputemail.setText(userEmail);

        //search for the user with the same email
        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref2 = database.getReference("Users");
        Ref2.equalTo(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(UserBooking.this,"Please try booking again", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String username = String.valueOf(task.getResult().child("username").getValue());
                            userinputname.setText(username);

                            String contactNo = String.valueOf(task.getResult().child("contactnum").getValue());
                            userinputcontact.setText(contactNo);
                        }
                    }
                });


        // Set up an onclick listener for the submission of booking
        confirmbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer bookingnumber;
                Integer bookingpax;
                String bookingname = userinputname.getText().toString();
                String bookingemail = userinputemail.getText().toString();
                try {
                    bookingnumber = Integer.parseInt(userinputcontact.getText().toString());
                }
                catch (Exception e){
                    bookingnumber = null;
                }
                try {
                    bookingpax = Integer.parseInt(userinputpax.getText().toString());
                }
                catch (Exception e){
                    bookingpax = null;
                }

                // Check for a filled name field which does not consist of invalid characters
                if (bookingname.isEmpty()){
                    errormessage.setText("Name is required");
                }
                else if (!bookingname.matches(namePattern)){
                    errormessage.setText("Kindly enter a valid name");
                }
                // Check for valid email
                else if (bookingemail.isEmpty()){
                    errormessage.setText("Email is required");
                }
                else if (!bookingemail.matches(emailPattern)){
                    errormessage.setText("Kindly enter a valid email address");
                }
                // Check for valid booking contact
                else if (bookingnumber == null){
                    errormessage.setText("Contact Number is required");
                }
                else if (!((bookingnumber < 100000000 && bookingnumber >= 80000000) || (bookingnumber >= 60000000 && bookingnumber < 70000000))) {
                    errormessage.setText("Kindly enter a valid contact");
                }
                else {
                    // Connect Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference Ref = database.getReference("Event");
                    DatabaseReference book = database.getReference("Booking");
                    DatabaseReference user = database.getReference("Users");

                    int finalbookingnumber = bookingnumber;
                    int finalbookingpax = bookingpax;

                    // Store the data in the user folders in the Event table
                    Ref.orderByChild("event_ID").equalTo(eventid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Check for event ID
                            if (snapshot.exists()){

                                userbooking.Name = bookingname;
                                userbooking.Email = bookingemail;
                                userbooking.Contact = finalbookingnumber;
                                userbooking.Pax = finalbookingpax;

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date today = Calendar.getInstance().getTime();
                                String todayString = sdf.format(today);


                                // Create subfolder in the event and name the subfolder booking and create another subfolder within with the booking email as the key to store the record
                                book.child(eventid).child(bookingemail.toLowerCase().replace(".", "")).setValue(userbooking);
                                book.child(eventid).child(bookingemail.toLowerCase().replace(".","")).child("DayOrdered").setValue(todayString);
                                // Let user know that Booking is successful
                                Toast.makeText(getApplicationContext(), "Booking Made Successfully", Toast.LENGTH_LONG).show();

                                //Add event to the list of booked events
                                user.child(userID).child("event_booked").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        Integer numberChild = Math.toIntExact(task.getResult().getChildrenCount());
                                        Integer storeID = numberChild + 1 ;

//                                        Hao Zhong's change to use UUID upon booking
//                                        user.child(bookingemail.toLowerCase().replace(".", "")).child("event_booked").child(String.valueOf(storeID)).setValue(eventid);
                                        user.child(userID).child("event_booked").child(String.valueOf(storeID)).setValue(eventid);
//                                          end change


                                        //Pass intent into the Profile Page
                                        Intent profileData = new Intent(UserBooking.this,BookingSummary.class);
                                        Bundle profileDatas = new Bundle();
                                        profileDatas.putString("EventID",eventid);
                                        profileDatas.putString("Email",userEmail);
                                        profileDatas.putString("Name",bookingname);
                                        profileDatas.putString("UserEmail",bookingemail);
                                        profileDatas.putInt("ContactNum",finalbookingnumber);
                                        profileDatas.putInt("NumberofTix",finalbookingpax);
                                        profileDatas.putString("EventName",eventName);
                                        profileData.putExtras(profileDatas);
                                        profileData.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(profileData);
                                    }
                                });

                            }
                            else {
                                // Let user know that Booking is Unsuccessful
                                Toast.makeText(getApplicationContext(), "Booking Made Unsuccessfully, Please try again", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Let user know that Booking is Unsuccessful
                            Toast.makeText(getApplicationContext(), "Booking Made Unsuccessfully, Please try again", Toast.LENGTH_LONG).show();
                        }
                    });

                    // Code below is used to set up notification for the user on the day of the event
                    Toast.makeText(UserBooking.this,"Reminder has been set!", Toast.LENGTH_LONG).show();
                    Intent notifyIntent = new Intent(UserBooking.this, NotifyService.class);
                    notifyIntent.putExtra("eventDate", eventDate);
                    notifyIntent.putExtra("eventName", eventName);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(UserBooking.this, 0,
                            notifyIntent, 0);

                    long timeAtButtonClick = System.currentTimeMillis();
                    long seconds = 5000; // 10 seconds,
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + seconds, pendingIntent);

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