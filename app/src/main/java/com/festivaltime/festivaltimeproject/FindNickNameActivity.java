package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FindNickNameActivity extends AppCompatActivity {

    private UserDataBase db;
    private UserDao userDao;

    private UserEntity loadedUser;
    private String userId;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nick_name);

        getSupportActionBar().hide();
        TextView nicknameText = findViewById(R.id.nickname_hint);
        Button inquiryButton = findViewById(R.id.nickname_inquiry);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder maskedNickname = new StringBuilder();
                        int length = loadedUser.getUserName().length();
                        for (int i = 0; i < length; i++) {
                            if (i < length / 2)
                                maskedNickname.append(loadedUser.getUserName().charAt(i));
                            else maskedNickname.append("*");
                        }
                        nicknameText.setText("닉네임 : " + maskedNickname);
                    }
                });
            }
        });
    }

    public void inquiryOnClick(View v) {
        Intent urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.kakao.com/o/sEEKjlJf"));
        startActivity(urlintent);
    }
}