package com.festivaltime.festivaltimeproject.festivalcalendaract;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.festivaltime.festivaltimeproject.calendaract.CalendarUtil;
import com.festivaltime.festivaltimeproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//Festival calendar class 날짜별 축제 개수 비춰짐 (api)
public class FestivalCalendarActivity extends AppCompatActivity {
    public long now = System.currentTimeMillis();
    public Date date = new Date(now);
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    TextView monthText, Year_text;
    Button move_cal_btn;
    RecyclerView calendarrecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festivalcalendar);

        Year_text = findViewById(R.id.festivalcalendar_yeartext);
        monthText = findViewById(R.id.festivalcalendar_monthText);
        ImageButton preBtn = findViewById(R.id.festivalcalendar_pre_btn);
        ImageButton nextBtn = findViewById(R.id.festivalcalendar_next_btn);
        calendarrecycler = findViewById(R.id.festivalcalendar_recyclerView);
        move_cal_btn = findViewById(R.id.festivalcalendar_nomal_cal_btn);

        //상단바 year 현재시간으로 출력, 선택 날짜 현재시간으로 초기화
        Year_text.setText(sdf.format(date));

        CalendarUtil.selectedDate = Calendar.getInstance();

        setMonthView();

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtil.selectedDate.add(Calendar.MONTH, -1);
                setMonthView();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtil.selectedDate.add(Calendar.MONTH, 1);
                setMonthView();
            }
        });

        move_cal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //bottomNavi
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_calendar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(FestivalCalendarActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(FestivalCalendarActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(FestivalCalendarActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(FestivalCalendarActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(FestivalCalendarActivity.this);
                return true;
            }
        });
    }


    private void setMonthView(){
        int month = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
        //년월 텍스트뷰
        monthText.setText(Month_eng(month));

        ArrayList<Date> dayList = daysInMonthArray();
        FestivalCalendarAdapter adapter = new FestivalCalendarAdapter(dayList);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        calendarrecycler.setLayoutManager(manager);
        calendarrecycler.setAdapter(adapter);
    }

    private ArrayList<Date> daysInMonthArray(){
        ArrayList<Date> dayList = new ArrayList<>();
        Calendar monthCalendar = (Calendar) CalendarUtil.selectedDate.clone();

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        while(dayList.size()<42){
            dayList.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dayList;
    }

    //월_ENG 출력
    private String Month_eng(int month) {
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