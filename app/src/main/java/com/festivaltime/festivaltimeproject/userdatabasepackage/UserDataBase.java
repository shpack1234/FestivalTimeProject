package com.festivaltime.festivaltimeproject.userdatabasepackage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.festivaltime.festivaltimeproject.ArrayListConverter;

@Database(entities = {UserEntity.class}, version = 23)
@TypeConverters({ArrayListConverter.class})
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}
