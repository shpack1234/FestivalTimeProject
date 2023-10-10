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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppSettingActivity extends AppCompatActivity {

    public ImageButton Back_Btn;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PUSH_ALARM_KEY = "pushAlarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);

        getSupportActionBar().hide();
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

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean pushAlarmEnabled = settings.getBoolean(PUSH_ALARM_KEY, false);
        push.setChecked(pushAlarmEnabled);

        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(PUSH_ALARM_KEY, isChecked);
                editor.apply();

                if (isChecked) {
                    // 백그라운드 스레드에서 데이터베이스 작업 수행
                    new AsyncTask<Void, Void, List<CalendarEntity>>() {
                        @Override
                        protected List<CalendarEntity> doInBackground(Void... voids) {
                            CalendarDao calendarDao = CalendarDatabase.getInstance(getApplicationContext()).calendarDao();
                            return calendarDao.getAllCalendarEntity();
                        }

                        @Override
                        protected void onPostExecute(List<CalendarEntity> events) {
                            super.onPostExecute(events);

                            // 현재 날짜와 시간
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());

                            // 매일 오전 8시로 시간을 설정
                            calendar.set(Calendar.HOUR_OF_DAY, 12);
                            calendar.set(Calendar.MINUTE, 10);
                            calendar.set(Calendar.SECOND, 0);

                            long alarmTimeMillis = calendar.getTimeInMillis();

                            // 이벤트 날짜와 오늘 날짜를 비교하여 알림을 설정
                            for (CalendarEntity event : events) {
                                String eventDate = event.startDate; // 이벤트의 날짜 (date) 정보
                                if (isEventToday(eventDate, alarmTimeMillis)) {
                                    String title = event.getTitle();
                                    String message = "오늘 이벤트: " + eventDate;

                                    // 캘린더 알림 표시
                                    showNotification(title, message);
                                }
                            }
                        }
                    }.execute();
                } else {
                    // 스위치가 꺼진 경우 알림 취소
                    cancelNotification();
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

    private boolean isEventToday(String eventDate, long alarmTimeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(eventDate);
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.setTime(date);

            return eventCalendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                    eventCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                    eventCalendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                    alarmTimeMillis > System.currentTimeMillis(); // 알람 시간이 현재 시간 이후인 경우에만 설정합니다.
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void showNotification(String title, String message) {
        String NOTIFICATION_CHANNEL_ID = "my_channel_id";
        int NOTIFICATION_ID = 1234;

        Intent notificationIntent = new Intent(this, AppSettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_favorite)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "FestivalTime";
            String description = "오늘의 알람";
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

    private void cancelNotification() {
        // 알림 취소 코드 추가
        int NOTIFICATION_ID = 1234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}