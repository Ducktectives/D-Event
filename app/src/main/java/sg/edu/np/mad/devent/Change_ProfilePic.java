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
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class Change_ProfilePic extends AppCompatActivity {

    ImageView profpic;
    Button accept;
    Button cancel;
    Uri image;
    DatabaseReference reference;

    String user_id_unique;

    // Firebase for storing Image
    private StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;

    /*Arthur*/
    Uri selectedImage; // event_Picture
    ImageView imgView;
    private static final int PICK_IMAGE_REQUEST = 9544;

    /* Arthur Edit */
    private FirebaseUser user;
    private String userID, profilePic;
    private ProgressBar progressBar;
    /* Arthur edit */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_pic);
    /*    profpic = findViewById(R.id.currentprofpic);
        accept = findViewById(R.id.acceptchangeprofpic);
        cancel = findViewById((R.id.cancelchangeprofpic));*/


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    /*    DatabaseReference event_path = database.getReference("Event");
        DatabaseReference user_path = database.getReference("Users");*/

        /* storageReference = FirebaseStorage.getInstance().getReference();*/


        /*Arthur edit*/
        // [START storage_field_initialization]
        firebaseStorage = FirebaseStorage.getInstance();
        // [END storage_field_initialization]

        imgView = (ImageView) findViewById(R.id.img_ProfilePicture);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        Toast.makeText(this, "User ID : " + userID, Toast.LENGTH_LONG).show();


        DatabaseReference databaseReference ;
        databaseReference = firebaseDatabase.getInstance().getReference();


        /* 26/07 - Set the form when user comes in */
        // Get username to send in activity

        databaseReference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                profilePic = profile.ProfilePicReference;

                try{

                    Glide.with(Change_ProfilePic.this).load(profilePic).into(imgView);


                }catch (Exception ex){
                    Log.d("image", "error : " + ex);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

/*
        user_id_unique = getIntent().getStringExtra("Email");
        String a = user_id_unique;


        user_id_unique = user_id_unique.toLowerCase().replace(".","");*/


       /* profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick(view);
            }
        });*/

  /*      accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Change_ProfilePic.this,profile_page.class);
                // Send new image to profile page
                i.putExtra("new_pic",image.toString());
                i.putExtra("Email",a);
                Log.d("aa","Image uploaded");
                uploadImage();
                i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // Need to reflect change in profile page activity

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Change_ProfilePic.this,profile_page.class);
                i.putExtra("Email",a);
                i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });*/

    }

    // Method for starting the activity for selecting image from phone storage
    public void pick(View view) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            launcher.launch(intent);
        }
    }

    public void submit_the_form(View view){
        uploadImage();
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        selectedImage = data.getData(); // get the file uri
                        if (selectedImage != null) {
                            try {

                                // update the preview image in the layout
//                            image.setImageURI(selectedImage);

                                // Glide is an API that supports fetching of images.
                                // here we are setting image on image view using Glide
                                Glide.with(Change_ProfilePic.this).load(selectedImage).into(imgView);
                            } catch (Exception ex) {
                                Log.d("Image upload error", String.valueOf(ex));
                            }
                        }
                    }
                }
            }
    );

  /*  private final ActivityResultLauncher<Intent> launchGallery = registerForActivityResult(
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
    );*/

    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Firebase for storing Image
    /*protected StorageReference storageReference;*/

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
 /*   public void pick(View view) {
        verifyStoragePermissions(Change_ProfilePic.this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launchGallery.launch(intent);
    }
*/
    // getting file extension
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    // uploadimage method
    protected void uploadImage() {
        if (imgView.getDrawable() == null) {
            Toast.makeText(Change_ProfilePic.this, "Profile Pic is Empty", Toast.LENGTH_SHORT).show();

        }
            // progressDialogue while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            firebaseStorage = firebaseStorage.getInstance("gs://dvent---ducktectives.appspot.com/");

            storageReference = firebaseStorage.getReference();



            // Defining the child of storageReference
            //selectedImage => URI
            // SorageReference represents a reference to Google Cloud Storage Object
            StorageReference reference = storageReference.child("profileImages").child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));

            firebaseDatabase = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");

            DatabaseReference databaseReference;
            databaseReference = firebaseDatabase.getReference();

            /*   Toast.makeText(EventFormActivity.this, "Event Stop Time" + event_StopTime, Toast.LENGTH_SHORT).show();
             */

            imgView.setDrawingCacheEnabled(true);
            imgView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            reference.putBytes(data).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(Change_ProfilePic.this, "Image failed to uploaded" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    progressDialog.dismiss();
                    Toast.makeText(Change_ProfilePic.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;

                            HashMap hashMap = new HashMap();
                            hashMap.put("profilePicReference", downloadUrl.toString());

                            databaseReference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Profile profile = dataSnapshot.getValue(Profile.class);
                                    System.out.println(profile);
                                     Toast.makeText(Change_ProfilePic.this, "User Email" + profile.Email, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("The read failed: " + databaseError.getCode());
                                }
                            });

                            databaseReference.child("Users").child(userID).updateChildren(hashMap);


//
                            Toast.makeText(Change_ProfilePic.this, "Form Uploaded", Toast.LENGTH_SHORT).show();

                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.dismiss();

                        }
                    });
                }




        /*    String storageReference_ID = UUID.randomUUID().toString();

            // Defining the child of storageReference
            //
            // SorageReference represents a reference to Google Cloud Storage Object
            StorageReference reference = storageReference.child(
                    // UUID is a class that represents immutable universally unique identifier (UUID)
                    //
                    // A UUID represents a 128-bit value
                    "images/" + storageReference_ID) ;


            user_path.child(userID).child("profpic").setValue(storageReference_ID);
*/

                // adding listeners on event progression of image upload
           /* reference.putFile(image).addOnSuccessListener(
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
*/

            });

    }
}