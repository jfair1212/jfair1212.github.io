package com.example.cs360projectjustinfaircloth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class dayEvents extends AppCompatActivity{
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US);
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.day_events_screen);

        RecyclerView recyclerView = findViewById(R.id.dayEventsRecyclerView);
        TextView dayHeader = findViewById(R.id.headerTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(new ArrayList<>());
        recyclerView.setAdapter(eventAdapter);

        String dateString = getIntent().getStringExtra("date_string");
        LocalDate localDate = LocalDate.parse(dateString, dateFormatter);
        dayHeader.setText(localDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US)));
        TextView noEventMessage = findViewById(R.id.noEventMessage);


        // Events for day are shown. If no events, message appears
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).collection("events").whereEqualTo("date", dateString).get()
                .addOnSuccessListener(query -> {
                    List<Event> events = new ArrayList<>();

                    for (var doc : query.getDocuments()) {
                        Event event = new Event(uid, doc.getString("title"), doc.getString("date"), doc.getString("time"), doc.getLong("timestamp"), doc.getString("description"));
                        event.setFireStoreId(doc.getId());
                        events.add(event);
                    }

                    eventAdapter.setEvents(events);
                    if (events.isEmpty()) {
                        noEventMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noEventMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    noEventMessage.setText("Failed to load events");
                    noEventMessage.setVisibility(View.VISIBLE);
                });


        // If user presses return, screen returns to main activity screen
        Button returnButton = findViewById(R.id.returnToCalendarButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
