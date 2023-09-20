package com.festivaltime.festivaltimeproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

public class SearchSetting {
    private SearchView searchView;
    private String query, detailInfo;
    public Date date;
    public SimpleDateFormat sdf;
    private ApiReader apiReader;
    private Executor executor;

    private final int numberOfLayouts = 3;

    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    private UserEntity loadedUser;
    private String userId;
    private UserDataBase db;
    private UserDao userDao;

    private String locationSelect;
    private String formattedStartDate;
    private String formattedEndDate;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
    }
}
