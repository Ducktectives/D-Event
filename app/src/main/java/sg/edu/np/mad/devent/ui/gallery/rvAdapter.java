package sg.edu.np.mad.devent.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.edu.np.mad.devent.Profile;
import sg.edu.np.mad.devent.R;

public class rvAdapter extends RecyclerView.Adapter<ViewHolder>{
    private List<Profile> data;
    public rvAdapter (List<Profile> data) { this.data = data; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.followedviewholder,
                parent,
                false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile user = data.get(position);
        holder.username.setText(user.getUsername());
        // holder.profilePic.setImageResource();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
