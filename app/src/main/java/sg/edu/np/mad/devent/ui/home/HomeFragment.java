package sg.edu.np.mad.devent.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.devent.Events;
import sg.edu.np.mad.devent.R;
import sg.edu.np.mad.devent.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    static List<Events> eventsList = new ArrayList<>();
    static List<String> eventsIDList = new ArrayList<>();
    HomeGridAdapter gridAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final GridView gridView = binding.gridView;

        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Event");

        // List of events (Replaced with actual data retrieved from firebase)
        int[] imageList = {R.drawable.a1,R.drawable.a2,R.drawable.a3, R.drawable.a4, R.drawable.me,
                R.drawable.kirby_drawing};

        Ref.orderByChild("event_ID").addChildEventListener(new ChildEventListener() {
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

                // Meant to prevent duplication of data display in gridAdapter
                if (eventsIDList.contains(eventID)) return;

                Events event = new Events(eventID,eventTitle, eventLoc, eventDate, eventDesc,
                        eventDetail, eventUserID, eventStorageID, eventBooked);

                eventsIDList.add(eventID);
                eventsList.add(event);


                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR!", "Some error occurred regarding HomeFragement firebase data retrieval\n" +
                        error);
            }
        });

        gridAdapter = new HomeGridAdapter(container.getContext(),eventsList);
        gridView.setAdapter(gridAdapter);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // Not sure why but putting this prevents double icons
        inflater.inflate(R.menu.nav_drawer, menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                gridAdapter.getFilter().filter(s);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_view){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}