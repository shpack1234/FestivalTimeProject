package com.festivaltime.festivaltimeproject.userdatabasepackage;

import android.content.Context;

import androidx.room.Room;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;

public class UserDataBaseSingleton {
    private static UserDataBase instance;

    public static synchronized UserDataBase getInstance(Context context) {
        if(instance==null) {
            instance= Room.databaseBuilder(context.getApplicationContext(), UserDataBase.class, "User_Database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
