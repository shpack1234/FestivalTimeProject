package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import android.content.Context;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FetchScheduleTask {
    private Context mContext;
    private CalendarDao calendarDao;

    public FetchScheduleTask(Context context, CalendarDao calendarDao) {
        this.mContext = context;
        this.calendarDao = calendarDao;
    }

    public void fetchSchedules(FetchScheduleTaskListener listener) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 데이터베이스에서 일정 데이터를 가져오기 위해 DAO를 사용하여 쿼리 수행
                List<CalendarEntity> schedules = calendarDao.getAllCalendarEntity();

                // 가져온 일정 데이터를 listener를 통해 CalendarActivity로 전달
                listener.onFetchCompleted(schedules);
            }
        });
    }

    // 해당 날짜에 속하는 일정 데이터를 가져오는 메소드
    public List<CalendarEntity> fetchSchedulesForDate(Date date, boolean showft, boolean showholi) {
        List<CalendarEntity> allSchedules = calendarDao.getAllCalendarEntity();
        List<CalendarEntity> matchingSchedules = new ArrayList<>();

        // date의 시간을 자정으로 설정
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        dateCalendar.set(Calendar.MINUTE, 0);
        dateCalendar.set(Calendar.SECOND, 0);
        dateCalendar.set(Calendar.MILLISECOND, 0);

        for (CalendarEntity schedule : allSchedules) {
            String categoryColor = schedule.category;
            Date startDate = convertStringToDate(schedule.startDate);
            Date endDate = convertStringToDate(schedule.endDate);

            if (categoryColor == null) {
                // 오류시 비출 기본 색상 값 설정
                categoryColor = "#000000";
            }

            if ((showft && categoryColor.equals("#ed5c55")) ||
                    (showholi && categoryColor.equals("#52c8ed")) ||
                    ((!categoryColor.equals("#52c8ed")) &&
                            (!categoryColor.equals("#ed5c55")))) {
                // 시간을 무시한 날짜 비교
                if (startDate != null && endDate != null &&
                        !dateCalendar.getTime().before(startDate) &&
                        !dateCalendar.getTime().after(endDate)) {
                    matchingSchedules.add(schedule);
                }
            }
        }

        return matchingSchedules;
    }


    // 문자열 형태의 날짜를 Date 객체로 변환하는 메소드
    private Date convertStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // FetchScheduleTask에서 일정 데이터를 가져온 후, CalendarActivity에 전달하는 리스너 인터페이스
    public interface FetchScheduleTaskListener {
        void onFetchCompleted(List<CalendarEntity> scheduleList);
    }

}
