package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private UserDataBase db;
    private UserDao userDao;

    private UserEntity loadedUser;
    private String userId;
    private Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        Button loginButton = findViewById(R.id.login_button);
        EditText userName = findViewById(R.id.login_nickname);
        EditText userPassword = findViewById(R.id.login_password);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (loadedUser.getUserName().equals(userName.getText().toString()) && loadedUser.getPassword().equals(userPassword.getText().toString())) {
                                    loadedUser.setIsLogin(true);
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            userDao.insertOrUpdate(loadedUser);
                                        }
                                    });
                                    finish();
                                    navigateToSomeActivity.navigateToMainActivity(LoginActivity.this);
                                } else if (loadedUser.getUserName().equals(userName.getText().toString())) {
                                    userName.setError("해당 닉네임이 존재하지 않습니다.");
                                } else {
                                    userPassword.setError("잘못된 비밀번호입니다.");
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}