package sg.edu.np.mad.devent;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ProfileAdapter_Past extends BaseAdapter {
    private  Context mContext;
    public ProfileAdapter_Past(Context c) {
        mContext = c;
    }
    public int getCount(){
        return mThumbIds.length;
    }

    public Object getItem(int position){
        return null;
    }
    public long getItemId(int position){
        return 0;
    }
    // create a new ImageView for each item referenced by the Adapter

    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;

        if(convertView == null){
            imageView = new ImageView(mContext);
            // Need to fix the image params because idk wtf to do with it wth alfnDLFgnn
            imageView.setLayoutParams(new ViewGroup.LayoutParams(305,305));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }
        else{
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // Keep all Images in array
    // Change these images to something else set by the database
    public Integer[] mThumbIds = {
            R.drawable.playstore,R.drawable.a2,R.drawable.a3,
            R.drawable.beet_salad,R.drawable.me,R.drawable.kirby_drawing

    };
}
