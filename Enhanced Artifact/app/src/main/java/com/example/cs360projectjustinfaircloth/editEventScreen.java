package com.example.cs360projectjustinfaircloth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class editEventScreen extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event_screen);

        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        Intent intent = getIntent();



        // Sets field values from intent
        String docId = intent.getStringExtra("event_id");

        EditText eventName = findViewById(R.id.editEventTitleInput);
        eventName.setText(intent.getStringExtra("title"));

        EditText datePicker = findViewById(R.id.editEventDateInput);
        datePicker.setText(intent.getStringExtra("date"));

        EditText timePicker = findViewById(R.id.editEventTimeInput);
        timePicker.setText(intent.getStringExtra("time"));


        EditText description = findViewById(R.id.editEventNotesInput);
        description.setText(intent.getStringExtra("description"));


        // Creates date picker for user to select date

        datePicker.setInputType(InputType.TYPE_NULL);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(editEventScreen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dateInput = (month + 1) + "/" + dayOfMonth + "/" + year;
                        datePicker.setText(dateInput);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        // Creates time picker for user to select time

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(editEventScreen.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeInput = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                        timePicker.setText(timeInput);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });



        // If user presses cancel, screen returns to main activity screen
        Button cancelButton = findViewById(R.id.buttonCancelEditScreen);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Grabs values from input fields and inserts into event table
        Button editEventButton = findViewById(R.id.buttonEditEventScreen);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText eventNameInput = findViewById(R.id.editEventTitleInput);
                EditText eventDateInput = findViewById(R.id.editEventDateInput);
                EditText eventTimeInput = findViewById(R.id.editEventTimeInput);
                EditText eventNotesInput = findViewById(R.id.editEventNotesInput);

                String eventName = eventNameInput.getText().toString().trim();
                String eventDate = eventDateInput.getText().toString().trim();
                String eventTime = eventTimeInput.getText().toString().trim();
                String eventNotes = eventNotesInput.getText().toString().trim();
                long timestamp;

                try {
                    String dateTime = eventDate + " " + eventTime;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.US);
                    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                    timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                } catch (Exception e) {
                    Toast.makeText(editEventScreen.this, "Invalid date or time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eventName.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty() || eventNotes.isEmpty()) {
                    Toast.makeText(editEventScreen.this, "Please fill in all information.", Toast.LENGTH_SHORT).show();
                } else {
                    new EventDatabaseFirebase().editEvent(docId, eventName, eventDate, eventTime, timestamp, eventNotes, null, null);
                    Toast.makeText(editEventScreen.this, "Event has been edited.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }}
