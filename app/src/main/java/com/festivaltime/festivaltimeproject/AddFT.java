package com.festivaltime.festivaltimeproject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.calendaract.CalendarPopupActivity;
import com.festivaltime.festivaltimeproject.calendaract.ScheduleLoader;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddFT extends Dialog {
    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    final Button shutdownClick, addBtn, startdateClick, enddateClick;
    public DatePicker StartDatePicker, EndDatePicker;

    public AddFT(@NonNull Context context, String title, String contentID) {
        super(context);
        setContentView(R.layout.activity_addft_pop);

        shutdownClick = findViewById(R.id.addcalendar_close);
        addBtn = findViewById(R.id.addcalendar_add);
        startdateClick = findViewById(R.id.addcalendar_start_date);
        StartDatePicker = findViewById(R.id.addcalendar_StartDatePicker);
        enddateClick = findViewById(R.id.addcalendar_end_date);
        EndDatePicker = findViewById(R.id.addcalendar_EndDatePicker);

        shutdownClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //일정 추가 버튼
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = startdateClick.getText().toString();
                String endDate = enddateClick.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

                try {
                    Date startdate = sdf.parse(startDate);
                    Date enddate = sdf.parse(endDate);

                    if (enddate.after(startdate) || enddate.equals(startdate)) {
                        // 종료 날짜-시간이 시작 날짜-시간보다 나중일 경우
                        calendarDatabase = CalendarDatabase.getInstance(context);
                        calendarDao = calendarDatabase.calendarDao();

                        // CalendarEntity 생성
                        CalendarEntity event = new CalendarEntity();
                        event.title = title;
                        event.startDate = startdateClick.getText().toString();
                        event.endDate = enddateClick.getText().toString();
                        event.startTime = "";
                        event.endTime = "";
                        event.category = "#ed5c55";
                        event.contentid = contentID;

                        // CalendarEntityDao를 사용하여 데이터베이스에 이벤트 추가
                        calendarDao.InsertSchedule(event);
                        dismiss();

                    } else {
                        Toast.makeText(context, "기간을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.addcalendar_start_date:
                        EndDatePicker.setVisibility(View.GONE);
                        if (StartDatePicker.getVisibility() == View.VISIBLE) {
                            StartDatePicker.setVisibility(View.GONE);
                        } else {
                            StartDatePicker.setVisibility(View.VISIBLE);
                        }
                        break;

                    case R.id.addcalendar_end_date: //end 시간-날짜 클릭시 표시
                        StartDatePicker.setVisibility(View.GONE);
                        if (EndDatePicker.getVisibility() == View.VISIBLE) {
                            EndDatePicker.setVisibility(View.GONE);
                        } else {
                            EndDatePicker.setVisibility(View.VISIBLE);
                        }
                        break;

                    default: //기타 버튼 선택시 감춤
                        StartDatePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        break;
                }
            }
        };
        startdateClick.setOnClickListener(onClickListener);
        enddateClick.setOnClickListener(onClickListener);

        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });

        //end 시간-날짜 변화시
        EndDatePicker.init(EndDatePicker.getYear(), EndDatePicker.getMonth(), EndDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        enddateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });
    }
}
