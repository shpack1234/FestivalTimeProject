package com.festivaltime.festivaltimeproject.inquirydatabasepackage;

import android.content.Context;
import androidx.room.Room;

import com.festivaltime.festivaltimeproject.InquiryDatabase;

public class DatabaseHolder {
    private static InquiryDatabase inquiryDatabase;

    public static InquiryDatabase getAppDatabase(Context context) {
        if (inquiryDatabase == null) {
            inquiryDatabase = Room.databaseBuilder(context,
                            InquiryDatabase.class, "my-database")
                    .allowMainThreadQueries() // 주의: UI 스레드에서 사용시에만 사용
                    .build();
        }
        return inquiryDatabase;
    }
}

