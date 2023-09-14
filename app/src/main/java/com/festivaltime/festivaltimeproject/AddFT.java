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

    public AddFT(@NonNull Context context, String title, String contentID, String startdate, String enddate) {
        super(context);
        setContentView(R.layout.activity_addft_pop);

        shutdownClick = findViewById(R.id.addcalendar_close);
        addBtn = findViewById(R.id.addcalendar_add);
        startdateClick = findViewById(R.id.addcalendar_start_date);
        StartDatePicker = findViewById(R.id.addcalendar_StartDatePicker);
        enddateClick = findViewById(R.id.addcalendar_end_date);
        EndDatePicker = findViewById(R.id.addcalendar_EndDatePicker);

        startdateClick.setText(startdate);
        enddateClick.setText(enddate);

        // 시작 날짜와 종료 날짜의 최소 및 최대 날짜 설정
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.M.d", Locale.getDefault()); // 날짜 형식 변경
        Date minDate, maxDate;

        try {
            minDate = sdf.parse(startdate);
            maxDate = sdf.parse(enddate);

            // 시작 날짜로부터 종료 날짜까지의 범위 설정
            StartDatePicker.setMinDate(minDate.getTime());
            StartDatePicker.setMaxDate(maxDate.getTime());

            // 종료 날짜로부터 시작 날짜까지의 범위 설정
            EndDatePicker.setMinDate(minDate.getTime());
            EndDatePicker.setMaxDate(maxDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                /*String startDate = startdateClick.getText().toString();
                String endDate = enddateClick.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                try {
                    Date originalStartDate = sdf2.parse(startDate);
                    Date originalEndDate = sdf2.parse(endDate);

                    // 일수 계산 위해 밀리초로 날짜 변환
                    long startDateMillis = originalStartDate.getTime();
                    long endDateMillis = originalEndDate.getTime();

                    // 두 날짜 사이의 일 수 계산
                    long daysBetween = (endDateMillis - startDateMillis) / (1000 * 60 * 60 * 24);

                    // 14일(2주) 이상인 경우 날짜 제한
                    if (daysBetween >= 14) {
                        Toast.makeText(getContext(), "일정은 2주까지 설정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        try {
                            Date fixedstartdate = sdf.parse(startDate);
                            Date fixedenddate = sdf.parse(endDate);

                            if (fixedenddate.after(fixedstartdate) || fixedenddate.equals(fixedstartdate)) {
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

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }*/
                dismiss();
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
