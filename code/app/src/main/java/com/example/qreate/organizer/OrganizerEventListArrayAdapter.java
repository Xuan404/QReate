package com.example.qreate.organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qreate.R;

import java.util.ArrayList;
/**
 * The following class is responsible for adapting events into recycler views
 *
 * @author Denis Soh
 */
public class OrganizerEventListArrayAdapter extends RecyclerView.Adapter<AViewHolder> {
    private ArrayList<OrganizerEvent> events;
    private Context context;

    public OrganizerEventListArrayAdapter(Context context, ArrayList<OrganizerEvent> events){
        this.context = context;
        this.events = events;
    }

    /**
     * generates view-holder
     *
     * @param parent the view-group of the view
     * @param viewType view type index
     *
     * @return AViewHolder
     */
    @NonNull
    @Override
    public AViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_event_list_recycler_row_layout,parent,false);
        return new AViewHolder(view);
    }

    /**
     * generates view-holder
     *
     * @param holder view-holder
     * @param position current position in recycler
     */
    @Override
    public void onBindViewHolder(@NonNull AViewHolder holder, int position) {
        holder.eventName.setText(events.get(position).getEventName());

    }

    /**
     *returns amount of items
     *
     * @return int of list size
     */
    @Override
    public int getItemCount() {
        return events.size();
    }
}

/**
 * View holder for the recycler view
 *
 */
class AViewHolder extends RecyclerView.ViewHolder{
    Button eventName;
    /**
     * sets event names into recycler
     *
     * @param itemView the view
     *
     */
    public AViewHolder(@NonNull View itemView) {
        super(itemView);
        eventName = itemView.findViewById(R.id.event_list_item);
    }
}

