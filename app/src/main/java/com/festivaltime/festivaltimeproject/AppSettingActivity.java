package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppSettingActivity extends AppCompatActivity {

    public ImageButton Back_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);

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

        Switch push = findViewById(R.id.app_push_alarm);

        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String NOTIFICATION_CHANNEL_ID = "my_channel_id";
                    int NOTIFICATION_ID = 1234;


                    Intent notificationIntent = new Intent(AppSettingActivity.this, AppSettingActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            AppSettingActivity.this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(AppSettingActivity.this, NOTIFICATION_CHANNEL_ID)
                                    .setContentTitle(" 푸쉬 알림 테스트 ")
                                    .setContentText(" 우왕 알림이 떴나용? ")
                                    .setSmallIcon(R.drawable.ic_favorite)
                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        CharSequence channelName = "노티피케이션 채널";
                        String description = "해당 채널에 대한 설명";
                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        NotificationChannel channel = new NotificationChannel(
                                NOTIFICATION_CHANNEL_ID,
                                channelName,
                                importance
                        );
                        channel.setDescription(description);

                        NotificationManager customNotificationManager = getSystemService(NotificationManager.class);
                        customNotificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);  //하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(AppSettingActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(AppSettingActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(AppSettingActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(AppSettingActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(AppSettingActivity.this);
                return true;
            }
        });
    }
}