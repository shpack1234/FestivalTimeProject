package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import android.content.Context;
import android.widget.TextView;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FetchScheduleTask {
    private Context mContext;
    private TextView scheduleText;
    private CalendarDao calendarDao;

    public FetchScheduleTask(Context context, TextView scheduleText, CalendarDao calendarDao) {
        this.mContext = context;
        this.scheduleText = scheduleText;
        this.calendarDao = calendarDao;
    }

    public void fetchSchedules() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 데이터베이스에서 일정 데이터를 쿼리하여 가져오는 작업 수행
                List<CalendarEntity> schedules = calendarDao.getAllCalendarEntity();

                // 가져온 일정 데이터를 문자열로 변환하여 UI에 표시
                final StringBuilder stringBuilder = new StringBuilder();
                for (CalendarEntity schedule : schedules) {
                    String newData = schedule.title + " - " + schedule.startDate + " ~ " + schedule.endDate;
                    stringBuilder.append(newData).append("\n");
                }

                // UI 업데이트 작업을 메인 스레드에서 수행
                scheduleText.post(new Runnable() {
                    @Override
                    public void run() {
                        scheduleText.setText(stringBuilder.toString());
                    }
                });
            }
        });
    }
}