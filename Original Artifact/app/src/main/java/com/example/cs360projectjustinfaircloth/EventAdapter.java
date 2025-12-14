package com.example.cs360projectjustinfaircloth;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events = new ArrayList<>();
    public EventAdapter(List<Event> eventList) {
        this.events = eventList;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView dateTimeText;
        private TextView descriptionText;
        private Button deleteEventButton;
        private Button editEventButton;

        public EventViewHolder(View view ) {
            super(view);

            titleText = view.findViewById(R.id.eventTitle);
            dateTimeText = view.findViewById(R.id.eventDateTime);
            descriptionText = view.findViewById(R.id.eventNotes);
            deleteEventButton = view.findViewById(R.id.deleteEventCardButton);
            editEventButton = view.findViewById(R.id.editEventCardButton);
        }
    }



    // Creates new viewholder
    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(eventView);
    }


    // Injects table data into event cards
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {

        String title = events.get(position).getTitle();
        String date = events.get(position).getDate();
        String time = events.get(position).getTime();
        String description = events.get(position).getDescription();

        holder.titleText.setText(title);
        holder.dateTimeText.setText(date + " - " + time);
        holder.descriptionText.setText(description);


        // If delete button is pressed, event is deleted from table and event card is removed from recyclerview
        holder.deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                Event event = events.get(adapterPosition);

                AppDatabase db = AppDatabase.getInstance(v.getContext());
                db.deleteEvent(event.getId());

                events.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, events.size() - adapterPosition);


            }
        });


        // If edit button is pressed, relevant info is passed through intent and user is taken to edit screen
        holder.editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                Event event = events.get(adapterPosition);
                android.content.Context context = v.getContext();
                android.content.Intent intent = new android.content.Intent(context, editEventScreen.class);


                intent.putExtra("event_id", event.getId());
                intent.putExtra("title", event.getTitle());
                intent.putExtra("date", event.getDate());
                intent.putExtra("time", event.getTime());
                intent.putExtra("description", event.getDescription());


                context.startActivity(intent);
            }
        });
    }


    // Updates adapter's data and refreshes list
    public void setEvents(List<Event> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        notifyDataSetChanged();
    }


    // Counts items in recycler
    @Override
    public int getItemCount() {
        return events.size();
    }
}
