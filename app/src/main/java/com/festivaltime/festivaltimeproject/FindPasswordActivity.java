package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FindPasswordActivity extends AppCompatActivity {

    private UserDataBase db;
    private UserDao userDao;

    private UserEntity loadedUser;
    private String userId;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        getSupportActionBar().hide();
        EditText nicknameEditText = findViewById(R.id.find_password_nickname);
        DatePicker birth = findViewById(R.id.find_password_birth);
        Button confirmButton = findViewById(R.id.find_password_confirm_button);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadedUser = userDao.getUserInfoById(userId);

                        String confirmNickname = nicknameEditText.getText().toString();
                        int year = birth.getYear();
                        int month = birth.getMonth() + 1;
                        int day = birth.getDayOfMonth();
                        String confirmBirth = year + "-" + month + "-" + day;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (confirmNickname.equals(loadedUser.getUserName()) && confirmBirth.equals(loadedUser.getUserBirth())) {
                                    Toast.makeText(getApplicationContext(), "비밀번호는 " + loadedUser.getPassword() + " 입니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "정보를 정확하게 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });

    }

    public void inquiryOnClick(View view) {
        Intent urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.kakao.com/o/sEEKjlJf"));
        startActivity(urlintent);
    }
}