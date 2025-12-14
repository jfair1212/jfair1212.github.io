package com.example.cs360projectjustinfaircloth;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class searchEventScreen extends AppCompatActivity {
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();


    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_event_screen);

        EventDatabaseFirebase db = new EventDatabaseFirebase();
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        eventAdapter = new EventAdapter(new java.util.ArrayList<>());
        RecyclerView recyclerView = findViewById(R.id.searchScreenRecyclerView);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView noEventMsg = findViewById(R.id.noEventSearchMessage);

        SearchView searchView = findViewById(R.id.searchBar);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(id);
        searchText.setTextColor(Color.parseColor("#d4d4d4"));
        searchText.setHintTextColor(Color.parseColor("#ffffff"));

        // Displays "no events" message if user has no planned events
        db.getAllEvents(events -> {
            eventList.clear();
            eventList.addAll(events);
            eventAdapter.setEvents(events);

            if (events.isEmpty()) {
                noEventMsg.setText("You have no events planned.");
                noEventMsg.setVisibility(View.VISIBLE);
            } else {
                noEventMsg.setVisibility(View.GONE);
            }
        });

        // Handles user's queries in search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // Queries eventList every time user types. Displays "No events" message if none match query
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Event> events = new java.util.ArrayList<>();

                for (Event event : eventList) {
                    if (event.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        events.add(event);
                    }
                }

                eventAdapter.setEvents(events);
                if (events.isEmpty()) {
                    noEventMsg.setText("No matching events found.");
                    noEventMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventMsg.setVisibility(View.GONE);
                }
                return true;
            }
        });

        // Takes user back to main calendar screen
        Button backButton = findViewById(R.id.returnToCalendarButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
