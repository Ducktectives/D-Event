package sg.edu.np.mad.devent.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sg.edu.np.mad.devent.Profile;
import sg.edu.np.mad.devent.R;
import sg.edu.np.mad.devent.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // List of followed users (replace with data from firebase)
        /*
        List<Profile> followedList = new ArrayList<>();
        for (int index = 1; index < 21; index++){
            Random randObj = new Random();      //reduce the number of times new Random has to be typed
            Profile randUser = new Profile(index + "","User" + index, "Title?",
                    "Email", 99999999, "pass");
            followedList.add(randUser);
        }



        // final TextView textView = binding.textGallery;
        // final ImageView imageView = binding.imageView3;
        // galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        // textView.setText("test");
        RecyclerView recyclerView = binding.rvFollowed;
        //rvAdapter mAdapter = new rvAdapter(followedList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

         */


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}