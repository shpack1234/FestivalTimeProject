package com.festivaltime.festivaltimeproject;

import android.content.Context;

import androidx.room.Room;

public class UserDataBaseSingleton {
    private static UserDataBase instance;

    public static synchronized UserDataBase getInstance(Context context) {
        if(instance==null) {
            instance= Room.databaseBuilder(context.getApplicationContext(), UserDataBase.class, "User_Database").build();
        }
        return instance;
    }
}
