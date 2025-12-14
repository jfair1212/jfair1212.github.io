package com.example.cs360projectjustinfaircloth;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "application.db";
    private static final int VERSION = 7;
    private SharedPreferences sharedPreferences;

    private static volatile AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = new AppDatabase(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class userTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    private static final class eventTable {
        static final String TABLE = "events";
        static final String COL_ID = "_id";
        static final String COL_USER_ID = "user_id";
        static final String COL_TITLE = "event_name";
        static final String COL_DATE = "date";
        static final String COL_TIME = "time";
        static final String COL_TIMESTAMP = "timestamp";
        static final String COL_DESCRIPTION = "description";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + userTable.TABLE + " (" +
                userTable.COL_ID + " integer primary key, " +
                userTable.COL_USERNAME + " text not null unique , " +
                userTable.COL_PASSWORD + " text not null)");

        db.execSQL("create table " + eventTable.TABLE + " (" +
                eventTable.COL_ID + " integer primary key autoincrement, " +
                eventTable.COL_USER_ID + " text not null, " +
                eventTable.COL_TITLE + " text not null, " +
                eventTable.COL_DATE + " text not null, " +
                eventTable.COL_TIME + " text not null, " +
                eventTable.COL_TIMESTAMP + " integer not null, " +
                eventTable.COL_DESCRIPTION + " text not null)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion) {
        db.execSQL("drop table if exists " + eventTable.TABLE);
        db.execSQL("drop table if exists " + userTable.TABLE);
        onCreate(db);
    }

    //Method for adding user to table
    public long addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(userTable.COL_USERNAME, username);
        values.put(userTable.COL_PASSWORD, password);

        long userId = db.insert(userTable.TABLE, null, values);
        return userId;
    }

    //Method for adding event to table
    public long addEvent(String username, String title, String date, String time, String desc) {
        SQLiteDatabase db = getWritableDatabase();

        // Turns date and time input into unix timestamp int
        String dateTime = date + " " + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.US);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ContentValues values = new ContentValues();
        values.put(eventTable.COL_USER_ID, username);
        values.put(eventTable.COL_TITLE, title);
        values.put(eventTable.COL_DATE, date);
        values.put(eventTable.COL_TIME, time);
        values.put(eventTable.COL_TIMESTAMP, timestamp);
        values.put(eventTable.COL_DESCRIPTION, desc);

        long returnValue = db.insert(eventTable.TABLE, null, values);
        return returnValue;
    }

    //Validates log in on sign in screen
    public boolean validateLogIn(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        if (username == null || password == null) {
            return false;
        }

        String searchQuery = "select * from " + userTable.TABLE +
                " where " + userTable.COL_USERNAME + " = ? " +
                " and " + userTable.COL_PASSWORD + " = ?";

        try (android.database.Cursor c = db.rawQuery(searchQuery, new String[] {username, password})) {
            return c.moveToFirst();
        }
    }

    //Returns id from table
    public int getId(String username) {

        SQLiteDatabase db = getReadableDatabase();
        try (android.database.Cursor c = db.query(
                "users",
                new String[] { "_id" },
                "username=?",
                new String[] { username },
                null, null, null,
                "1")) {
            return c.moveToFirst() ? c.getInt(0) : null;
        }
    }

    // Orders events from newest to oldest through timestamp and returns sorted list
    public List<Event> orderEvents(String username) {

        SQLiteDatabase db= getWritableDatabase();
        List<Event> eventList = new ArrayList<>();

        Cursor c = db.rawQuery("select * from " + eventTable.TABLE + " where " + eventTable.COL_USER_ID + " = ?" + " order by " + eventTable.COL_TIMESTAMP + " ASC", new String[]{username});

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(eventTable.COL_ID));
                String userId = c.getString(c.getColumnIndexOrThrow(eventTable.COL_USER_ID));
                String title = c.getString(c.getColumnIndexOrThrow(eventTable.COL_TITLE));
                String date = c.getString(c.getColumnIndexOrThrow(eventTable.COL_DATE));
                String time = c.getString(c.getColumnIndexOrThrow(eventTable.COL_TIME));
                long timestamp = c.getLong(c.getColumnIndexOrThrow(eventTable.COL_TIMESTAMP));
                String description = c.getString(c.getColumnIndexOrThrow(eventTable.COL_DESCRIPTION));

                Event event = new Event(id, userId, title, date, time, timestamp, description);
                eventList.add(event);
            } while (c.moveToNext());
        }

        c.close();
        return eventList;
    }

    // Deletes event from table
    public void deleteEvent(long eventId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("events", "_id = ?", new String[]{String.valueOf(eventId)});
    }

    // Edits event and injects new info to table
    public void editEvent(long eventID, String title, String date, String time, String description) {
        SQLiteDatabase db = getWritableDatabase();

        String dateTime = date + " " + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.US);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();



        ContentValues values = new ContentValues();
        values.put(eventTable.COL_TITLE, title);
        values.put(eventTable.COL_DATE, date);
        values.put(eventTable.COL_TIME, time);
        values.put(eventTable.COL_TIMESTAMP, timestamp);
        values.put(eventTable.COL_DESCRIPTION, description);

        db.update("events", values, eventTable.COL_ID + " = ?", new String[]{String.valueOf(eventID)});
    }
}
