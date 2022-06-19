package sg.edu.np.mad.devent;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Settings extends AppCompatActivity {

        Uri image;
        ImageView profile_pic;
        Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


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
        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");


    }


    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Firebase for storing Image
    private StorageReference storageReference;
    private FirebaseDatabase database;


    public static void verifyStoragePermissions(Activity activity) {
        Settings s = new Settings();
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private final ActivityResultLauncher<Intent> launchGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        image = data.getData();
                        if (image != null){
                            try{
                                profile_pic = findViewById(R.id.profilepic);
                                Glide.with(Settings.this).load(image).into(profile_pic);
                            }
                            catch (Exception ex){
                                Log.d("Failed to upload image", String.valueOf(ex));
                            }
                        }


                    }
                }
            }
    );

    // Method for starting the activity for selecting image from phone storage
    public void pick(View view) {
        verifyStoragePermissions(Settings.this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launchGallery.launch(intent);
    }


    // uploadimage method
    protected void uploadImage(){
        if(image != null){

            // progressDialogue while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String storageReference_ID = UUID.randomUUID().toString();

            // Defining the child of storageReference
            //
            // SorageReference represents a reference to Google Cloud Storage Object
            StorageReference reference = storageReference.child(
                    // UUID is a class that represents immutable universally unique identifier (UUID)
                    //
                    // A UUID represents a 128-bit value
                    "images/" + storageReference_ID) ;

            // adding listeners on event progression of image upload
            reference.putFile(image).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            progressDialog.dismiss();
                            Toast.makeText(Settings.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e){
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(Settings.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress listneer for loading on the dialog box
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded " + (int) progress + "%"
                    );
                }
            });


        }
    }




    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            EditTextPreference password = findPreference("password");
            if (password != null)
            {
                password.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        // Make editText box empty upon opening
                        editText.getText().clear();
                        // Mask entering of password
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                // Send password value over to profile page
                                String new_password = editText.getText().toString();
                                Intent i = new Intent(getActivity(),profile_page.class);
                                i.putExtra("new_pass",new_password);

                            }
                        });
                    }
                });
            }

            // Change profile picture
            Preference changepic = findPreference("profile_pic");
            changepic.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("help","i want die");
//                    Settings s = new Settings();
//                    s.pick(getView());
//                    s.uploadImage();
                    Intent i = new Intent(getActivity(),Change_ProfilePic.class);
                    startActivity(i);
                    return false;
                }
            });

        }


        // This just goes back to main Activity cause idk how to make it
        // go to the previous activity
        // To change which activity to go to change the parentActivityName
        // in the manifest file
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    super.getActivity().onBackPressed();
                    super.getActivity().finish();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }



    }


}