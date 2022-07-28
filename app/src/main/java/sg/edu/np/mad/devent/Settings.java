package sg.edu.np.mad.devent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class Settings extends AppCompatActivity {

        Uri image;

        Context mContext;
        static String title;
        static String username;
        static String user_id_unique;
        static String new_user_id_unique;
        static Calendar lastReminded = Calendar.getInstance();



    // Firebase for storing Image
    private StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference().child("Users");

    FirebaseUser user;
    private String userID;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Getting an instance out of Firebase AUth
        auth = FirebaseAuth.getInstance();

        // have a ref to the realtime database
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


        Intent setting = getIntent();

        user_id_unique = setting.getStringExtra("Email");
        Intent toprof = new Intent(Settings.this,profile_page.class);
        toprof.putExtra("Email",user_id_unique);

        new_user_id_unique = user_id_unique.toLowerCase().replace(".","");

        // Get username to send in activity
        user_path.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else{
                    username = String.valueOf(task.getResult().child("username").getValue());
                }
            }
        });



        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set this to true if you want back button on the actionbar
        // Intents dont send over when you press back on the action bar for some raeason
        // so im stuck
        actionBar.setDisplayHomeAsUpEnabled(true);




        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        String getuser = getIntent().getStringExtra("EventOrganiser");

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent i = new Intent(Settings.this,NavDrawer.class);
                i.putExtra("Email",user_id_unique);
                i.putExtra("Username",username);
                Intent i2 = new Intent(Settings.this,profile_page.class);
                i2.putExtra("Email",user_id_unique);
                Log.d("backbutton","hey the back button is being pressed");
                i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this,callback);


    }



    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static class SettingsFragment extends PreferenceFragmentCompat {
        private FirebaseUser user;
        private String userID;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Settings s = new Settings();
            final Integer[] saltvalue = new Integer[1];

            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();


            // Changing password
            Preference newpass = findPreference("password");
            newpass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(),change_password.class);
                    i.putExtra("Email",user_id_unique);
                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return false;
                }
            });


            // Change profile picture
            Preference changepic = findPreference("profile_pic");
            changepic.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("help","i want die");
                    Intent i = new Intent(getActivity(),Change_ProfilePic.class);
                    i.putExtra("Email",user_id_unique);
                    Log.d("help",user_id_unique);
                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return false;
                }
            });

            // Change Title
            EditTextPreference changetitle = findPreference("title");
            s.user_path.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Profile shitake = snapshot.getValue(Profile.class);
                    title = shitake.Title;
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
            if(changetitle != null){
                final String[] new_title = new String[1];
                changetitle.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                       editText.setText(title);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                new_title[0] = editText.getText().toString();
                            if (new_title[0] != null){
                                s.user_path.child(userID).child("title").setValue(new_title[0]);
                            }
                            }
                        });
                    }
                });
            }

            // Changing username

            final Bitmap[] setprofilepic = new Bitmap[1];
            EditTextPreference changename = findPreference("username");
            s.user_path.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Profile shitake = snapshot.getValue(Profile.class);
                    username = shitake.Username;
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

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userID = user.getUid();



            if(changename != null){
                final String[] new_name = new String[1];
                changename.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setText(username);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                new_name[0] = editText.getText().toString();
                                if (new_name[0] != null){
                                    s.user_path.child(userID).child("username").setValue(new_name[0]);
                                }
                            }
                        });
                    }
                });
            }
        // End Change username

        //  Send email notification
//            Calendar today = Calendar.getInstance();
//            today.set(Calendar.HOUR_OF_DAY, 0);
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // Getting last reminded info
//            if(!findPreference("notifs").isEnabled()){
//                s.user_path.child("LastReminded").setValue(sdf.format(today));
//            }
//            else{
//                Log.d("CheckEnabled", "switch is on");
//                s.user_path.child(new_user_id_unique).get()
//                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                if(!task.isSuccessful()){
//                                    Log.e("firebase", "Error getting lastReminded", task.getException());
//                                }
//                                else{
//                                    Log.d("firebaseSuccessful", String.valueOf(task.getResult()
//                                            .child("LastReminded").getValue()));
//                                    Log.d("firebaseSuccessful", "lastReminded gotten from firebase");
//                                    String remindedString = String.valueOf(task.getResult().child("LastReminded").getValue());
//                                    try {
//                                        lastReminded.setTime(sdf.parse(remindedString));
//                                        Log.d("lastReminded","remindedString");
//                                        Log.d("TodayIs","Today is"+today.toString());
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//                                    if(lastReminded.before(today)){
//                                        Log.d("EmailSent", "Processing email...");
//                                        BackgroundMail bm = new BackgroundMail(getContext());
//                                        bm.setGmailUserName("groupprojectmail2024@gmail.com");
//                                        bm.setGmailPassword("pa1n1stemporary");
//                                        bm.setMailTo(user_id_unique);
//                                        bm.setFormSubject("You have upcoming events!");
//                                        bm.setFormBody("You need therapy!");
//                                        bm.send();
//                                    }
//                                }
//                            }
//                        });
//            }

            // ^ Doesn't work
        }



        // This just goes back to NavDrawer cause idk how to make it
        // go to the previous activity
        // To change which activity to go to change the parentActivityName
        // in the manifest file
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case android.R.id.home:
                    Intent i = new Intent(getActivity(),NavDrawer.class);
                    i.putExtra("Email",user_id_unique);
                    i.putExtra("Username",username);
                    Intent i2 = new Intent(getActivity(), profile_page.class);
                    i2.putExtra("Email",user_id_unique);
                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }


        public boolean onCreateOptionsMenu(Menu menu) {
            return true;
        }
        }
    }

