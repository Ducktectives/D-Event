package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

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

    // Firebase stuff
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference("Users");

    String user_id_unique = "W222"; // Change this to get from intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Getting all the views as variables
        TextView EditDesc = findViewById(R.id.EditDesc);
        TextView UserDesc = findViewById(R.id.UserDescription);
        TextView UserName = findViewById(R.id.username);

        Intent setting = getIntent();

        // Change Pic
        user_path.child(user_id_unique).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data. Please reload.", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().child("email").getValue()));
                    // Get image reference from image folder
                    String imagelink = String.valueOf(task.getResult().child("profpic").getValue());
                    Log.d("AAAAAAAAAAAAA",imagelink);
                    // Set reference point for firebase storage
                    StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("images/" + imagelink);
                    ImageView profile_pic = findViewById(R.id.profilepic);
                    if(profile_pic == null){
                        Log.d("imageview missing","help me please");
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

        // !!! Change this to get profile from database
        user_path.child(user_id_unique).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                // Getting user data
                userid = String.valueOf(task.getResult().child("id").getValue());
                username = String.valueOf(task.getResult().child("username").getValue());
                usertitle = String.valueOf(task.getResult().child("title").getValue());
                useremail = String.valueOf(task.getResult().child("email").getValue());
                usercontact = Integer.valueOf(String.valueOf(task.getResult().child("contactnum").getValue()));
                userpass = String.valueOf(task.getResult().child("hashedpassword").getValue());
                saltvalue = Integer.parseInt(String.valueOf(task.getResult().child("saltvalue").getValue()));


                p.setId(userid);
                p.setUsername(username);
                p.setTitle(usertitle);
                p.setEmail(useremail);
                p.setContactnum(usercontact);
                p.setHashedpassword(userpass);

                // Set default texts
                UserDesc.setText(p.Title);
                UserName.setText(p.Username);




                // Changing Password
                String value = setting.getStringExtra("new_pass");
                p.setHashedpassword(Profile.HashPassword(saltvalue,value));


                // Change title
                String new_title = setting.getStringExtra("new_title");
                Log.d("a","sent new_title is "+ new_title);
                if(new_title != null){
                    user_path.child(user_id_unique).child("title").setValue(new_title);
                }
                }
        });




        // !!! Make if else statement to only show edit desc if profile owner is viewing own profile
        // !!! Also should not show follow button on own profile
        // !!! Someone teach me how to use database
//        Button followButton = findViewById(R.id.FollowButton);
//        if(p.Id == "1") // Change ID to reflect actual user ID
//        {
//            followButton.setVisibility(View.VISIBLE);
//        }
//        else{
//            followButton.setVisibility(View.INVISIBLE);
//        }

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


        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile_page.this, NavDrawer.class));
            }
        });





    }



}
