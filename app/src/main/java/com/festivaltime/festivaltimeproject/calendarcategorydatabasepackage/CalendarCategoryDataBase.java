package com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.festivaltime.festivaltimeproject.ArrayListConverter;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.festivaldatabasepackage.FestivalDao;
import com.festivaltime.festivaltimeproject.festivaldatabasepackage.FestivalEntity;

@Database(entities = {CalendarCategoryEntity.class}, version = 1)
public abstract class CalendarCategoryDataBase extends RoomDatabase {
    public abstract CalendarCategoryDao categoryDao();

    //Database instance 생성위한 singleton 적용
    private static com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase INSTANCE;

    //Singleton 패턴을 구현하여 TodoDatabase의 인스턴스를 생성하고 반환
    public static synchronized com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase.class, "category_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
