package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
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

        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Users");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer eventID = (Integer) dataSnapshot.child("EventID").getValue();
                Integer price = (Integer) dataSnapshot.child("Pricing").getValue();
                Log.d("aa", price + " / " + eventID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error","An error occured!");
            }

        };
        Ref.addListenerForSingleValueEvent(valueEventListener);

        imageSlider =findViewById(R.id.imageslider);

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.a1,null));
        imageList.add(new SlideModel(R.drawable.a2,null));
        imageList.add(new SlideModel(R.drawable.a3,null));

        imageSlider.setImageList(imageList);
    }
}