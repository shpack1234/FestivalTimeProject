package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import android.content.Context;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public List<CalendarEntity> fetchSchedulesForDate(Date date) {
        List<CalendarEntity> allSchedules = calendarDao.getAllCalendarEntity();
        List<CalendarEntity> matchingSchedules = new ArrayList<>();

        for (CalendarEntity schedule : allSchedules) {
            Date startDate = convertStringToDate(schedule.startDate);
            Date endDate = convertStringToDate(schedule.endDate);

            if (startDate != null && endDate != null && date.after(startDate) && date.before(endDate)) {
                matchingSchedules.add(schedule);
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
