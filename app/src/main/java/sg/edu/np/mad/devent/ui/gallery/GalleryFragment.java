package sg.edu.np.mad.devent.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

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

        final GridView gridView = binding.gridView;
        int[] imageList = {R.drawable.a1,R.drawable.a2,R.drawable.a3, R.drawable.a4};

        GalleryGridAdapter gridAdapter = new GalleryGridAdapter(container.getContext(),imageList);
        binding.gridView.setAdapter(gridAdapter);
        // final TextView textView = binding.textGallery;
        // final ImageView imageView = binding.imageView3;
        // galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //textView.setText("test");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}