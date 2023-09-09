package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChangePasswordActivity extends AppCompatActivity {

    public ImageButton Back_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Back_Btn=findViewById(R.id.before_btn);

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   onBackPressed(); }
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