package com.festivaltime.festivaltimeproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {FestivalEntity.class}, version = 1)
@TypeConverters({ArrayListConverter.class})
public abstract class FestivalDataBase extends RoomDatabase {
    public abstract FestivalDao festivalDao();
}
