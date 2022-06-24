package sg.edu.np.mad.devent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ProfileAdapter extends BaseAdapter {
    private  Context mContext;
    String[] web;
    int[] Imageid;
    List<Events> EventsList;
    static List<Drawable> DrawableList = new ArrayList<Drawable>();
    Integer size;


    public ProfileAdapter(Context c) {
        mContext = c;
    }
    public ProfileAdapter(Context c, String[] web, int[] Imageid){
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }
    public ProfileAdapter(Context c, int[] Imageid){
        mContext = c;
        this.Imageid = Imageid;
    }

    public ProfileAdapter(Context c, List<Events> eventsList, Integer size){
        mContext = c;
        EventsList = eventsList;
        this.size = size;
    }
    public int getCount(){
        return size;
    }

    public Object getItem(int position){
        return null;
    }
    public long getItemId(int position){
        return 0;
    }


    // create a new ImageView for each item referenced by the Adapter


    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        View view;
        StorageReference firebaseStorage;
        String refID;



        for(Events event: EventsList) {
            refID = event.getEvent_StorageReferenceID();
            // Set reference point for firebase storage
            try {
                firebaseStorage = FirebaseStorage.getInstance().getReference("images/" + refID.trim());
                Log.d("EventLink", "images/" + refID.trim());
                // Need to trim because for some reason a space is added when inputted the events
                // I took 2 hours to find that out.

                try {
                    File localfile = File.createTempFile("image", ".jpg");
                    Log.d("TempFile", "event: " + event);
                    firebaseStorage.getFile(localfile).addOnSuccessListener(
                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmapImage = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                    Drawable d = new BitmapDrawable(mContext.getResources(), bitmapImage);
                                    Log.d("DrawableList", "event added: " + event + d);
                                    DrawableList.add(d);

                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("OnFailure", "Failure: " + e);
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.d("why?", "got cancelled");
                        }
                    }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                            Log.d("Paused", "why is it paused");
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d("Exception", "help");
                }
            }
            catch(NullPointerException ex){
                Toast.makeText(mContext,"Some images failed to load",Toast.LENGTH_SHORT);
            }
        }





        if(convertView == null){
            imageView = new ImageView(mContext);

            // Need to fix the image params becaus
            // e idk wtf to do with it wth alfnDLFgnn
            imageView.setLayoutParams(new ViewGroup.LayoutParams(305,305));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }
        else{
            imageView = (ImageView) convertView;
        }


        if(DrawableList != null){
            try {
                imageView.setImageDrawable(DrawableList.get(position));
            }
            catch(IndexOutOfBoundsException ex){
                Log.d("woops","xd");
                Log.d("woops 2","" + DrawableList.size() + " "  + position);
                return imageView;
            }
        }
        return imageView;

    }

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference event_path = database.getReference("Event");
    DatabaseReference user_path = database.getReference("Users");


    // Keep all Images in array
    // Change these images to something else set by the database
    public Integer[] mThumbIds = {
            R.drawable.a1,R.drawable.a2,R.drawable.a3,
            R.drawable.a4,R.drawable.me,R.drawable.kirby_drawing

    };

    public Integer[] newThumbIds;
}
