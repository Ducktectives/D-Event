package sg.edu.np.mad.devent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Change_ProfilePic extends AppCompatActivity {

    ImageView profpic;
    Button accept;
    Button cancel;
    Uri image;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_pic);
        profpic = findViewById(R.id.currentprofpic);
        accept = findViewById(R.id.acceptchangeprofpic);
        cancel = findViewById((R.id.cancelchangeprofpic));
        database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storageReference = FirebaseStorage.getInstance().getReference();
        profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick(view);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Change_ProfilePic.this,profile_page.class);
                // Send new image to profile page
                i.putExtra("new_pic",image.toString());
                Log.d("aa","Image uploaded");
                uploadImage();
                // Need to reflect change in profile page activity

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




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
                                Glide.with(Change_ProfilePic.this).load(image).into(profpic);
                            }
                            catch (Exception ex){
                                Log.d("Failed to upload image", String.valueOf(ex));
                            }
                        }
                    }
                }
            }
    );

    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Firebase for storing Image
    protected StorageReference storageReference;
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

    // Method for starting the activity for selecting image from phone storage
    public void pick(View view) {
        verifyStoragePermissions(Change_ProfilePic.this);
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
                            Toast.makeText(Change_ProfilePic.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e){
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(Change_ProfilePic.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}