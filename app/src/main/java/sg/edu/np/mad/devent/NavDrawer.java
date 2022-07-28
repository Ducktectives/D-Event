package sg.edu.np.mad.devent;

import static sg.edu.np.mad.devent.R.id.nav_host_fragment_content_nav_drawer;
// import static sg.edu.np.mad.devent.RegistrationActivity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.devent.databinding.ActivityNavDrawerBinding;
import sg.edu.np.mad.devent.databinding.FragmentGalleryBinding;
import sg.edu.np.mad.devent.ui.home.HomeFragment;
import sg.edu.np.mad.devent.ui.home.HomeGridAdapter;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavDrawerBinding binding;
    public static String getuserprofileId, getemailofuser, getusernameofuser;

    /* Arthur Edit */
    private FirebaseUser user;
    private String userID;
    private NavigationView nav_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityNavDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Intent i1 = getIntent();
//        getemailofuser = i1.getStringExtra("Email");
//        String getusernameofuser = i1.getStringExtra("Username");
//        String getuserprofileId = i1.getStringExtra("profile_id");

        /* Arthur Edit */
        nav_view = (NavigationView) findViewById(R.id.nav_view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        getuserprofileId = user.getUid();
        getemailofuser = user.getEmail();

        Toast.makeText(this, "User ID : " + user.toString(), Toast.LENGTH_SHORT).show();

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference Ref = database.getReference("Users");




            Ref.equalTo(getuserprofileId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                            // Getting the values required to authenticate the user
                            Ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data. Please reload.", task.getException());
                                    }
                                    else {
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                        getusernameofuser = task.getResult().child("username").getValue(String.class);
                                    }
                                }
                            });
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        setSupportActionBar(binding.appBarNavDrawer.toolbar);
        /*
        binding.appBarNavDrawer.fab.setOnClickListener(new View.OnClickListener() { // for the bottom right email icon
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_AddEvent) // R.id.nav_followed
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_drawer);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.nav_signout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(nav_view.getContext());
                        builder.setTitle("Profile");
                        builder.setMessage("Are you sure you want to sign out?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Just change code below to whatever you gotta do
                                // Intent testAct = new Intent(NavDrawer.this, loginpage.class);
                                //startActivity(testAct);
                                AuthUI.getInstance()
                                        .signOut(NavDrawer.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task){
                                                Toast.makeText(NavDrawer.this,"Signed Out Complete", Toast.LENGTH_SHORT).show();
                                                // below line is to go to MainActivity via an intent.
                                                Intent i = new Intent(NavDrawer.this, LoginActivity.class);
                                                startActivity(i);
                                            }
                                        });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder.show();

                        break;
                    case R.id.nav_settings:
                        Intent settingAct = new Intent(NavDrawer.this, Settings.class);
                        settingAct.putExtra("Email", getemailofuser);
                        startActivity(settingAct);

                        break;
                    case R.id.nav_AddEvent:
                        Intent eventForm = new Intent(NavDrawer.this, EventFormActivity.class);
                        startActivity(eventForm);

                        break;
                    case R.id.nav_eventList:
                        Intent eventList = new Intent(NavDrawer.this, EventListActivity.class);
                        startActivity(eventList);

                        break;

                    case R.id.nav_stats:
                        Intent stats = new Intent(NavDrawer.this,Statistics.class);
                        stats.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(stats);

                        break;

                }
                return true;
            }
        });
        // ^ Used for displaying bottom right icon of email

        // Sign out menu item's Alert dialog
//        navigationView.getMenu().findItem(R.id.nav_signout).setOnMenuItemClickListener(menuItem -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(navigationView.getContext());
//            builder.setTitle("Profile");
//            builder.setMessage("Are you sure you want to sign out?");
//            builder.setCancelable(false);
//            builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
                    // Just change code below to whatever you gotta do
                    // Intent testAct = new Intent(NavDrawer.this, loginpage.class);
                    //startActivity(testAct);
//                    AuthUI.getInstance()
//                            .signOut(NavDrawer.this).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                public void onComplete(@NonNull Task<Void> task){
//                                    Toast.makeText(NavDrawer.this,"User Signed Out", Toast.LENGTH_SHORT).show();
//                                    // below line is to go to MainActivity via an intent.
//                                    Intent i = new Intent(NavDrawer.this, LoginActivity.class);
//                                    startActivity(i);
//                                }
//                            });
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                }
//            });
//            builder.show();
//            return true;
//        });
//
//        // Setting an OnClick listener for Menu item "Settings"
//        navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Intent settingAct = new Intent(NavDrawer.this, Settings.class);
//                settingAct.putExtra("Email", getemailofuser);
//                startActivity(settingAct);
//                return true;
//            }
//        });
//
//        Log.d("Profile ID at EventForm", String.valueOf(getemailofuser));
//        Log.d("Profile ID at EventForm", "Do NOTE");
//
//        // Setting an OnClick listener for Menu item "AddEvent"
//        navigationView.getMenu().findItem(R.id.nav_AddEvent).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Intent eventFormAct = new Intent(NavDrawer.this, EventFormActivity.class);
//                eventFormAct.putExtra("Email", getemailofuser);
//                startActivity(eventFormAct);
//                return true;
//            }
//        });
//
//        // Setting an OnClick listener for Menu item "My Own Event List"
//        navigationView.getMenu().findItem(R.id.update_event_list).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Intent eventFormAct = new Intent(NavDrawer.this, EventFormActivity.class);
//                eventFormAct.putExtra("Email", getemailofuser);
//                startActivity(eventFormAct);
//                return true;
//            }
//        });


        // NAV HEADER
        // Used for displaying profile pic / Username / email in nav header
        View navHeader = navigationView.getHeaderView(0);
        // Pull data of user and display *** ! IMPORTANT COME BACK TO THIS LATER
        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(NavDrawer.this, profile_page.class);
                i2.putExtra("Username_forprofile", getusernameofuser);
                i2.putExtra("Email", getemailofuser);
                i2.putExtra("profile_id_forprofile", getuserprofileId);
                startActivity(i2);
            }
        });
//
        TextView username = (TextView) navHeader.findViewById(R.id.nav_username);
        username.setText(getusernameofuser);

        TextView email = (TextView) navHeader.findViewById(R.id.nav_email);
        email.setText(getemailofuser);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, nav_host_fragment_content_nav_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}