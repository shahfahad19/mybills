package com.fahad.mybills.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static int calculateDaysLeft(String dueDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
        try {
            Date currentDate = new Date();
            Date dueDate = dateFormat.parse(dueDateString);

            // Clear the time components of the dates
            currentDate = clearTimeComponents(currentDate);
            dueDate = clearTimeComponents(dueDate);

            long timeDifference = dueDate.getTime() - currentDate.getTime();
            return (int) (timeDifference / (24 * 60 * 60 * 1000)); // Convert milliseconds to days
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Handle parse exception
        }
    }

    // Helper method to clear time components of a Date
    private static Date clearTimeComponents(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
