package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CalendarEntity.class}, version = 3)
public abstract class CalendarDatabase extends RoomDatabase {
    public abstract CalendarDao calendarDao();

    //Database instance 생성위한 singleton 적용
    private static CalendarDatabase INSTANCE;

    //Singleton 패턴을 구현하여 TodoDatabase의 인스턴스를 생성하고 반환
    public static synchronized CalendarDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CalendarDatabase.class, "schedule_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
