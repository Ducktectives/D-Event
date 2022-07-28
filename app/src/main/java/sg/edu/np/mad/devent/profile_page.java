package sg.edu.np.mad.devent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class profile_page extends AppCompatActivity {

    Profile p = new Profile();
    Integer saltvalue;
    String userid;
    String username;
    String usertitle;
    String useremail;
    Integer usercontact;
    String userpass;
    Uri new_img;
    String user_id_unique;
    int[] imageId;
    String[] web;
    String events_booked;
    GridView grid;
    List<String> eventsBookedList;
    List<Events> DBevents = new ArrayList<>();
    List<String> eventsIDList = new ArrayList<>();
    List<Events> pastDBevents = new ArrayList<>();
    List<String> pastEventsIDList = new ArrayList<>();
    String instaURL;
    String facebookURL;
    String linkedinURL;
    String websiteURL;


    // Firebase stuff
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference("Users");
    DatabaseReference bookings_path = database.getReference("Booking");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button toNavDrawer = findViewById(R.id.backToNavDrawer);

        // Setting scrollview to start from the top
        GridView gridView = (GridView) findViewById(R.id.gallery);
        ScrollView v =  findViewById(R.id.ProfileScroll);
        v.requestFocus();
        gridView.setFocusable(false);

//        bookings_path.orderByChild()


        user_id_unique = getIntent().getStringExtra("Email");

        event_path.orderByChild("event_ID").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String eventID = snapshot.child("event_ID").getValue(String.class);
                String eventTitle = snapshot.child("event_Name").getValue(String.class);
                String eventLoc = snapshot.child("event_Location").getValue(String.class);
                String eventDate = snapshot.child("event_Date").getValue(String.class);
                String eventDesc = snapshot.child("event_Description").getValue(String.class);
                String eventDetail = snapshot.child("event_Detail").getValue(String.class);
                String eventUserID = snapshot.child("event_UserID").getValue(String.class);
                Boolean eventBooked = snapshot.child("bookmarked").getValue(Boolean.class);
                String eventStorageID = snapshot.child("event_StorageReferenceID").getValue(String.class);
                String eventStartTime = snapshot.child("event_StartTime").getValue(String.class);
                String eventEndTime = snapshot.child("event_EndTime").getValue(String.class);
                Double eventTicketPrice = snapshot.child("event_TicketPrice").getValue(Double.class);
                List<String> eventTypes = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.child("eventTypes").getChildren()) {
                    eventTypes.add(dataSnapshot.getValue(String.class));
                };

                // Meant to prevent duplication of data display in gridAdapter
                if (eventsIDList.contains(eventID)) return;

                Events event = new Events(eventID,eventTitle, eventLoc, eventDate, eventDesc,
                        eventDetail, eventStartTime, eventEndTime, eventUserID, eventStorageID, eventBooked, eventTicketPrice,eventTypes);


                // Checking if past event or upcoming event
                Date date = null;
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date = sdf.parse(eventDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(date.after(today)){
                    eventsIDList.add(eventID);
                    DBevents.add(event);
                    Log.d("EventCount","" + DBevents.size());
                    Log.d("EventCount2", "" + event.getEvent_StorageReferenceID());
                }
                else{
                    pastEventsIDList.add(eventID);
                    pastDBevents.add(event);
                    Log.d("PastEventCount","" + pastDBevents.size());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("email",user.getEmail());
        String a = user.getEmail();
        Log.d("email a", "a is "+ a);

        // Get username to send to NavDrawer
//        user_path.child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(!task.isSuccessful()){
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else{
//                    username = String.valueOf(task.getResult().child("username").getValue());
//                    Log.d("DBusername","username is " + username);
//                }
//            }
//        });
//        Log.d("Username is",username);
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Intent i = new Intent(profile_page.this,NavDrawer.class);
//                i.putExtra("Email",user_id_unique);
//                i.putExtra("Username",username);
//                startActivity(i);
//            }
//        };
//
//        super.getOnBackPressedDispatcher().addCallback(this,callback);




        // Getting all the views as variables
        TextView EditDesc = findViewById(R.id.EditDesc);
        TextView UserDesc = findViewById(R.id.UserDescription);
        TextView UserName = findViewById(R.id.username);

        Intent setting = getIntent();
//        String getemailofuser = setting.getStringExtra("email");
//        String getusernameofuser = setting.getStringExtra("username");
//        String geruserprofileid = setting.getStringExtra("profile_id");

        user_path.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                // Getting user data
                userid = String.valueOf(task.getResult().child("id").getValue());
                username = String.valueOf(task.getResult().child("username").getValue());
                usertitle = String.valueOf(task.getResult().child("title").getValue());
                useremail = String.valueOf(task.getResult().child("email").getValue());

                // Adding all event ids into an array
                eventsBookedList = new ArrayList<String>();

                if (task.getResult().child("event_booked").getChildrenCount() > 0){
                    //there are events booked by the user, hooray!
                    for(DataSnapshot snapshot : task.getResult().child("event_booked").getChildren()){
                        String eventID = String.valueOf(snapshot.getValue());
                        eventsBookedList.add(eventID);
                    }
                }




                // Setting past and upcoming events
                // Idk if to change this to Fragment or not
                // May be laggy when changing or my emulator is garbage
                GridView gridView = (GridView) findViewById(R.id.gallery);
                gridView.setAdapter(new ProfileAdapter(profile_page.this,DBevents, eventsBookedList.size()));
                Log.d("set","AdapterSet");


                // Upcoming events
                Button upcoming = findViewById(R.id.UpcomingEvents);
                upcoming.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridView.setAdapter(new ProfileAdapter(profile_page.this,DBevents, eventsBookedList.size()));
                        Log.d("Clicked","click");
                        Log.d("Clicked", "" + DBevents.size());
                    }
                });

                // Past events
                Button past = findViewById(R.id.PastEvents);
                past.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridView.setAdapter(new ProfileAdapter(profile_page.this,pastDBevents,pastDBevents.size()));
                    }
                });

                // Make tapping on each image show their respective EventDetailsPage
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent showDetails = new Intent
                                (profile_page.this,EventDetailsPage.class);
                        Events e = new Events();
                        for (Events ev : DBevents){
                            if (String.valueOf(ev.getEvent_ID()).equals(eventsBookedList.get(position))) {
                                e = ev;
                                break;
                            }

                        }
                        List<Events> eList = new ArrayList<>();
                        eList.add(e);
                        showDetails.putExtra("event_List",(Serializable) eList);
                        Log.d("eListCreated","Event list is created " + String.valueOf(e.getEvent_ID()));
                        showDetails.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(showDetails);

                        // Need to figure out what to do with the position.
                        // Like how to link it with showing the actual event
                    }
                });


                try {
                    usercontact = Integer.valueOf(String.valueOf(task.getResult().child("contactnum").getValue()));
                }
                catch (Exception e){
                    usercontact = null;
                }
                userpass = String.valueOf(task.getResult().child("hashedpassword").getValue());
                //saltvalue = Integer.parseInt(String.valueOf(task.getResult().child("saltvalue").getValue()));



                p.setUsername(username);
                p.setTitle(usertitle);
                p.setEmail(useremail);
                try {
                    p.setContactnum(usercontact);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                p.setHashedpassword(userpass);

                // Set default texts
                UserDesc.setText(p.Title);
                UserName.setText(p.Username);

                ImageView profile_pic = findViewById(R.id.profilepic);



                // Changing Password
//                String value = setting.getStringExtra("new_pass");
//                p.setHashedpassword(Profile.HashPassword(saltvalue,value));
                // ^ Moved to change_password class

                // Change Pic
                user_path.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data. Please reload.", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().child("email").getValue()));
                            // Get image reference from image folder
                            String imagelink = String.valueOf(task.getResult().child("profpic").getValue());
                            // Set reference point for firebase storage
                            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("images/" + imagelink);
                            ImageView profile_pic = findViewById(R.id.profilepic);
                            if(profile_pic == null){
                                Log.d("ImageView", "ImageView missing");
                            }

                            try{
                                File localfile = File.createTempFile("image",".jpg");
                                firebaseStorage.getFile(localfile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmapImage = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                                profile_pic.setImageBitmap(Bitmap.createScaledBitmap
                                                        (bitmapImage,128,128,false));

                                            }
                                        }
                                );
                            }
                            catch (Exception exception){
                                exception.printStackTrace();
                            }
                        }
                    }
                });

            }
        });


        // OnClickListener to start the edit description activity
        EditDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(profile_page.this,EditDescription.class);
                startActivity(edit);
            }
        });
        toNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("i2","aa");
                Intent i2 = new Intent(profile_page.this,NavDrawer.class);
                i2.putExtra("Email",user_id_unique);
                i2.putExtra("Username",username);
                Log.d("Intent i2","" + user_id_unique +" "+ username);
                i2.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i2);
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


        // Social Media Stuff
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(profile_page.this);

        ImageButton insta = findViewById(R.id.InstaButton);
        ImageButton facebook = findViewById(R.id.FacebookButton);
        ImageButton linkedin = findViewById(R.id.linkedInButton);
        final ImageButton[] website = {findViewById(R.id.WebsiteButton)};

        Boolean enableMedia = sharedPreferences.getBoolean("EnableMedia",false);

        if (enableMedia) {
            insta.setVisibility(View.VISIBLE);
            facebook.setVisibility(View.VISIBLE);
            linkedin.setVisibility(View.VISIBLE);
            website[0].setVisibility(View.VISIBLE);
        } else {
            insta.setVisibility(View.INVISIBLE);
            facebook.setVisibility(View.INVISIBLE);
            linkedin.setVisibility(View.INVISIBLE);
            website[0].setVisibility(View.INVISIBLE);

        }


        // App gets links from preferences first before running this
        user_path.child(userID).orderByChild("Media").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(instaURL == null || instaURL.length() == 0 ){
                    instaURL = snapshot.child("Instagram").getValue(String.class);
                }
                if(facebookURL == null || facebookURL.length() == 0){
                    facebookURL = snapshot.child("Facebook").getValue(String.class);
                }
                if(linkedinURL == null || linkedinURL.length() == 0){
                    linkedinURL = snapshot.child("LinkedIn").getValue(String.class);
                }
                if(websiteURL == null || websiteURL.length() == 0){
                    websiteURL = snapshot.child("Website").getValue(String.class);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        instaURL = sharedPreferences.getString("Instagram",null);
        facebookURL = sharedPreferences.getString("Facebook",null);
        linkedinURL = sharedPreferences.getString("LinkedIn",null);
        websiteURL = sharedPreferences.getString("Website",null);

        user_path.child(userID).child("Media").child("Instagram").setValue(instaURL);
        user_path.child(userID).child("Media").child("Facebook").setValue(facebookURL);
        user_path.child(userID).child("Media").child("LinkedIn").setValue(linkedinURL);
        user_path.child(userID).child("Media").child("Website").setValue(websiteURL);

        // Onclick listeners for all the icons
        insta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("instaClick","Instagram was clicked");
                        try {
                            if (!instaURL.startsWith("http://") && !instaURL.startsWith("https://")){
                                instaURL = "http://" + instaURL;
                            }
                            Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(instaURL));
                            open.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(open);
                        }
                        catch(NullPointerException e){
                            Toast.makeText(getBaseContext(),"Media not connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!facebookURL.startsWith("http://") && !facebookURL.startsWith("https://")){
                                facebookURL = "http://" + facebookURL;
                            }
                            Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookURL));
                            open.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(open);
                        }
                        catch(NullPointerException e){
                            Toast.makeText(getBaseContext(),"Media not connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        linkedin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!linkedinURL.startsWith("http://") && !linkedinURL.startsWith("https://")){
                                linkedinURL = "http://" + linkedinURL;
                            }
                            Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinURL));
                            open.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(open);
                        }
                        catch(NullPointerException e){
                            Toast.makeText(getBaseContext(),"Media not connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        website[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")){
                                websiteURL = "http://" + websiteURL;
                            }
                            Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
                            open.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(open);
                        }
                        catch(NullPointerException e){
                            Toast.makeText(getBaseContext(),"Media not connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
