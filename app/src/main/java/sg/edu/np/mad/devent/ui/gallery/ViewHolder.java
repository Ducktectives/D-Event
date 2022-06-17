package sg.edu.np.mad.devent.ui.gallery;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.devent.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    TextView description;
    ImageView profilePic;

    public ViewHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        description = itemView.findViewById(R.id.description);
        profilePic = itemView.findViewById(R.id.profilepic);
    }
}
