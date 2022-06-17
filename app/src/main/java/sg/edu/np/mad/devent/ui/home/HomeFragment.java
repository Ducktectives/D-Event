package sg.edu.np.mad.devent.ui.home;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.devent.Events;
import sg.edu.np.mad.devent.R;
import sg.edu.np.mad.devent.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    List<Events>  eventsList = new ArrayList<>();
    HomeGridAdapter gridAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final GridView gridView = binding.gridView;

        // List of events (Replaced with actual data retrieved from firebase)
        int[] imageList = {R.drawable.a1,R.drawable.a2,R.drawable.a3, R.drawable.a4, R.drawable.me,
                R.drawable.kirby_drawing};
        String[] titleList = {"Duck picnic", "Duck picnic 2", "Return of the Duckening", "Amazing health app",
                "My mental health", "Amazing Kirby Exhibit"};

        for (int i = 0; i < titleList.length; i++){
            Events event = new Events(titleList[i], "Location", "17 June", "randDescription", "1",
                    imageList[i] + "", true);
            eventsList.add(event);
        }

        gridAdapter = new HomeGridAdapter(container.getContext(),eventsList);
        binding.gridView.setAdapter(gridAdapter);

        // final TextView textView = binding.textHome;
        // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
        menu.clear();
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