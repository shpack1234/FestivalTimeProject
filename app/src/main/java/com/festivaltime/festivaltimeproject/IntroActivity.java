package com.festivaltime.festivaltimeproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.Random;
import com.festivaltime.festivaltimeproject.userdatabasepackage.*;

public class IntroActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceStare) {
        super.onCreate(savedInstanceStare);
        setContentView(R.layout.activity_intro);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(KEY_USER_ID, null);

        if (userId == null) {
            userId = generateUniqueId();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
        }

        Log.d("Your ID", userId);

        UserDataBase db = UserDataBaseSingleton.getInstance(getApplicationContext());

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private String generateUniqueId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder uniqueId = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            uniqueId.append(characters.charAt(index));
        }

        return uniqueId.toString();
    }
}
