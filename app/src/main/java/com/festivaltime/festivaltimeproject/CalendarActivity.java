package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;

public class CalendarActivity extends AppCompatActivity {
    //현재 시간 가져오기 now, date, sdf
    public long now = System.currentTimeMillis();
    public Date date = new Date(now);
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    public SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
    private CalendarPopupActivity Popup_btn;
    private CalendarCategory Add_category;
    public String readDay = null;
    public Button cal_setting, add_Btn, del_Btn;
    public TextView SelectDateView, textView, Year_text, monthYearText;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Year_text = findViewById(R.id.yeartext);
        monthYearText = findViewById(R.id.monthYearText);
        ImageButton prevBtn = findViewById(R.id.pre_btn);
        ImageButton nextBtn = findViewById(R.id.next_btn);
        recyclerView = findViewById(R.id.recyclerView);

        SelectDateView = findViewById(R.id.SelectDateView);
        textView = findViewById(R.id.textView);
        cal_setting = findViewById(R.id.cal_setting);
        add_Btn = findViewById(R.id.add_Btn);
        del_Btn = findViewById(R.id.del_Btn);

        //상단바 year 현재시간으로 출력, 선택 날짜 현재시간으로 초기화
        Year_text.setText(sdf.format(date));
        SelectDateView.setText(sdf2.format(date));

        //현재 날짜
        CalendarUtil.selectedDate = Calendar.getInstance();

        //화면 설정
        setMonthView();

        //캘린더 설정 (설정한 카테고리만 비추기, 카테고리 추가)
        cal_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_category = new CalendarCategory(CalendarActivity.this);
                Add_category.show();
            }
        });

        //이전 달 버튼
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtil.selectedDate.add(Calendar.MONTH, -1);
                setMonthView();
            }
        });

        //다음 달 버튼
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtil.selectedDate.add(Calendar.MONTH, 1);
                setMonthView();
            }
        });

        //날짜 추가하기 버튼 클릭시 popup창 연결
        add_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Popup_btn = new CalendarPopupActivity(CalendarActivity.this, SelectDateView.getText().toString());
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

    //화면 설정
    private void setMonthView() {
        int month = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
        //월>영어 텍스트뷰
        monthYearText.setText(Month_eng(month));

        //date recyclerview 설정
        ArrayList<Date> dayList = daysInMonthArray();

        CalendarAdapter adapter = new CalendarAdapter(dayList);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    //날짜 생성
    private ArrayList<Date> daysInMonthArray() {
        ArrayList<Date> dayList = new ArrayList<>();
        //날짜 복사 후 변수 생성
        Calendar monthCalendar = (Calendar) CalendarUtil.selectedDate.clone();
        //1일로 set
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        while (dayList.size()<42) {
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
