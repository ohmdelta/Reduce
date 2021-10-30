package com.app.wreduce;

import android.widget.DatePicker;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public final class CalendarFunctions {

    public static void clearTimeOfDay(Calendar calendar) {
	    calendar.clear(Calendar.HOUR);
	    calendar.clear(Calendar.MINUTE);
	    calendar.clear(Calendar.SECOND);
    }

    public static Calendar offsetTodayBy(int addedDays) {
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, addedDays);
        return cal;
    }

    @NotNull
    public static Calendar getCalendarToday(DatePicker expiryPicker) {
        // calendar get date
        Calendar cal = Calendar.getInstance();
        setDateTo(cal, expiryPicker.getDayOfMonth(), expiryPicker.getMonth(), expiryPicker.getYear());

        return cal;
    }

    private static void setDateTo(Calendar cal, int day, int month, int year) {
	    cal.set(Calendar.DAY_OF_MONTH, day);
	    cal.set(Calendar.MONTH, month);
	    cal.set(Calendar.YEAR, year);
	}

	public static void setTimeTo(Calendar cal, int hour, int minute, int second) {
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
	}
}
