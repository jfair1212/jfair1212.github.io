package com.example.cs360projectjustinfaircloth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDatabaseFirebase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Get firebase uid
    private String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return null;
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // Adds events to Firebase
    public void addEvent(String title, String date, String time, long timestamp, String description, Runnable onSuccess, Runnable onFailure) {
        String uid = getUid();
        if (uid == null) {
            if (onFailure != null) onFailure.run();
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("time", time);
        event.put("timestamp", timestamp);
        event.put("description", description);

        db.collection("users").document(uid).collection("events").add(event)
                .addOnSuccessListener(documentReference -> {
                    if (onSuccess != null) onSuccess.run();
                }).addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.run();
                });
    }

    // Deletes event from Firebase
    public void deleteEvent(String eventId, Runnable onSuccess, Runnable onFailure) {
        String uid = getUid();
        if (uid == null) return;

        db.collection("users").document(uid).collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) onSuccess.run();
                }).addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.run();
                });
    }

    // Function for editing event
    public void editEvent(String eventId, String title, String date, String time, long timestamp, String description, Runnable onSuccess, Runnable onFailure) {
        String uid = getUid();
        if (uid == null) return;

        Map<String, Object> updated = new HashMap<>();
        updated.put("title", title);
        updated.put("date", date);
        updated.put("time", time);
        updated.put("timestamp", timestamp);
        updated.put("description", description);

        db.collection("users").document(uid).collection("events").document(eventId).update(updated).addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) onSuccess.run();
                }).addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.run();
                });
    }

    // Gets events for specific day
    public void getEventsForDate(String date, FirestoreCallback callback) {
        String uid = getUid();
        if (uid == null) return;

        db.collection("users").document(uid).collection("events").whereEqualTo("date", date).orderBy("timestamp").get()
                .addOnSuccessListener(query -> {
                    List<Event> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Event event = new Event(uid, doc.getString("title"), doc.getString("date"), doc.getString("time"), doc.getLong("timestamp"), doc.getString("description"));
                        event.setFireStoreId(doc.getId());
                        list.add(event);
                    }
                    callback.onCallback(list);
                });
    }

    // Gets all the events in the database
    public void getAllEvents(FirestoreCallback callback) {
        String uid = getUid();
        if (uid == null) {
            return;
        }

        db.collection("users").document(uid).collection("events").orderBy("timestamp").get()
                .addOnSuccessListener(query -> {
                    List<Event> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Event event = new Event(uid, doc.getString("title"), doc.getString("date"), doc.getString("time"), doc.getLong("timestamp"), doc.getString("description")
                        );
                        event.setFireStoreId(doc.getId());
                        list.add(event);
                    }
                    callback.onCallback(list);
                });
    }

    public interface FirestoreCallback {
        void onCallback(List<Event> list);
    }
}