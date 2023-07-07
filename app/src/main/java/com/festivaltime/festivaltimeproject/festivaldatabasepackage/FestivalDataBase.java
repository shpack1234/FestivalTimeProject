package com.festivaltime.festivaltimeproject.festivaldatabasepackage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.festivaltime.festivaltimeproject.ArrayListConverter;

@Database(entities = {FestivalEntity.class}, version = 1)
@TypeConverters({ArrayListConverter.class})
public abstract class FestivalDataBase extends RoomDatabase {
    public abstract FestivalDao festivalDao();
}
