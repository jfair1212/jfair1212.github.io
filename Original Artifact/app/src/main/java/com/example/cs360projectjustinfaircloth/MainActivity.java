package com.example.cs360projectjustinfaircloth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView eventRecyclerView;
    private EventAdapter adapter;
    private AppDatabase db;
    private SharedPreferences sharedPreferences;
    private static final String CHANNEL_ID = "event_notifications";
    private final static int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        db = new AppDatabase(getApplicationContext());
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(new ArrayList<>());
        eventRecyclerView.setAdapter(adapter);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        notificationCheck();






        // Is log out button is pressed, user is taken back to sign in page
        Button logOutButton = findViewById(R.id.logoutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(MainActivity.this, signInPage.class));
            }
        });

        // User is taken to add event screen
        Button addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, addEventScreen.class));
            }
        });
    }


    // When MainActivity resumes, the screen is populated with events
    @Override
    protected void onResume() {
        super.onResume();

        String username = sharedPreferences.getString("username", null);
        List<Event> events;

        if (username == null) {
            events = Collections.emptyList();
        } else {
            events = db.orderEvents(username);
        }

        adapter.setEvents(events);
    }

    // Checks table for any events scheduled for today and sends notificaiton for each one
    private void notificationCheck() {
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        SQLiteDatabase db = appDatabase.getReadableDatabase();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);
        String todayDate = simpleDateFormat.format(new Date());

        String username = getSharedPreferences("auth", MODE_PRIVATE).getString("username", null);

        String query = "select event_name, time from events where date = ? and user_id = ?";
        String[] arguments = new String[]{todayDate, username};

        Cursor cursor = db.rawQuery(query, arguments);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String event_name = cursor.getString(cursor.getColumnIndexOrThrow("event_name"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                    String message = "Your event " + event_name + " is today at " + time;

                    createEventNotification(message);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                db.close();
            }
        }
    }


    // Creates notification channel to send notification
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notifications";
            String description = "Today's Events";
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


    // Puts together the status bar notification and sends to notification channel
    private void createEventNotification(String message) {

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}