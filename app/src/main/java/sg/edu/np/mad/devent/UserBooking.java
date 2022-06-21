package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
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

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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
                            Toast.makeText(UserBooking.this,"Image Failed to load.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

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


                if (bookingname.isEmpty() || bookingemail.isEmpty() || bookingnumber == null || bookingpax == null){
                    errormessage.setText("Kindly fill up the fields above");
                }
                else {
                    // Check for valid email
                    if (!bookingemail.matches(emailPattern)){
                        errormessage.setText("Kindly enter a valid email address");
                    }
                    // Check for negative booking pax
                    else if (bookingpax <= 0){
                        errormessage.setText("Kindly enter a valid number fo tickets to be booked");
                    }
                    else {
                        // Connect Database
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                        DatabaseReference Ref = database.getReference("Event");
                        DatabaseReference book = database.getReference("Booking");

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
                                    Intent profileData = new Intent(UserBooking.this,profile_page.class);
                                    profileData.putExtra("User_Email",userEmail);
                                    profileData.putExtra("EventID",eventid);
                                    startActivity(profileData);
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
            }

        });
    }
}