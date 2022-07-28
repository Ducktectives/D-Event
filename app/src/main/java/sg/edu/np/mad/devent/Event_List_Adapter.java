package sg.edu.np.mad.devent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class Event_List_Adapter extends FirebaseRecyclerAdapter<Events, Event_List_Adapter.eventViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
//    private OnItemClickListener itemClickListener;



    public Event_List_Adapter(@NonNull FirebaseRecyclerOptions<Events> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull eventViewHolder holder, int position, @NonNull Events model) {
        holder.firstname.setText(model.Event_Name);
        holder.lastname.setText(model.Event_Detail);
        holder.age.setText(model.Event_Date);


        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            Bundle bundle = new Bundle();
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.event_menu_item);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.update_event_list:
//                                bundle.putString("Update",model.Event_ID);
                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                                EventListUpdateActivity fragment = new EventListUpdateActivity();
//                                activity.getSupportFragmentManager().beginTransaction().replace(, fragment).addToBackStack(null).commit();
                                Intent intent = new Intent();
                                intent = new Intent(activity, EventListUpdateActivity.class);

                                intent.putExtra("Event_ID", model.getEvent_ID());
                                intent.putExtra("Event_Storage", model.getEvent_StorageReferenceID());
                                intent.putExtra("Event_Storage", model.getEvent_StorageReferenceID());

                                activity.startActivity(intent);

                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });


    }

    @NonNull
    @Override
    public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_list_item_profile, parent, false);
        return new eventViewHolder(view);
    }







    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class eventViewHolder extends RecyclerView.ViewHolder
    {
        TextView firstname, lastname, age, time;
        CardView cardView;

        // new 18/07
        TextView buttonViewOption;
        public eventViewHolder(@NonNull View itemView)
        {
            super(itemView);

            firstname = itemView.findViewById(R.id.firstname);
            lastname = itemView.findViewById(R.id.lastname);
            age = itemView.findViewById(R.id.age);
            time = itemView.findViewById(R.id.eventlistprofilereventtime);

            // new 18/07
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        }

    }



}
