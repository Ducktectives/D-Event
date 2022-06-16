/*
package sg.edu.np.mad.devent.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import sg.edu.np.mad.devent.EventDetailsPage;
import sg.edu.np.mad.devent.R;

public class GalleryGridAdapter extends BaseAdapter {
    Context context;
    int[] image;

    LayoutInflater inflater;

    public GalleryGridAdapter(Context context, int[] image) {
        this.context = context;
        this.image = image;
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null){
            view = inflater.inflate(R.layout.gallery_grid_item,null);
        }

        ImageView imageView = view.findViewById(R.id.gridImage);

        imageView.setImageResource(image[i]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventAct = new Intent(context, EventDetailsPage.class);
                context.startActivity(eventAct);
            }
        });

        return view;
    }
}


 */