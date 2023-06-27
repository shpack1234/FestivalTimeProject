package com.festivaltime.festivaltimeproject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

public class CalendarPopupActivity extends Dialog {
    private Button shutdownClick, startdateClick, starttimeClick, enddateClick, endtimeClick;
    public DatePicker StartDatePicker;
    public TimePicker StartTimePicker;

    public CalendarPopupActivity(@NonNull Context context, String contents) {
        super(context);
        setContentView(R.layout.activity_calendar_popup);

        shutdownClick = findViewById(R.id.close_btn);
        startdateClick = findViewById(R.id.start_date);
        starttimeClick = findViewById(R.id.start_time);
        StartDatePicker = findViewById(R.id.StartDatePicker);
        StartTimePicker = findViewById(R.id.StartTimePicker);
        enddateClick = findViewById(R.id.end_date);
        endtimeClick = findViewById(R.id.end_time);

        //사용자 선택한 날짜로 초기화
        startdateClick.setText(contents);
        enddateClick.setText(contents);

        //팝업창 나가기 모션
        shutdownClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //시작 시간-날짜 클릭시 표시 (숨기기는 미완)
        startdateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartDatePicker.setVisibility(View.VISIBLE);
            }
        });

        //시작 시간-날짜 변화시
        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });

        //시작 시간-시간 클릭
        starttimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTimePicker.setVisibility(View.VISIBLE);
            }
        });

        //시작 시간-시간 변화시
        StartTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                starttimeClick.setText(String.format("%d:%d", hourOfDay, minute));
            }
        });
    }

}
