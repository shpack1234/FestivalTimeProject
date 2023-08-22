package com.festivaltime.festivaltimeproject.inquirydatabasepackage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.festivaltime.festivaltimeproject.ArrayListConverter;
import com.festivaltime.festivaltimeproject.inquirydatabasepackage.InquiryDao;

@Database(entities = {InquiryEntity.class}, version = 1)
@TypeConverters({ArrayListConverter.class})
public abstract class  InquiryDataBase extends RoomDatabase {
    public abstract InquiryDao InquiryDao();
}
