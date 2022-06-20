package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

public class profile_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // !!! Change this to get profile from database
        Profile p = new Profile("TestName","TestTitle","TestEmail"
                ,"arthurchongs@gmail.com",87978979,"sdwefewgew"
                );


        // Getting all the views as variables
        TextView EditDesc = findViewById(R.id.EditDesc);
        TextView UserDesc = findViewById(R.id.UserDescription);
        TextView UserName = findViewById(R.id.username);

        // Set default texts
        UserDesc.setText(p.Title);
        UserName.setText(p.Username);

        // !!! Make if else statement to only show edit desc if profile owner is viewing own profile
        // !!! Also should not show follow button on own profile
        // !!! Someone teach me how to use database
        Button followButton = findViewById(R.id.FollowButton);
        if(p.Id == "1") // Change ID to reflect actual user ID
        {
            followButton.setVisibility(View.VISIBLE);
        }
        else{
            followButton.setVisibility(View.INVISIBLE);
        }

        // !!! Make follow button do something
        // Wait where follow property of profile go



        // OnClickListener to start the edit description activity
        EditDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(profile_page.this,EditDescription.class);
                startActivity(edit);
            }
        });

        // Get new Description
        // Don't change if there is none set.
        Intent fromEdit = getIntent();
        Bundle newDesc = fromEdit.getExtras();
        if(newDesc != null){
            String setNewDesc = (String) newDesc.get("new");
            UserDesc.setText(setNewDesc);
            p.Title = setNewDesc;
        }

        // Setting past and upcoming events
        // Idk if to change this to Fragment or not
        // May be laggy when changing or my emulator is garbage
        GridView gridView = (GridView) findViewById(R.id.gallery);
        gridView.setAdapter(new ProfileAdapter(this));


            // Upcoming events
        Button upcoming = findViewById(R.id.UpcomingEvents);
        upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            gridView.setAdapter(new ProfileAdapter(profile_page.this));
            }
        });

            // Past events
        Button past = findViewById(R.id.PastEvents);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            gridView.setAdapter(new ProfileAdapter_Past(profile_page.this));
            }
        });

        // Make tapping on each image show their respective EventDetailsPage
        // Need database again
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDetails = new Intent
                        (profile_page.this,EventDetailsPage.class);
                startActivity(showDetails);
                Log.d("a","position is" + position);

                // Need to figure out what to do with the position.
                // Like how to link it with showing the actual event
            }
        });

        // Setting scrollview to start from the top
        ScrollView v =  findViewById(R.id.ProfileScroll);
        v.requestFocus();
        gridView.setFocusable(false);

        // Changing Password
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String value = extras.getString("new_pass");
            //p.setPassword(value);
        }

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile_page.this, NavDrawer.class));
            }
        });

    }
}
