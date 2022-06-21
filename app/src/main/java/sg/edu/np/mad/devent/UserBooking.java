package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserBooking extends AppCompatActivity {
    String imageLinkfromEventDetails;
    String eventid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        //receive intent
        Intent fromEventDetailsPage = getIntent();
        //imageLinkfromEventDetails = fromEventDetailsPage.getStringExtra("EventPicture");
        eventid = fromEventDetailsPage.getStringExtra("EventID");

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

        // Set up an onclick listener for the submission of booking
        confirmbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookingname = userinputname.getText().toString();
                String bookingemail = userinputemail.getText().toString();
                Integer bookingnumber = Integer.parseInt(userinputcontact.getText().toString());
                Integer bookingpax = Integer.parseInt(userinputpax.getText().toString());

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

                        // Store the data in the user folders in the Event table
                        Ref.orderByChild("event_ID").equalTo().addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

        });
    }
}