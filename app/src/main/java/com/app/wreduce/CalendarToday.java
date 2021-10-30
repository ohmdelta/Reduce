package com.app.wreduce;

import android.widget.DatePicker;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class CalendarToday {
    public CalendarToday() {
    }

    @NotNull
    Calendar getCalendarToday(DatePicker expiryPicker) {
        // calendar get date
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, expiryPicker.getYear());
        cal.set(Calendar.MONTH, expiryPicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, expiryPicker.getDayOfMonth());
        return cal;
    }
}