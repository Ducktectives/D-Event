package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class UserBooking extends AppCompatActivity {
    String imageLinkfromEventDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        //receive intent
        Intent fromEventDetailsPage = getIntent();
        imageLinkfromEventDetails = fromEventDetailsPage.getStringExtra("EventPicture");


    }
}