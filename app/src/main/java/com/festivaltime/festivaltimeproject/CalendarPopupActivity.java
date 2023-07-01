package com.festivaltime.festivaltimeproject;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

public class CalendarPopupActivity extends Dialog {
    public EditText TitleText;
    private Button shutdownClick, addBtn, startdateClick, starttimeClick, enddateClick, endtimeClick;
    public DatePicker StartDatePicker, EndDatePicker;
    public TimePicker StartTimePicker, EndTimePicker;
    public Switch alldaySwitch;

    public CalendarPopupActivity(@NonNull Context context, String contents) {
        super(context);
        setContentView(R.layout.activity_calendar_popup);

        TitleText = findViewById(R.id.u_title_Text);
        shutdownClick = findViewById(R.id.close_btn);
        addBtn = findViewById(R.id.add_finish_btn);
        startdateClick = findViewById(R.id.start_date);
        starttimeClick = findViewById(R.id.start_time);
        StartDatePicker = findViewById(R.id.StartDatePicker);
        StartTimePicker = findViewById(R.id.StartTimePicker);
        enddateClick = findViewById(R.id.end_date);
        endtimeClick = findViewById(R.id.end_time);
        EndDatePicker = findViewById(R.id.EndDatePicker);
        EndTimePicker = findViewById(R.id.EndTimePicker);
        alldaySwitch = findViewById(R.id.switchView);

        //사용자 선택한 날짜로 초기화 (미선택시 현재 날짜로 초기화)
        startdateClick.setText(contents);
        enddateClick.setText(contents);

        //팝업창 나가기 모션
        shutdownClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", TitleText.getText());
                dismiss();
            }
        });

        //하루종일 스위치 on off시 시간 표시 on off
        alldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    starttimeClick.setVisibility(View.GONE);
                    endtimeClick.setVisibility(View.GONE);
                }
                else{
                    starttimeClick.setVisibility(View.VISIBLE);
                    endtimeClick.setVisibility(View.VISIBLE);
                }
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.start_date: //시작 시간-날짜 클릭시 표시
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (StartDatePicker.getVisibility() == View.VISIBLE) {
                            StartDatePicker.setVisibility(View.GONE);}
                        else {StartDatePicker.setVisibility(View.VISIBLE);}
                        break;

                    case R.id.start_time: //시작 시간-시간 클릭
                        StartDatePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (StartTimePicker.getVisibility() == View.VISIBLE) {
                            StartTimePicker.setVisibility(View.GONE);}
                        else {StartTimePicker.setVisibility(View.VISIBLE);}
                        break;

                    case R.id.end_date: //end 시간-날짜 클릭시 표시
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (EndDatePicker.getVisibility() == View.VISIBLE) {
                            EndDatePicker.setVisibility(View.GONE);}
                        else {EndDatePicker.setVisibility(View.VISIBLE);}
                        break;

                    case R.id.end_time: //end 시간-시간 클릭
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        if (EndTimePicker.getVisibility() == View.VISIBLE) {
                            EndTimePicker.setVisibility(View.GONE);}
                        else {EndTimePicker.setVisibility(View.VISIBLE);}
                        break;

                    default: //기타 버튼 선택시 감춤
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                }
            }
        };
        startdateClick.setOnClickListener(onClickListener);
        starttimeClick.setOnClickListener(onClickListener);
        enddateClick.setOnClickListener(onClickListener);
        endtimeClick.setOnClickListener(onClickListener);

        //시작 시간-날짜 클릭시 표시 (숨기기는 미완)
        /*startdateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StartDatePicker.getVisibility() == View.GONE) {
                    StartDatePicker.setVisibility(View.VISIBLE);
                } else {
                    StartDatePicker.setVisibility(View.GONE);
                }
            }
        });
        */

        //시작 시간-날짜 변화시
        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });

        /*
        //시작 시간-시간 클릭
        starttimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StartTimePicker.getVisibility() == View.GONE) {
                    StartTimePicker.setVisibility(View.VISIBLE);
                } else {
                    StartTimePicker.setVisibility(View.GONE);
                }
            }
        });*/


        //시작 시간-시간 변화시
        StartTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    starttimeClick.setText(String.format("0%d:0%d", hourOfDay, minute));
                } else if (minute < 10) {
                    starttimeClick.setText(String.format("%d:0%d", hourOfDay, minute));
                } else if (hourOfDay < 10) {
                    starttimeClick.setText(String.format("0%d:%d", hourOfDay, minute));
                } else {
                    starttimeClick.setText(String.format("%d:%d", hourOfDay, minute));
                }
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

        //end 시간-시간 변화시
        EndTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    endtimeClick.setText(String.format("0%d:0%d", hourOfDay, minute));
                } else if (minute < 10) {
                    endtimeClick.setText(String.format("%d:0%d", hourOfDay, minute));
                } else if (hourOfDay < 10) {
                    endtimeClick.setText(String.format("0%d:%d", hourOfDay, minute));
                } else {
                    endtimeClick.setText(String.format("%d:%d", hourOfDay, minute));
                }
            }
        });
    }

}
