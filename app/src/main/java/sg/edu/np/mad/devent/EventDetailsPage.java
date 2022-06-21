package sg.edu.np.mad.devent;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.denzcoskun.imageslider.ImageSlider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class EventDetailsPage extends AppCompatActivity {
    TextView eventOrg;
    String eventOrganizerEmail;
    String imageLink;
    String userEmail;
    String eventID;

    List<Events> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

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

        //Goes through the eventList to find the correct event
        for (Events ev : eventList){
            if (String.valueOf(ev.getEvent_ID()).equals(eventID)) {
                //Set the information - Event Name
                String eventNameFromList = ev.getEvent_Name(); //get
                eventName.setText(eventNameFromList); //set

                //Set the information - Event Description
                String eventDesc = ev.getEvent_Description(); //get
                eventDescription.setText(eventDesc); //set

                //Set the information - Event Location
                String eventLoc = ev.getEvent_Location(); //get
                eventLocation.setText("Postal Code: " + eventLoc); //set

                //Set the information - Event Details
                String eventDet= ev.getEvent_Detail(); //get
                eventDetail.setText(eventDet); //set


                //Set the information - Event Organizer
                eventOrganizerEmail = ev.getEvent_UserID(); //get

                //search for the user with the same email
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference Ref2 = database.getReference("Users");
                Ref2.child(eventOrganizerEmail).get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(EventDetailsPage.this,"No such user exists.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    String username = String.valueOf(task.getResult().child("username").getValue());
                                    eventOrg.setText("By " + username); //set
                                }
                            }
                        });

                //for image
                //For event banner/image
                imageLink = ev.getEvent_StorageReferenceID();

                //set reference point for Firebase Storage
                StorageReference firebaseStorage= FirebaseStorage.getInstance().getReference("images/" + imageLink);

                try {
                    File localfile = File.createTempFile("image",".jpg");
                    firebaseStorage.getFile(localfile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmapImage = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                    eventPicture.setImageBitmap(bitmapImage);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EventDetailsPage.this,"Image Failed to load.", Toast.LENGTH_SHORT).show();
                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //for date of event
                //For date of the event
                //String eventDatedb = String.valueOf(task.getResult().child("event_Date").getValue());


            }
        }


/*

                }
            }
        });




//        //event listener for viewing profile of event organiser
//        eventOrg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent profile = new Intent(EventDetailsPage.this, profile_page.class);
//                profile.putExtra("EventOrganizer", eventOrganizerEmail);
//                startActivity(profile);
//
//            }
//        });


        //event listener for booking
        Button bookEvent = findViewById(R.id.bookbutton);
        bookEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent book = new Intent(EventDetailsPage.this,UserBooking.class);
                Bundle bookingInfo = new Bundle();
                bookingInfo.putString("EventPicture",imageLink);
                bookingInfo.putString("User_Email",userEmail);
                bookingInfo.putString("Event",eventName);
                book.putExtras(bookingInfo);
                startActivity(book);
            }
        });
*/







    }

}