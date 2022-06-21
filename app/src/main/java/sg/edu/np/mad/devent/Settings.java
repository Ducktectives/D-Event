package sg.edu.np.mad.devent;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

public class Settings extends AppCompatActivity {

        Uri image;

        Context mContext;
        static String title;
        static String username;
        static String user_id_unique;
        static String new_user_id_unique;



    // Firebase for storing Image
    private StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Intent setting = getIntent();

        user_id_unique = setting.getStringExtra("Email");
        Intent toprof = new Intent(Settings.this,profile_page.class);
        toprof.putExtra("Email",user_id_unique);

        new_user_id_unique = user_id_unique.toLowerCase().replace(".","");

        // Get username to send in activity
        user_path.child(new_user_id_unique).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
        actionBar.setDisplayHomeAsUpEnabled(false);




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
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Settings s = new Settings();
            final Integer[] saltvalue = new Integer[1];


//            // Changing password
//            EditTextPreference password = findPreference("password");
//            if (password != null)
//            {
//                password.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
//                    @Override
//                    public void onBindEditText(@NonNull EditText editText) {
//                        // Get Salt value
//                        s.user_path.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                Profile p = snapshot.getValue(Profile.class);
//                                saltvalue[0] = p.Saltvalue;
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                        // Make editText box empty upon opening
//                        editText.getText().clear();
//                        // Mask entering of password
//                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                        editText.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//                                String new_password = editText.getText().toString();
//                                if(new_password != null){
//                                    s.user_path.child(s.user_id_unique).child("hashedpassword")
//                                            .setValue(Profile.HashPassword(saltvalue[0],new_password));
//                                }
//
//                            }
//                        });
//                    }
//                });
//            }

            // Changing password
            Preference newpass = findPreference("password");
            newpass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(),change_password.class);
                    i.putExtra("Email",user_id_unique);
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
                                s.user_path.child(new_user_id_unique).child("title").setValue(new_title[0]);
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
                                    s.user_path.child(new_user_id_unique).child("username").setValue(new_name[0]);
                                }
                            }
                        });
                    }
                });
            }




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

