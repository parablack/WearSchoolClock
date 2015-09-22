package net.parablack.clocktest.watchface;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Simon on 16.09.2015.
 */
public class TimeUtils {

    public static Calendar currentCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        return  calendar;

    }

    public static long dayMillis(){
        return (currentCalendar().getTimeInMillis() + currentCalendar().get(Calendar.DST_OFFSET) + currentCalendar().get(Calendar.ZONE_OFFSET)) % (24 * 60 * 60 * 1000);

    }

}
