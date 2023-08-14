package com.festivaltime.festivaltimeproject.calendaract;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
    public static Calendar selectedDate;

    // 두 날짜가 같은 날짜인지 비교하는 메서드
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
