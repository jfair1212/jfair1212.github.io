package com.example.cs360projectjustinfaircloth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity {


    private EventAdapter adapter;
    private SharedPreferences sharedPreferences;
    private static final String CHANNEL_ID = "event_notifications";
    private final static int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;
    private final Set<LocalDate> eventDates = new HashSet<>();
    private static final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("M/d/yyyy", java.util.Locale.US);
    private static final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("MM/d/yyyy", java.util.Locale.US);

    // Helps parse dates
    private LocalDate parseDate(String string) {
        if (string == null) {
            return null;
        }

        LocalDate result = null;

        try {
            result = LocalDate.parse(string, dateFormatter1);
        } catch (Exception e) {
            result = null;
        }

        if (result == null) {
            try {
                result = LocalDate.parse(string, dateFormatter2);
            } catch (Exception e) {
                result = null;
            }
        }

        if (result != null) {
            return result;
        } else {
            return null;
        }
    }

    // Container for day layout
    public static class DayViewContainer extends ViewContainer {
        public final TextView textView;
        public DayViewContainer(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
        }
    }

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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadEventDates(uid);

        final CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            // Finds events matching selected day
            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
                container.textView.setText(String.valueOf(day.getDate().getDayOfMonth()));
                container.textView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                container.textView.setTextColor(android.graphics.Color.WHITE);

                if (day.getPosition() == com.kizitonwose.calendar.core.DayPosition.MonthDate) {
                    if (!eventDates.isEmpty() && eventDates.contains(day.getDate())) {
                        container.textView.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"));
                        container.textView.setTextColor(android.graphics.Color.WHITE);
                    } else {
                        container.textView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                        container.textView.setTextColor(android.graphics.Color.WHITE);
                    }

                    // When day is clicked, user is taken to screen for that day
                    container.textView.setOnClickListener(v -> {
                        String dateStr = day.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy", java.util.Locale.US));
                        Intent intent = new Intent(v.getContext(), dayEvents.class);
                        intent.putExtra("date_string", dateStr);
                        v.getContext().startActivity(intent);
                    });

                } else {
                    container.textView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    container.textView.setTextColor(android.graphics.Color.parseColor("#80FFFFFF"));
                    container.textView.setOnClickListener(null);
                }
            }
        });

        java.time.YearMonth currentMonth = java.time.YearMonth.now();
        java.time.YearMonth startMonth = currentMonth.minusMonths(5);
        java.time.YearMonth endMonth = currentMonth.plusMonths(12);
        java.time.DayOfWeek firstDayOfWeek = WeekFields.of(java.util.Locale.getDefault()).getFirstDayOfWeek();

        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        final TextView monthHeader = findViewById(R.id.monthHeader);
        final java.time.format.DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthHeader.setText(currentMonth.format(formatter));

        // Updates month header when user scrolls
        calendarView.setMonthScrollListener(new kotlin.jvm.functions.Function1<com.kizitonwose.calendar.core.CalendarMonth, kotlin.Unit>() {
            @Override
            public kotlin.Unit invoke(com.kizitonwose.calendar.core.CalendarMonth month) {
                monthHeader.setText(month.getYearMonth().format(formatter));
                return kotlin.Unit.INSTANCE;
            }
        });

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        notificationCheck();

        // Logs user out and returns to sign-in page
        Button logOutButton = findViewById(R.id.logoutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(MainActivity.this, signInPage.class));
            }
        });

        // Takes user to add event screen
        Button addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, addEventScreen.class));
            }
        });

        // Takes user to search event screen
        Button searchEventButton = findViewById(R.id.searchEventButton);
        searchEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, searchEventScreen.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadEventDates(uid);
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.notifyCalendarChanged();
    }

    // Checks DB for events for current user and puts them in a set
    private void loadEventDates(String uid) {
        eventDates.clear();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("users").document(uid).collection("events").get()
                .addOnSuccessListener(query -> {
                    for (var doc : query.getDocuments()) {
                        String dateString = doc.getString("date");
                        java.time.LocalDate date = parseDate(dateString);
                        eventDates.add(date);
                    }

                    CalendarView calendarView = findViewById(R.id.calendarView);
                    calendarView.notifyCalendarChanged();
                });
    }


    // Checks table for any events scheduled for today and sends notification for each one
    private void notificationCheck() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);
        String todayDate = simpleDateFormat.format(new Date());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(uid).collection("events").whereEqualTo("date", todayDate).get()
                .addOnSuccessListener(query -> {
                    for (var doc : query.getDocuments()) {
                        String eventName = doc.getString("title");
                        String time = doc.getString("time");

                        String notification = "Your event " + eventName + " is today at " + time;
                        createEventNotification(notification);
                    }
                });


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