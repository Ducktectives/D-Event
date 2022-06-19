package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventDetailsPage extends AppCompatActivity {
    ImageSlider imageSlider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        String eventID ="-N4qHSwXLyl0PNG654qf"; //to be changed when instance is passed

        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Event");
        DatabaseReference Ref2 = database.getReference("Users");

        //Using get to get info from database once, rather than setting an event listener
        Ref.child(eventID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data. Please reload.", task.getException());

                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().child("event_Name").getValue()));

                    //Retrieving Data
                    String eventNamedb = String.valueOf(task.getResult().child("event_Name").getValue());
                    String eventLocdb = String.valueOf(task.getResult().child("event_Location").getValue());
                    String eventDetaildb = String.valueOf(task.getResult().child("event_Details").getValue());
                    String eventDatedb = String.valueOf(task.getResult().child("event_Date").getValue());

                    //set the content in the activity
                    TextView eventName = findViewById(R.id.EventName);
                    eventName.setText(eventNamedb);
                    TextView eventLocation = findViewById(R.id.EventLocation);
                    eventLocation.setText(eventLocdb);
                    TextView eventDetail = findViewById(R.id.eventDetails);
                    eventDetail.setText(eventDetaildb);

                }
            }
        });

        //for slideshow of images
        imageSlider =findViewById(R.id.imageslider);

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.a1,null));
        imageList.add(new SlideModel(R.drawable.a2,null));
        imageList.add(new SlideModel(R.drawable.a3,null));

        imageSlider.setImageList(imageList);

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