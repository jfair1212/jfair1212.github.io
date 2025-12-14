package com.example.cs360projectjustinfaircloth;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.kizitonwose.calendar.view.ViewContainer;

public class DayViewContainer extends ViewContainer {

    public final TextView textView;
    public com.kizitonwose.calendar.core.CalendarDay day;

    // Opens days that are clicked on
    public DayViewContainer(@NonNull View view) {
        super(view);
        textView = view.findViewById(R.id.calendarDayText);
        textView.setOnClickListener(v -> {
            if (day == null) return;
            if (day.getPosition() != com.kizitonwose.calendar.core.DayPosition.MonthDate) return;

            java.time.LocalDate date = day.getDate();
            Intent i = new Intent(v.getContext(), dayEvents.class);
            i.putExtra("date_iso", date.toString());
            v.getContext().startActivity(i);
        });
    }
}
