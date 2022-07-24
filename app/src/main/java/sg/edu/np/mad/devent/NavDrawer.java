package sg.edu.np.mad.devent;

import static sg.edu.np.mad.devent.R.id.nav_host_fragment_content_nav_drawer;
// import static sg.edu.np.mad.devent.RegistrationActivity.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.view.menu.MenuView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import sg.edu.np.mad.devent.databinding.ActivityNavDrawerBinding;
import sg.edu.np.mad.devent.databinding.FragmentGalleryBinding;

public class NavDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavDrawerBinding binding;
    public static String getemailofuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i1 = getIntent();
        getemailofuser = i1.getStringExtra("Email");
        String getusernameofuser = i1.getStringExtra("Username");
        String getuserprofileId = i1.getStringExtra("profile_id");

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
        // ^ Used for displaying bottom right icon of email

        // Sign out menu item's Alert dialog
        navigationView.getMenu().findItem(R.id.nav_signout).setOnMenuItemClickListener(menuItem -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(navigationView.getContext());
            builder.setTitle("Profile");
            builder.setMessage("Are you sure you want to sign out?");
            builder.setCancelable(false);
            builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Just change code below to whatever you gotta do
                    SharedPreferences.Editor editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
                    editor.remove("Email");
                    editor.remove("Hahedpass");
                    editor.apply();
                    Intent logoutAct = new Intent(NavDrawer.this, SplashScreen.class);
                    logoutAct.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(logoutAct);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
            return true;
        });

        // Setting an OnClick listener for Menu item "Settings"
        navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent settingAct = new Intent(NavDrawer.this, Settings.class);
                settingAct.putExtra("Email", getemailofuser);
                settingAct.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingAct);
                return true;
            }
        });

        Log.d("Profile ID at EventForm", String.valueOf(getemailofuser));
        Log.d("Profile ID at EventForm", "Do NOTE");

        // Setting an OnClick listener for Menu item "AddEvent"
        navigationView.getMenu().findItem(R.id.nav_AddEvent).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent eventFormAct = new Intent(NavDrawer.this, EventFormActivity.class);
                eventFormAct.putExtra("Email", getemailofuser);
                eventFormAct.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(eventFormAct);
                return true;
            }
        });

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
                i2.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i2);
            }
        });

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