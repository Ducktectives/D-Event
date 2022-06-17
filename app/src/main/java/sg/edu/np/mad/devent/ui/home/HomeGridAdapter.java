package sg.edu.np.mad.devent.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.edu.np.mad.devent.EventDetailsPage;
import sg.edu.np.mad.devent.Events;
import sg.edu.np.mad.devent.R;

public class HomeGridAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<Events> eventsList;
    private List<Events> filteredEventsList;

    LayoutInflater inflater;

    public HomeGridAdapter(Context context, List<Events> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
        this.filteredEventsList = eventsList;
    }

    @Override
    public int getCount() {
        return filteredEventsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null){
            view = inflater.inflate(R.layout.home_grid_item,null);
        }

        ImageView imageView = view.findViewById(R.id.gridImage);
        TextView textView = view.findViewById(R.id.eventTitle);

        imageView.setImageResource(filteredEventsList.get(i).getImage());
        textView.setText(filteredEventsList.get(i).getName());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventAct = new Intent(context, EventDetailsPage.class);
                context.startActivity(eventAct);
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if (charSequence == null || charSequence.length() == 0){
                    filterResults.count = eventsList.size();
                    filterResults.values = eventsList;
                }
                else{
                    String searchStr = charSequence.toString().toLowerCase();
                    List<Events> resultData = new ArrayList<>();
                    for (Events event:eventsList){
                        // add if it contains event description
                        if (event.getName().toLowerCase().contains(searchStr)){
                            resultData.add(event);
                        }
                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredEventsList = (List<Events>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
