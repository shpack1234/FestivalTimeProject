package com.festivaltime.festivaltimeproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {InquiryEntity.class}, version = 1)
public abstract class InquiryDatabase extends RoomDatabase {

    public abstract InquiryDao inquiryDao();
}
