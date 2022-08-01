package sg.edu.np.mad.devent.ui.home;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.devent.Events;
import sg.edu.np.mad.devent.R;
import sg.edu.np.mad.devent.Statistics;
import sg.edu.np.mad.devent.databinding.FragmentHomeBinding;
import sg.edu.np.mad.devent.profile_page;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    static List<Events> eventsList = new ArrayList<>();
    static List<String> eventsIDList = new ArrayList<>();
    HomeGridAdapter gridAdapter;
    Button sportsFilter, gamingFilter, animeFilter, musicFilter, educationFilter, animalsFilter;
    GridView gridView;
    boolean flag = false;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gridView = binding.gridView;
        sportsFilter = binding.SportsFilter;
        gamingFilter = binding.GamingFilter;
        animeFilter = binding.AnimeFilter;
        musicFilter = binding.MusicFilter;
        educationFilter = binding.EducationFilter;
        animalsFilter = binding.AnimalsFilter;

        List<String> eventsIDList = new ArrayList<>();
        List<Events> createdEvents = new ArrayList<>();
        List<String> createdEventsID = new ArrayList<>();


        sportsFilter.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    List<Events> filterEvents = new ArrayList<>();
                    for (Events event:eventsList) {
                        if (event.getEventTypes().contains("Sports")) {
                            filterEvents.add(event);
                        }
                    }
                    gridAdapter = new HomeGridAdapter(container.getContext(), filterEvents);
                }
                else { gridAdapter = new HomeGridAdapter(container.getContext(),eventsList); }
                gridView.setAdapter(gridAdapter);
            }
        });

        gamingFilter.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    List<Events> filterEvents = new ArrayList<>();
                    for (Events event:eventsList) {
                        if (event.getEventTypes().contains("Gaming")) {
                            filterEvents.add(event);
                        }
                    }
                    gridAdapter = new HomeGridAdapter(container.getContext(), filterEvents);
                }
                else { gridAdapter = new HomeGridAdapter(container.getContext(),eventsList); }
                gridView.setAdapter(gridAdapter);
            }
        });

        animeFilter.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    List<Events> filterEvents = new ArrayList<>();
                    for (Events event:eventsList) {
                        if (event.getEventTypes().contains("Anime")) {
                            filterEvents.add(event);
                        }
                    }
                    gridAdapter = new HomeGridAdapter(container.getContext(), filterEvents);
                }
                else { gridAdapter = new HomeGridAdapter(container.getContext(),eventsList); }
                gridView.setAdapter(gridAdapter);
            }
        });

        musicFilter.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    List<Events> filterEvents = new ArrayList<>();
                    for (Events event:eventsList) {
                        if (event.getEventTypes().contains("Music")) {
                            filterEvents.add(event);
                        }
                    }
                    gridAdapter = new HomeGridAdapter(container.getContext(), filterEvents);
                }
                else { gridAdapter = new HomeGridAdapter(container.getContext(),eventsList); }
                gridView.setAdapter(gridAdapter);
            }
        });

        educationFilter.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    List<Events> filterEvents = new ArrayList<>();
                    for (Events event:eventsList) {
                        if (event.getEventTypes().contains("Education")) {
                            filterEvents.add(event);
                        }
                    }
                    gridAdapter = new HomeGridAdapter(container.getContext(), filterEvents);
                }
                else { gridAdapter = new HomeGridAdapter(container.getContext(),eventsList); }
                gridView.setAdapter(gridAdapter);
            }
        });

        animalsFilter.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                flag = !flag;
                if (flag) {
                    List<Events> filterEvents = new ArrayList<>();
                    for (Events event:eventsList) {
                        if (event.getEventTypes().contains("Animals")) {
                            filterEvents.add(event);
                        }
                    }
                    gridAdapter = new HomeGridAdapter(container.getContext(), filterEvents);
                }
                else { gridAdapter = new HomeGridAdapter(container.getContext(),eventsList); }
                gridView.setAdapter(gridAdapter);
            }
        });


        //For firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference Ref = database.getReference("Event");

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
                String eventStartTime = snapshot.child("event_StartTime").getValue(String.class);
                String eventEndTime = snapshot.child("event_EndTime").getValue(String.class);
                Double eventTicketPrice = snapshot.child("event_TicketPrice").getValue(Double.class);
                List<String> eventTypes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.child("eventTypes").getChildren()) {
                    eventTypes.add(dataSnapshot.getValue(String.class));
                };



                // Meant to prevent duplication of data display in gridAdapter
                if (eventsIDList.contains(eventID)) return;

                int totalpax = 0;
                Events event = new Events(eventID,eventTitle, eventLoc, eventDate, eventDesc,eventDetail, eventStartTime, eventEndTime,
                         eventUserID, eventStorageID, eventBooked,eventTicketPrice, eventTypes, totalpax);

                eventsIDList.add(eventID);
                eventsList.add(event);

                if (String.valueOf(eventUserID).equals(String.valueOf(userID))) {
                    createdEvents.add(event);
                    createdEventsID.add(event.getEvent_ID());

                }


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