package com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CalendarCategoryEntity.class}, version = 3)
public abstract class CalendarCategoryDataBase extends RoomDatabase {
    public abstract CalendarCategoryDao categoryDao();

    //Database instance 생성위한 singleton 적용
    private static CalendarCategoryDataBase INSTANCE;

    //Singleton 패턴을 구현하여 TodoDatabase의 인스턴스를 생성하고 반환
    public static synchronized CalendarCategoryDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CalendarCategoryDataBase.class, "category_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
