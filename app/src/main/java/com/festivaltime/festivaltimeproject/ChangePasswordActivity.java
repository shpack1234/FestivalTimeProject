package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChangePasswordActivity extends AppCompatActivity {

    private UserDataBase db;
    private UserDao userDao;
    private String userId;

    private UserEntity loadedUser;

    private boolean isLogin;

    boolean userExist = false;

    public ImageButton Back_Btn;

    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().hide();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        EditText currentPW = findViewById(R.id.currentPW);
        EditText changePW = findViewById(R.id.changePW);
        EditText checkPW = findViewById(R.id.checkPW);
        Button changeBtn = findViewById(R.id.change_button);

        currentPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 포커스를 가지면 밑줄 색상을 selected_color로 변경
                    currentPW.setBackgroundTintList(getResources().getColorStateList(R.color.light_red));
                } else {
                    // 포커스를 잃으면 밑줄 색상을 dark_gray로 변경
                    currentPW.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                }
            }
        });

        changePW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 포커스를 가지면 밑줄 색상을 selected_color로 변경
                    changePW.setBackgroundTintList(getResources().getColorStateList(R.color.light_red));
                } else {
                    // 포커스를 잃으면 밑줄 색상을 dark_gray로 변경
                    changePW.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                }
            }
        });

        checkPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 포커스를 가지면 밑줄 색상을 selected_color로 변경
                    checkPW.setBackgroundTintList(getResources().getColorStateList(R.color.light_red));
                } else {
                    // 포커스를 잃으면 밑줄 색상을 dark_gray로 변경
                    checkPW.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                }
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(loadedUser.getUserId().equals("000000")) {
                                    Toast.makeText(getApplicationContext(), "admin 계정의 비밀번호는 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (currentPW.getText().toString().equals(loadedUser.getPassword())) {
                                        if (changePW.getText().toString().equals(checkPW.getText().toString())) {
                                            loadedUser.setPassword(changePW.getText().toString());
                                            executor.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    userDao.insertOrUpdate(loadedUser);
                                                }
                                            });
                                            finish();
                                        } else {
                                            checkPW.setError("비밀번호가 일치하지 않습니다.");
                                        }
                                    } else {
                                        currentPW.setError("잘못 된 비밀번호 입니다.");
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });


        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Back_Btn = findViewById(R.id.before_btn);

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);//하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    navigateToMainActivity(ChangePasswordActivity.this);
                    return true;
                case R.id.action_map:
                    navigateToMapActivity(ChangePasswordActivity.this);
                    return true;
                case R.id.action_calendar:
                    navigateToCalendarActivity(ChangePasswordActivity.this);
                    return true;
                case R.id.action_favorite:
                    return true;
                case R.id.action_profile:
                    navigateToMyPageActivity(ChangePasswordActivity.this);
                    return true;
            }
            return false;
        });
    }
}