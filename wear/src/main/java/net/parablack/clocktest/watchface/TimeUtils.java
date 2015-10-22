package net.parablack.clocktest.watchface;

import java.util.Calendar;
import java.util.TimeZone;


public class TimeUtils {

    /**
     *
     * @return An current Calendar of the current timezone
     */
    public static Calendar currentCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

    /**
     *
     * @return The milliseconds since the start of the day
     */
    public static long dayMillis(){
        return (currentCalendar().getTimeInMillis() + currentCalendar().get(Calendar.DST_OFFSET) + currentCalendar().get(Calendar.ZONE_OFFSET)) % (24 * 60 * 60 * 1000);

    }

}
