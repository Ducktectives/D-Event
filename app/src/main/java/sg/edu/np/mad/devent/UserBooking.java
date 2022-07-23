package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.annotation.NonNullApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class UserBooking extends AppCompatActivity {
    String imageLinkfromEventDetails;
    String userEmail;
    String eventid;
    List<Events> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        Booking userbooking = new Booking();

        //receive intent
        Intent fromEventDetailsPage = getIntent();
        imageLinkfromEventDetails = fromEventDetailsPage.getStringExtra("EventPicture");
        userEmail = fromEventDetailsPage.getStringExtra("User_Email");
        eventid = fromEventDetailsPage.getStringExtra("Event");
        eventList = (List<Events>) fromEventDetailsPage.getSerializableExtra("EventList");

        // Assign the texts, buttons and images to a variable to be called
        ImageView eventimage = (ImageView)findViewById(R.id.eventimage);
        Button confirmbooking = (Button)findViewById(R.id.confirmbooking);
        EditText userinputname = (EditText)findViewById(R.id.UserBookingName);
        EditText userinputemail = (EditText)findViewById(R.id.UserBookingEmail);
        EditText userinputcontact = (EditText)findViewById(R.id.UserBookingPhone);
        EditText userinputpax = (EditText)findViewById(R.id.UserBookingPax);
        TextView errormessage = (TextView)findViewById(R.id.Errormessage);


        String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]";
        String namePattern = "^[a-zA-Z- ]{3,30}";

        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Users");

        //Using get to get info from database once, rather than setting an event listener
        Ref.orderByChild("email").equalTo(userEmail.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Getting the values required to authenticate the user
                    Ref.child(userEmail.toLowerCase().replace(".", "")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data. Please reload.", task.getException());
                            }
                            else {
                                Log.d("firebase", String.valueOf(task.getResult().child("username").getValue()));
                                Integer contactnum = task.getResult().child("contactnum").getValue(Integer.class);
                                String username = task.getResult().child("username").getValue(String.class);

                                userinputname.setText(username);
                                userinputemail.setText(userEmail);
                                userinputcontact.setText(contactnum.toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref2 = database.getReference("Users");
        Ref2.child(userEmail.replace(".","").toLowerCase()).get()
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
                // Check for negative booking pax or booking for more that 4 pax
                else if (bookingpax == null ){
                    errormessage.setText("Number of Tickets is required");
                }
                else if (bookingpax <= 0){
                    errormessage.setText("Kindly enter a valid number of tickets to be booked");
                }
                else if (bookingpax > 4){
                    errormessage.setText("Sorry the maximum number of tickets you can book is 4");
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
                                // Create subfolder in the event and name the subfolder booking and create another subfolder within with the booking email as the key to store the record
                                book.child(eventid).child(bookingemail.toLowerCase().replace(".", "")).setValue(userbooking);
                                // Let user know that Booking is successful
                                Toast.makeText(getApplicationContext(), "Booking Successfully", Toast.LENGTH_LONG).show();


                                //Add event to the list of booked events
                                user.child(bookingemail.toLowerCase().replace(".", "")).child("event_booked").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        Integer numberChild = Math.toIntExact(task.getResult().getChildrenCount());
                                        Integer storeID = numberChild +1 ;
                                        user.child(bookingemail.toLowerCase().replace(".", "")).child("event_booked").child(String.valueOf(storeID)).setValue(eventid);

                                        //Pass intent into the Profile Page
                                        Intent profileData = new Intent(UserBooking.this,BookingSummary.class);
                                        profileData.putExtra("Email",userEmail);
                                        profileData.putExtra("EventName",userEmail);
                                        profileData.putExtra("Name",userEmail);
                                        profileData.putExtra("UserEmail",userEmail);
                                        profileData.putExtra("ContactNum",userEmail);
                                        profileData.putExtra("NumofTix",userEmail);
                                        finishAndRemoveTask();
                                        startActivity(profileData);
                                    }
                                });

                            }
                            else {
                                // Let user know that Booking is Unsuccessful
                                Toast.makeText(getApplicationContext(), "Booking Unsuccessfully, Please try again", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Let user know that Booking is Unsuccessful
                            Toast.makeText(getApplicationContext(), "Booking Unsuccessfully, Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}