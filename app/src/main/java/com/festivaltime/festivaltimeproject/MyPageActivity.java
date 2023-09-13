package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToAppSettingActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToChangePasswordActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToBadgeActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToInquiryActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToLoginActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToPrivacyActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class MyPageActivity extends AppCompatActivity {

    private UserDataBase db;
    private UserDao userDao;
    private String userId;

    private UserEntity loadedUser;

    private boolean isLogin;

    boolean userExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);//하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    navigateToMainActivity(MyPageActivity.this);
                    return true;
                case R.id.action_map:
                    navigateToMapActivity(MyPageActivity.this);
                    return true;
                case R.id.action_calendar:
                    navigateToCalendarActivity(MyPageActivity.this);
                    return true;
                case R.id.action_favorite:
                    navigateToFavoriteActivity(MyPageActivity.this);
                    return true;
                case R.id.action_profile:
                    navigateToMyPageActivity(MyPageActivity.this);
                    return true;
            }
            return false;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                if (loadedUser != null) {
                    if (loadedUser.getIsLogin()) {
                        userId = loadedUser.getUserId();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View blurLayout = findViewById(R.id.blur_layout);
                                blurLayout.setVisibility(View.GONE);

                                TextView userNickname = findViewById(R.id.user_nickname);
                                TextView userIdText = findViewById(R.id.user_id);

                                userNickname.setText(loadedUser.getUserName());
                                userIdText.setText("#" + userId);

                                userExist = true;
                                isLogin = true;
                            }
                        });
                    } else {
                        userExist = true;
                        isLogin = false;
                    }
                }
            }
        });

    }

    public void customOnClick(View v) {
        if (userExist) {
            navigateToBadgeActivity(MyPageActivity.this);
        }
    }

    public void InquiryOnClick(View v) {
        navigateToInquiryActivity(MyPageActivity.this);
    }

    public void PrivacyOnClick(View v) {
        Log.d("st", String.valueOf(isLogin) + String.valueOf(userExist));


        if (userExist) {
            if (!isLogin) {
                navigateToLoginActivity(MyPageActivity.this, userId);
            } else {
                navigateToPrivacyActivity(MyPageActivity.this, null);
            }
        } else {
            navigateToPrivacyActivity(MyPageActivity.this, null);
        }
    }

    public void settingOnClick(View v) {
        navigateToAppSettingActivity(MyPageActivity.this);
    }

    public void changePWOnClick(View v) {
        navigateToChangePasswordActivity(MyPageActivity.this);
    }
}
