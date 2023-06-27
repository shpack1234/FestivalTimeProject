package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
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

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
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

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Main_Year_textView.setText(String.format("%d", year));
                add_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                SelectDateView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                SelectDateView.setText(String.format("%d.%d.%d",year,month,dayOfMonth));
            }
        });
        add_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Popup_btn = new CalendarPopupActivity(CalendarActivity.this, SelectDateView.getText().toString());
                Popup_btn.show();
            }
        });

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
