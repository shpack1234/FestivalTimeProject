package com.festivaltime.festivaltimeproject.calendaract;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.FetchScheduleTask;
import com.festivaltime.festivaltimeproject.festivalcalendaract.FestivalCalendarActivity;
import com.festivaltime.festivaltimeproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;

//개인 캘린더 총괄 class
public class CalendarActivity extends AppCompatActivity {
    private boolean showOtherMonths=true; // 다른 달의 일자를 표시할지 여부를 저장 변수
    //현재 시간 가져오기 now, date, sdf
    public long now = System.currentTimeMillis();
    public Date date = new Date(now);
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    public SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
    private CalendarPopupActivity Popup_btn; //popup창에서 startdate, enddate default 설정위한 클래스 변수 지정
    public Button cal_setting, add_Btn, festi_cal;
    public TextView SelectDateView, Year_text, monthText, scheduleText;
    public RecyclerView calendarrecycler; //캘린더 recyclerview, 일정 담는 recyclerview
    private ArrayList<CalendarSchedule> calendarScheduleArrayList; //일정 담는 recyclerview
    private CalendarScheduleAdapter calendarScheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Year_text = findViewById(R.id.calendar_yeartext); //캘린더 상단에 해당 년도 표시하는 text
        monthText = findViewById(R.id.calendar_monthText); //calendar 설정한 month 표시하는 text
        calendarrecycler = findViewById(R.id.calendar_recyclerView); //calendar recyclerview

        scheduleText = findViewById(R.id.calendar_scheduleText); //일정 recyclerview
        SelectDateView = findViewById(R.id.calendar_SelectDateView); //일정 위 선택한 날짜 표시 text

        ImageButton prevBtn = findViewById(R.id.calendar_pre_btn); //calendar 이전 달 이동 btn
        ImageButton nextBtn = findViewById(R.id.calendar_next_btn); //calendar 다음 달 이동 btn
        cal_setting = findViewById(R.id.calendar_cal_setting); //calendar 설정 dialog 띄우는 btn
        add_Btn = findViewById(R.id.calendar_add_Btn); //calendar 일정 추가 dialog 띄우는 btn
        festi_cal = findViewById(R.id.calendar_festical_btn); //festival calendar 이동 btn

        //상단바 year 현재시간으로 출력, 선택 날짜 현재시간으로 초기화
        Year_text.setText(sdf.format(date));
        SelectDateView.setText(sdf2.format(date));

        //현재 날짜 set
        CalendarUtil.selectedDate = Calendar.getInstance();

        //화면 설정
        setMonthView();
        //setScheduleView();
        setScheduleView();

        //이전 달 버튼
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CalendarUtil DATE month 1달 이전으로 설정
                CalendarUtil.selectedDate.add(Calendar.MONTH, -1);
                //달 변경시 이전에 선택했던 일정view INVISIBLE 설정
                SelectDateView.setVisibility(View.INVISIBLE);
                setMonthView();
            }
        });

        //다음 달 버튼
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CalendarUtil DATE month 1달 이후로 설정
                CalendarUtil.selectedDate.add(Calendar.MONTH, 1);
                //달 변경시 이전에 선택했던 일정view INVISIBLE 설정
                SelectDateView.setVisibility(View.INVISIBLE);
                setMonthView();
            }
        });

        //festival cal로 이동
        festi_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 activity에서 FestivalCalendarActivity로 이동
                Intent intent = new Intent(CalendarActivity.this, FestivalCalendarActivity.class);
                startActivity(intent);
            }
        });

        //캘린더 설정 (설정한 카테고리만 비추기, 카테고리 추가)
        cal_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CalendarSetting dialog 띄움, 다른달 표시 변수 전송
                CalendarSetting dialog = new CalendarSetting(CalendarActivity.this, showOtherMonths);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { //dismiss시 실행
                    //오류발견: 완료버튼이 아닌 다른화면 선택시 자동 dismiss되어 설정 추가되는 현상> 추후 수정 예정
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        CalendarSetting settingDialog = (CalendarSetting) dialog;
                        showOtherMonths = settingDialog.getShowOtherMonths(); // othermonth_switch.isChecked() 값을 가져오고 재설정
                        SelectDateView.setVisibility(View.INVISIBLE); //값 변경시 이전에 선택했던 일정view INVISIBLE 설정
                        setMonthView(); //재설정된 캘린더 view
                    }
                });
                dialog.show();
            }
        });


        // 날짜 추가하기 버튼 클릭시 popup창 연결
        add_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Popup_btn = new CalendarPopupActivity(CalendarActivity.this);
                // 선택한 날짜 popup 전송 후 startdate, enddate default 값으로 설정
                Popup_btn.startdateClick.setText(SelectDateView.getText().toString());
                Popup_btn.enddateClick.setText(SelectDateView.getText().toString());

                Popup_btn.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        CalendarPopupActivity popupDialog = (CalendarPopupActivity) dialog;
                    }
                });
                Popup_btn.show();
            }
        });


        //bottomNavi
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_calendar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(CalendarActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(CalendarActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_calendar) {
                return false;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(CalendarActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(CalendarActivity.this);
                return true;
            }
        });
    }

    //calendar 화면 설정
    private void setMonthView() {
        //선택되어있는 달 저장
        int month = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
        //해당 달 월>영어 텍스트뷰
        monthText.setText(Month_eng(month));

        //date recyclerview 설정
        ArrayList<Date> dayList = daysInMonthArray();

        //calendar 어뎁터 사용 위한 정의
        CalendarAdapter adapter = new CalendarAdapter(dayList, showOtherMonths, calendarrecycler, SelectDateView, scheduleText);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7); //recyclerview layout 설정
        calendarrecycler.setLayoutManager(manager);
        calendarrecycler.setAdapter(adapter);
    }

    private void setScheduleView() {
        // 일정 데이터베이스에서 데이터 가져오기
        CalendarDatabase calendarDatabase = CalendarDatabase.getInstance(this);
        CalendarDao calendarDao = calendarDatabase.calendarDao();
        FetchScheduleTask fetchScheduleTask = new FetchScheduleTask(this, scheduleText, calendarDao);
        fetchScheduleTask.fetchSchedules();
    }


    //schedule 화면 설정 재수정중
    /*private void setScheduleView(){
        //일정 recyclerview 설정
        calendarScheduleArrayList = new ArrayList<>();
        calendarScheduleAdapter = new CalendarScheduleAdapter(calendarScheduleArrayList);
        //schedule recylerview LinearLayoutManager 객체 지정
        scheduleText.setLayoutManager(new LinearLayoutManager(this));
        scheduleText.setAdapter(calendarScheduleAdapter);
    }*/


    //날짜 생성
    private ArrayList<Date> daysInMonthArray() {
        ArrayList<Date> dayList = new ArrayList<>();
        //날짜 복사 후 변수 생성
        Calendar monthCalendar = (Calendar) CalendarUtil.selectedDate.clone();
        //1일로 set
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        while (dayList.size()<42) { //42칸 채우도록 날짜 추가
            dayList.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dayList;
    }


    //월_ENG 출력
    public String Month_eng(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
            default:
                return "";
        }
    }
}