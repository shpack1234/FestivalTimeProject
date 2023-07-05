package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToBadgeActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToPrivacyActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.lifecycle.Observer;

import kotlinx.coroutines.Dispatchers;

public class MyPageActivity extends AppCompatActivity {

    private UserDataBase db;
    private UserDao userDao;
    private String userId;

    private UserEntity loadedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

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

        db = UserDataBaseSingleton.getInstance(getApplicationContext());

        userDao = db.userDao();

        String exampleID="123456";

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(exampleID);

                if (loadedUser!=null) {
                    userId = loadedUser.getUserId();
                } else {
                    userId = null;
                }

                // UI 업데이트 등 필요한 작업 수행
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // UI 업데이트 등의 코드 작성
                    }
                });
            }
        });

    }
    public void customOnClick(View v) {
        navigateToBadgeActivity(MyPageActivity.this);
    }

    public void PrivacyOnClick(View v) {
        navigateToPrivacyActivity(MyPageActivity.this, userId);
    }
}
