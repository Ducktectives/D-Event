package sg.edu.np.mad.devent;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.navBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navDraw = new Intent(MainActivity.this, NavDrawer.class);
                startActivity(navDraw);
            }
        });

        // Go to Login Page button
        Button login = findViewById(R.id.loginpage);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginpage = new Intent(MainActivity.this, loginpage.class);
                startActivity(loginpage);
            }
        });

        // Go to Profile button
        Button to_profile = findViewById(R.id.to_profile);
        to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open_profile = new Intent(MainActivity.this, profile_page.class);
                startActivity(open_profile);
            }
        });

        // Go to Settings button
        Button to_settings = findViewById(R.id.to_settings);
        to_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open_settings = new Intent(MainActivity.this, Settings.class);
                startActivity(open_settings);
            }
        });



        /*
        * TODO
        *
        * I need to get USERID so I know it was that user that posted the EVENT
        *
        * */
        // create the GET intent object
        Intent intent = getIntent();


        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        userID = intent.getStringExtra("profile_id");

        Log.d("Profile ID", String.valueOf(userID));
        // Go to EventFormActivity
        Button to_eventForm = findViewById(R.id.to_eventForm);
        to_eventForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent eventFormActivity = new Intent(MainActivity.this, EventFormActivity.class);
                eventFormActivity.putExtra("user_id", userID);
                startActivity(eventFormActivity);

            }
        });
    }

}