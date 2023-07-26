package com.festivaltime.festivaltimeproject.calendaract;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;

public class ScheduleLoader extends AsyncTaskLoader<Void> {
    private CalendarEntity newSchedule;
    private CalendarDao calendarDao;

    public ScheduleLoader(Context context, CalendarEntity schedule, CalendarDao dao) {
        super(context);
        this.newSchedule = schedule;
        this.calendarDao = dao;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Void loadInBackground() {
        calendarDao.InsertSchedule(newSchedule);
        return null;
    }
}