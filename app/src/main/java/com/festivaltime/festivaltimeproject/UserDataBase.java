package com.festivaltime.festivaltimeproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UserEntity.class}, version = 1)
@TypeConverters({ArrayListConverter.class})
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}
