package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    //현재 시간 가져오기 now, date, sdf
    public long now = System.currentTimeMillis();
    public Date date = new Date(now);
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    public SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
    private CalendarPopupActivity Popup_btn;
    public String readDay = null;
    public CalendarView calendarView;
    public Button add_Btn, del_Btn;
    public TextView Main_Year_textView, SelectDateView, textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        Main_Year_textView = findViewById(R.id.Main_Year_textView);
        SelectDateView = findViewById(R.id.SelectDateView);
        textView = findViewById(R.id.textView);
        add_Btn = findViewById(R.id.add_Btn);
        del_Btn = findViewById(R.id.del_Btn);

        //상단바 year 현재시간으로 출력, 선택 날짜 현재시간으로 초기화
        Main_Year_textView.setText(sdf.format(date));
        SelectDateView.setText(sdf2.format(date));

        //캘린더 날짜 선택시 실행
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //해당 날짜 내용 visible
                add_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                SelectDateView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                SelectDateView.setText(String.format("%d.%d.%d",year,month,dayOfMonth));
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
}
