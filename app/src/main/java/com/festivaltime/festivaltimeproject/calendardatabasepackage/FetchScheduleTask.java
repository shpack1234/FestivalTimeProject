package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import android.content.Context;
import android.view.View;
import java.util.LinkedHashMap;
import java.util.List;
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

    // FetchScheduleTask에서 일정 데이터를 가져온 후, CalendarActivity에 전달하는 리스너 인터페이스
    public interface FetchScheduleTaskListener {
        void onFetchCompleted(List<CalendarEntity> scheduleList);
    }
}
