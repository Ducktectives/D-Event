package sg.edu.np.mad.devent;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    Event_List_Adapter adapter;
    DatabaseReference databaseReference;
    LinearLayout linearLayout;
    Query query;



    // Firebase for storing Image
    private StorageReference reference;
    FirebaseStorage firebaseStorage;
    private FirebaseDatabase database;


    private FirebaseUser user;
    private String userID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        databaseReference = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Event");

        query = databaseReference.orderByChild("event_UserID").equalTo(userID);



        recyclerView = (RecyclerView) findViewById(R.id.eventList_recyclerView);

//        recyclerView.setLayoutManager(
////                new LinearLayoutManager(this),
//                new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false);
//        );
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        layoutManager();
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(this));

        FirebaseRecyclerOptions<Events> options = new FirebaseRecyclerOptions.Builder<Events>()
                .setQuery(query, Events.class)
                .build();

        // Connecting object of required Adapter class to
        // the Adapter class itself
        adapter = new Event_List_Adapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);

        // NEW 18/07
//        registerForContextMenu(recyclerView);
//        adapter.setOnItemClickListener(this);
        // NEW 18/07

        // [START storage_field_initialization]
        firebaseStorage = FirebaseStorage.getInstance();
        // [END storage_field_initialization]

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {



        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int argument) {
                    int position = viewHolder.getBindingAdapterPosition();

                    reference = firebaseStorage.getReferenceFromUrl(adapter.getItem(position).Event_StorageReferenceID);


                    DataSnapshot snapshot = (DataSnapshot ) adapter.getSnapshots().getSnapshot(position);
                    for(DataSnapshot ohSnap : snapshot.getChildren()){
                        ohSnap.getRef().removeValue();


                    }
                    reference.delete();
                    EventListActivity.super.recreate();
                    dialog.cancel();
                };
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int argument) {
                    EventListActivity.super.recreate();
                    dialog.cancel();
                };
            });
            builder.setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }
    };
    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
}

