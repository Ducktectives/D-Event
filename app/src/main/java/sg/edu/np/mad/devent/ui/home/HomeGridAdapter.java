package sg.edu.np.mad.devent.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.devent.EventDetailsPage;
import sg.edu.np.mad.devent.Events;
import sg.edu.np.mad.devent.NavDrawer;
import sg.edu.np.mad.devent.R;

public class HomeGridAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<Events> eventsList;
    private List<Events> filteredEventsList;
    String imgLink;

    LayoutInflater inflater;


    /*Arthur Edit*/


    // Firebase for storing Image
//    private StorageReference reference;
    FirebaseStorage firebaseStorage;
    /*Arthur Edit*/


    public HomeGridAdapter(Context context, List<Events> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
        this.filteredEventsList = eventsList;
    }

    @Override
    public int getCount() {
        return filteredEventsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear() {
        eventsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Events> list){
        eventsList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {


        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null){
            view = inflater.inflate(R.layout.home_grid_item,null);
        }

        ImageView gridImage = view.findViewById(R.id.gridImage);
        TextView gridTitle = view.findViewById(R.id.eventTitle);

        imgLink = filteredEventsList.get(position).getEvent_StorageReferenceID();


      /*  DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Event");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Events events = dataSnapshot.getValue(Events.class);
                    imageList
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        StorageReference firebaseStorage= FirebaseStorage.getInstance().getReference("images/" + imgLink);


//        firebaseStorage = FirebaseStorage.getInstance();
//        reference = FirebaseStorage firebaseStorage.getReferenceFromUrl(adapter.getItem(position).Event_StorageReferenceID);


//        try {
//            File localfile = File.createTempFile("image",".png");
//            firebaseStorage.getFile(localfile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            Bitmap bitmapImage = BitmapFactory.decodeFile(localfile.getAbsolutePath());
//                            gridImage.setImageBitmap(bitmapImage);
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // gridImage.setImageResource(R.drawable.no_event_thumbnail);
//                        }
//                    });
//            // Reference to an image file in Cloud Storage
////            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
////
////            Toast.makeText(context, "Image ID " + storageReference.getDownloadUrl(), Toast.LENGTH_LONG).show();
////
////
////            // Download directly from StorageReference using Glide
////            // (See MyAppGlideModule for Loader registration)
////                        Glide.with(context)
////                                .load(storageReference)
////                                .into(gridImage);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            Glide.with(context).load(imgLink).into(gridImage);
        }
        catch (Exception e) {

        }

        // imageView.setImageResource(Integer.parseInt(filteredEventsList.get(i)));
        gridTitle.setText(filteredEventsList.get(position).getEvent_Name());
        gridImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventAct = new Intent(context, EventDetailsPage.class);
                eventAct.putExtra("event_List", (Serializable) eventsList);
                eventAct.putExtra("event_Name",filteredEventsList.get(position).getEvent_ID());
                eventAct.putExtra("Email", NavDrawer.getemailofuser);
                context.startActivity(eventAct);
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if (charSequence == null || charSequence.length() == 0){
                    filterResults.count = eventsList.size();
                    filterResults.values = eventsList;
                }
                else{
                    String searchStr = charSequence.toString().toLowerCase();
                    List<Events> resultData = new ArrayList<>();
                    for (Events event:eventsList){
                        // add if it contains event description
                        if (event.getEvent_Name().toLowerCase().contains(searchStr)){
                            resultData.add(event);
                        }
                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredEventsList = (List<Events>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
