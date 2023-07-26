package com.festivaltime.festivaltimeproject.calendaract;


import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;

//캘린더 일정 추가 dialog class
public class CalendarPopupActivity extends Dialog {
    protected Context mContext;
    public EditText TitleText;
    final Button shutdownClick, addBtn, startdateClick, starttimeClick, enddateClick, endtimeClick;
    public DatePicker StartDatePicker, EndDatePicker;
    public TimePicker StartTimePicker, EndTimePicker;
    public Switch alldaySwitch;

    // CalendarDatabase에 대한 참조 추가
    private CalendarDatabase calendarDatabase;

    public CalendarPopupActivity(@NonNull Context context) {
        super(context);
        //팝업 애니메이션 위한 윈도우
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_calendar_popup);
        this.mContext = context;


        TitleText = findViewById(R.id.calendar_popup_title_Text);
        shutdownClick = findViewById(R.id.calendar_popup_close_btn);
        addBtn = findViewById(R.id.calendar_popup_add_finish_btn);
        startdateClick = findViewById(R.id.calendar_popup_start_date);
        starttimeClick = findViewById(R.id.calendar_popup_start_time);
        StartDatePicker = findViewById(R.id.calendar_popup_StartDatePicker);
        StartTimePicker = findViewById(R.id.calendar_popup_StartTimePicker);
        enddateClick = findViewById(R.id.calendar_popup_end_date);
        endtimeClick = findViewById(R.id.calendar_popup_end_time);
        EndDatePicker = findViewById(R.id.calendar_popup_EndDatePicker);
        EndTimePicker = findViewById(R.id.calendar_popup_EndTimePicker);
        alldaySwitch = findViewById(R.id.calendar_popup_switchView);

        // CalendarDatabase 인스턴스 초기화
        calendarDatabase = CalendarDatabase.getInstance(context);

        //뒤로가기 true
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window window = getWindow();

        if(window!=null){
            //팝업 배경 투명설정 별로같아서 주석처리함)
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
            params.width         = WindowManager.LayoutParams.MATCH_PARENT;
            params.height        = WindowManager.LayoutParams.WRAP_CONTENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes( params );
            // UI 하단 정렬
            window.setGravity( Gravity.BOTTOM );
        }

        //사용자 선택한 날짜로 초기화 (미선택시 현재 날짜로 초기화)


        //팝업창 나가기 모션
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
                String title = TitleText.getText().toString();
                String startDate = startdateClick.getText().toString();
                String startTime = starttimeClick.getText().toString();
                String endDate = enddateClick.getText().toString();
                String endTime = endtimeClick.getText().toString();

                if (title.isEmpty()) {
                    // 제목이 입력되지 않았을 때 토스트 메시지를 표시합니다.
                    Toast.makeText(mContext, "Title을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 시작 날짜와 시간을 결합합니다.
                    String startDateTime = startDate + " " + startTime;
                    // 종료 날짜와 시간을 결합합니다.
                    String endDateTime = endDate + " " + endTime;

                    // 사용자 입력으로 새로운 CalendarEntity 객체를 생성합니다.
                    CalendarEntity newSchedule = new CalendarEntity();
                    newSchedule.title = title;
                    newSchedule.startDate = startDateTime;
                    newSchedule.endDate = endDateTime;

                    // ScheduleLoader를 사용하여 새 일정을 데이터베이스에 추가합니다.
                    ScheduleLoader loader = new ScheduleLoader(getContext(), newSchedule, calendarDatabase.calendarDao());
                    loader.forceLoad();

                    // 다이얼로그를 닫습니다.
                    dismiss();
                }
            }
        });


        //하루종일 스위치 on off시 시간(time) 표시 on off
        alldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //date > time 위치 이동은 미구현
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
                    case R.id.calendar_popup_start_date: //시작 시간-날짜 클릭시 표시
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (StartDatePicker.getVisibility() == View.VISIBLE) {
                            StartDatePicker.setVisibility(View.GONE);}
                        else {StartDatePicker.setVisibility(View.VISIBLE);}
                        break;

                    case R.id.calendar_popup_start_time: //시작 시간-시간 클릭
                        StartDatePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (StartTimePicker.getVisibility() == View.VISIBLE) {
                            StartTimePicker.setVisibility(View.GONE);}
                        else {StartTimePicker.setVisibility(View.VISIBLE);}
                        break;

                    case R.id.calendar_popup_end_date: //end 시간-날짜 클릭시 표시
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (EndDatePicker.getVisibility() == View.VISIBLE) {
                            EndDatePicker.setVisibility(View.GONE);}
                        else {EndDatePicker.setVisibility(View.VISIBLE);}
                        break;

                    case R.id.calendar_popup_end_time: //end 시간-시간 클릭
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

        //시작 시간-날짜 변화시
        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });


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