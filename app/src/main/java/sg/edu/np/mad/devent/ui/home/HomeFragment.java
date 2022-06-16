package sg.edu.np.mad.devent.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import sg.edu.np.mad.devent.R;
import sg.edu.np.mad.devent.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final GridView gridView = binding.gridView;
        int[] imageList = {R.drawable.a1,R.drawable.a2,R.drawable.a3, R.drawable.a4, R.drawable.me,
                R.drawable.kirby_drawing};
        HomeGridAdapter gridAdapter = new HomeGridAdapter(container.getContext(),imageList);
        binding.gridView.setAdapter(gridAdapter);

        // final TextView textView = binding.textHome;
        // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}