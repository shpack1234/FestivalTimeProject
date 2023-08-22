package com.festivaltime.festivaltimeproject.inquirydatabasepackage;

import android.content.Context;
import androidx.room.Room;

public class InquiryDataBaseSingleton{

    private static InquiryDataBase INSTANCE;

    public static synchronized InquiryDataBase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), InquiryDataBase.class, "inquiry_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
