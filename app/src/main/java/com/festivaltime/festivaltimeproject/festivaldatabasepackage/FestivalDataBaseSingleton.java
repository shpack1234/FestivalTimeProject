package com.festivaltime.festivaltimeproject.festivaldatabasepackage;

import android.content.Context;

import androidx.room.Room;
import com.festivaltime.festivaltimeproject.festivaldatabasepackage.*;

public class FestivalDataBaseSingleton {
    private static FestivalDataBase instance;

    public static synchronized FestivalDataBase getInstance(Context context) {
        if(instance==null) {
            instance= Room.databaseBuilder(context.getApplicationContext(), FestivalDataBase.class, "Festival_Database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
