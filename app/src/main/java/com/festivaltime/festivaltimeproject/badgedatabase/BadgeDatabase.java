package com.festivaltime.festivaltimeproject.badgedatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BadgeEntity.class}, version = 1)
public abstract class BadgeDatabase extends RoomDatabase {
    public abstract BadgeDao badgeDao();

    //Database instance 생성위한 singleton 적용
    private static BadgeDatabase INSTANCE;

    //Singleton 패턴을 구현하여 TodoDatabase의 인스턴스를 생성하고 반환
    public static synchronized BadgeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BadgeDatabase.class, "badge_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
