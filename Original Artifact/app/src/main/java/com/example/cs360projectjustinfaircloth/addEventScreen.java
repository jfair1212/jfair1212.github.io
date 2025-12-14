package com.example.cs360projectjustinfaircloth;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class addEventScreen extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_screen);

    sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);








        // Creates date picker for user to select date
        EditText datePicker = findViewById(R.id.addEventDateInput);
        datePicker.setInputType(InputType.TYPE_NULL);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(addEventScreen.this, new DatePickerDialog.OnDateSetListener() {
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
        EditText timePicker = findViewById(R.id.addEventTimeInput);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(addEventScreen.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeInput = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                        timePicker.setText(timeInput);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });




        //If user clicks cancel button, returns to main screen
        Button cancelButton = findViewById(R.id.buttonCancelAddScreen);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






        // Grabs values from input fields and inserts into event table
        Button addEventButton = findViewById(R.id.buttonAddEventScreen);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppDatabase db = AppDatabase.getInstance(addEventScreen.this);


                EditText eventNameInput = findViewById(R.id.addEventTitleInput);
                EditText eventDateInput = findViewById(R.id.addEventDateInput);
                EditText eventTimeInput = findViewById(R.id.addEventTimeInput);
                EditText eventNotesInput = findViewById(R.id.addEventNotesInput);


                String username = sharedPreferences.getString("username", null);
                String eventName = eventNameInput.getText().toString().trim();
                String eventDate = eventDateInput.getText().toString().trim();
                String eventTime = eventTimeInput.getText().toString().trim();
                String eventNotes = eventNotesInput.getText().toString().trim();


                //Displays message if any field is empty. Otherwise, adds event to table
                if (eventName.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty() || eventNotes.isEmpty()) {
                    Toast.makeText(addEventScreen.this, "Please fill in all information.", Toast.LENGTH_SHORT).show();
                } else {
                    db.addEvent(username, eventName, eventDate, eventTime, eventNotes);
                    finish();
                }




            }
        });
}}
