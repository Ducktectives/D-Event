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

public class EventDetailsPage extends AppCompatActivity {
    ImageSlider imageSlider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        //receive Intent information
        Intent receiveEventAct = getIntent();
        String eventName = receiveEventAct.getStringExtra("event_Name");
        Log.d("maow",eventName);

        String eventID ="-N4yWQOhDz4yQ28BA3qr"; //to be changed when instance is passed

        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Event");

        //Using get to get info from database once, rather than setting an event listener
        Ref.child(eventID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data. Please reload.", task.getException());

                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().child("event_Name").getValue()));

                    //Retrieving Data from the
                    String eventNamedb = String.valueOf(task.getResult().child("event_Name").getValue());
                    String eventLocdb = String.valueOf(task.getResult().child("event_Location").getValue());
                    String eventDetaildb = String.valueOf(task.getResult().child("event_Details").getValue());
                    String eventDatedb = String.valueOf(task.getResult().child("event_Date").getValue());
                    String imageLink = String.valueOf(task.getResult().child("event_StorageReferenceID").getValue());


                    //set reference point for Firebase Storage
                    StorageReference firebaseStorage= FirebaseStorage.getInstance().getReference("images/" + imageLink);

                    //set the final load point for the image
                    ImageView eventPicture = findViewById(R.id.eventPicture);


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

                                    }
                                });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                    //set the content in the activity
                    //----For the Event Name
                    TextView eventName = findViewById(R.id.EventName);
                    eventName.setText(eventNamedb);
                    //----For the Event Location
                    TextView eventLocation = findViewById(R.id.EventLocation);
                    eventLocation.setText(eventLocdb);
                    //----For the details of the App
                    TextView eventDetail = findViewById(R.id.eventDetails);
                    eventDetail.setText(eventDetaildb);






                }
            }
        });

        //for slideshow of images
/*
        imageSlider =findViewById(R.id.imageslider);

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.a1,null));
        imageList.add(new SlideModel(R.drawable.a2,null));
        imageList.add(new SlideModel(R.drawable.a3,null));

        imageSlider.setImageList(imageList);
*/

        //event listener for viewing profile of event organiser
        TextView eventOrg = findViewById(R.id.EventOrganiser);
        eventOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewProfile = new Intent(EventDetailsPage.this,profile_page.class);
                Integer userID =0;
                viewProfile.putExtra("UserID",userID);
            }
        });


        //event listener for booking
        Button bookEvent = findViewById(R.id.bookbutton);
        bookEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent book = new Intent(EventDetailsPage.this,BookingPage.class);
                //startActivity(book);
            }
        });


        //get user's name - foreign key ref not too sure where 




    }
}