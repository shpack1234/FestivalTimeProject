package com.festivaltime.festivaltimeproject.calendaract;

import static com.festivaltime.festivaltimeproject.calendaract.CalendarUtil.selectedDate;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.FetchScheduleTask;
import com.festivaltime.festivaltimeproject.festivalcalendaract.FestivalCalendarActivity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//개인 캘린더 총괄 class
public class CalendarActivity extends AppCompatActivity implements FetchScheduleTask.FetchScheduleTaskListener, CalendarAdapter.OnDateClickListener {
    private CalendarDao calendarDao;
    private boolean showOtherMonths = true; // 다른 달의 일자를 표시할지 여부를 저장 변수
    public boolean showft = true, showholi = true;
    //현재 시간 가져오기 now, date, sdf
    public long now = System.currentTimeMillis();
    public Date date = new Date(now);
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    public SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
    private CalendarPopupActivity Popup_btn; //popup창에서 startdate, enddate default 설정위한 클래스 변수 지정
    public Button cal_setting, add_Btn, festi_cal;
    public ScrollView schedules;
    public TextView SelectDateView, Year_text, monthText;
    public RecyclerView calendarrecycler; //캘린더 recyclerview, 일정 담는 recyclerview
    private Executor executor;

    private UserDao userDao;

    private UserEntity loadedUser;
    private String userId;
    private UserDataBase db;
    boolean userExist = false;
    private FrameLayout calendarBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        loadSettings();

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        calendarBox = findViewById(R.id.calendar_box);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);
                //Log.d("st", String.valueOf(loadedUser.getIsLogin()) + String.valueOf(loadedUser != null));
                if (loadedUser != null) {
                    if (loadedUser.getIsLogin()) {
                        userId = loadedUser.getUserId();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View blurLayout = findViewById(R.id.blur_layout);
                                blurLayout.setVisibility(View.GONE);
                                for (int i = 0; i < calendarBox.getChildCount(); i++) {
                                    View child = calendarBox.getChildAt(i);
                                    child.setClickable(true);
                                }
                                userExist = true;
                            }
                        });
                    } else {
                        Log.d("count", String.valueOf(calendarBox.getChildCount()));
                        for (int i = 0; i < calendarBox.getChildCount(); i++) {
                            View child = calendarBox.getChildAt(i);
                            child.setClickable(false);
                        }
                    }
                } else {
                    for (int i = 0; i < calendarBox.getChildCount(); i++) {
                        View child = calendarBox.getChildAt(i);
                        child.setClickable(false);
                    }
                }
            }
        });

        Year_text = findViewById(R.id.calendar_yeartext); //캘린더 상단에 해당 년도 표시하는 text
        monthText = findViewById(R.id.calendar_monthText); //calendar 설정한 month 표시하는 text
        calendarrecycler = findViewById(R.id.calendar_recyclerView); //calendar recyclerview

        SelectDateView = findViewById(R.id.calendar_SelectDateView); //일정 위 선택한 날짜 표시 text
        schedules = findViewById(R.id.calendar_schedules);

        ImageButton prevBtn = findViewById(R.id.calendar_pre_btn); //calendar 이전 달 이동 btn
        ImageButton nextBtn = findViewById(R.id.calendar_next_btn); //calendar 다음 달 이동 btn
        cal_setting = findViewById(R.id.calendar_cal_setting); //calendar 설정 dialog 띄우는 btn
        add_Btn = findViewById(R.id.calendar_add_Btn); //calendar 일정 추가 dialog 띄우는 btn
        festi_cal = findViewById(R.id.calendar_festical_btn); //festival calendar 이동 btn

        executor = Executors.newSingleThreadExecutor();

        SelectDateView.setText(sdf2.format(date));
        Year_text.setText(sdf.format(date));

        //현재 날짜 set
        selectedDate = Calendar.getInstance();

        //화면 설정
        setMonthView();

        //이전 달 버튼
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CalendarUtil DATE month 1달 이전으로 설정
                selectedDate.add(Calendar.MONTH, -1);
                //달 변경시 이전에 선택했던 일정view INVISIBLE 설정
                SelectDateView.setVisibility(View.INVISIBLE);
                schedules.setVisibility(View.INVISIBLE);
                setMonthView();
            }
        });

        //다음 달 버튼
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CalendarUtil DATE month 1달 이후로 설정
                selectedDate.add(Calendar.MONTH, 1);
                //달 변경시 이전에 선택했던 일정view INVISIBLE 설정
                SelectDateView.setVisibility(View.INVISIBLE);
                schedules.setVisibility(View.INVISIBLE);
                setMonthView();
            }
        });

        //캘린더 설정 (설정한 카테고리만 비추기, 카테고리 추가)
        cal_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CalendarSetting dialog 띄움, 다른달 표시 변수 전송
                CalendarSetting dialog = new CalendarSetting(CalendarActivity.this, showOtherMonths, showft, showholi, CalendarActivity.this);
                // 팝업 창 배경을 투명으로 설정
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { //dismiss시 실행
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SelectDateView.setVisibility(View.INVISIBLE); //값 변경시 이전에 선택했던 일정view INVISIBLE 설정
                        schedules.setVisibility(View.INVISIBLE);
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
                        FetchScheduleTask fetchScheduleTask = new FetchScheduleTask(CalendarActivity.this, calendarDao);
                        fetchScheduleTask.fetchSchedules(new FetchScheduleTask.FetchScheduleTaskListener() {
                            @Override
                            public void onFetchCompleted(List<CalendarEntity> scheduleList) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateUI(scheduleList);
                                    }
                                });
                            }
                        });
                    }
                });
                Popup_btn.show();
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

    @Override
    public void onDateClick(Date selectedDate) {
        Log.d("CalendarActivity", "onCreate: Calendar activity created.");
        // 클릭된 날짜에 대한 일정 업데이트 처리
        updateUIForSelectedDate(selectedDate);
    }

    private void updateUIForSelectedDate(Date selectedDate) {
        // 일정 화면 업데이트
        FetchScheduleTask fetchScheduleTask = new FetchScheduleTask(CalendarActivity.this, calendarDao);
        fetchScheduleTask.fetchSchedules(new FetchScheduleTask.FetchScheduleTaskListener() {
            @Override
            public void onFetchCompleted(List<CalendarEntity> scheduleList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(scheduleList);
                    }
                });
            }
        });
    }


    // FetchScheduleTask에서 일정 데이터를 가져온 후, 캘린더 레이어에 업데이트하는 메서드
    @Override
    public void onFetchCompleted(List<CalendarEntity> scheduleList) {
        updateUI(scheduleList); // 매개변수를 전달하여 호출합니다.
    }

    public void setShowOtherMonths(boolean showOtherMonths) {
        this.showOtherMonths = showOtherMonths;
    }

    public void setShowft(boolean showft) {
        this.showft = showft;
    }

    public void setShowholi(boolean showholi) {
        this.showholi = showholi;
    }

    // 설정 로드 메서드
    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        showft = sharedPreferences.getBoolean("showft", true); // showft 설정값을 로드
        showholi = sharedPreferences.getBoolean("showholi", true); // showholi 설정값을 로드
        showOtherMonths = sharedPreferences.getBoolean("showOtherMonths", true);
    }

    //calendar 화면 설정
    void setMonthView() {
        //선택되어있는 달 저장
        int month = selectedDate.get(Calendar.MONTH) + 1;
        int year = selectedDate.get(Calendar.YEAR);
        //해당 달 월>영어 텍스트뷰
        monthText.setText(Month_eng(month));
        Year_text.setText(String.valueOf(year));

        // CalendarDao 인스턴스 생성 (생략)
        calendarDao = CalendarDatabase.getInstance(this).calendarDao();

        // 데이터베이스에서 일정 데이터를 가져와서 캘린더 레이어에 업데이트하는 작업을 시작합니다.
        FetchScheduleTask fetchScheduleTask = new FetchScheduleTask(this, calendarDao);
        fetchScheduleTask.fetchSchedules(this);

        //date recyclerview 설정
        ArrayList<Date> dayList = daysInMonthArray();

        //calendar 어뎁터 사용 위한 정의
        CalendarAdapter adapter = new CalendarAdapter(dayList, showOtherMonths, calendarrecycler, SelectDateView, schedules, showft, showholi);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7); //recyclerview layout 설정
        calendarrecycler.setLayoutManager(manager);
        adapter.setOnDateClickListener(this);
        calendarrecycler.setAdapter(adapter);
    }

    private boolean isDateInRange(Date selectedDate, Date startDate, Date endDate) {
        return (selectedDate.compareTo(startDate) >= 0) && (selectedDate.compareTo(endDate) <= 0);
    }

    // 화면을 업데이트하는 메서드
    private void updateUI(List<CalendarEntity> scheduleList) {
        //setMonthView();
        LinearLayout scheduleContainer = findViewById(R.id.schedule_container);
        scheduleContainer.removeAllViews();

        SimpleDateFormat select_sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        try {
            Date selectedDate = select_sdf.parse(SelectDateView.getText().toString());

            int scheduleCount = 0;

            for (CalendarEntity schedule : scheduleList) {
                Date startDate = select_sdf.parse(schedule.startDate);
                Date endDate = select_sdf.parse(schedule.endDate);
                //db확인용 log
                Log.d("CalendarDatabase: DateLog", "selectedDate: " + selectedDate);
                Log.d("CalendarDatabase: DateLog", "startDate: " + startDate);
                Log.d("CalendarDatabase: DateLog", "endDate: " + endDate);

                if (isDateInRange(selectedDate, startDate, endDate)) {
                    View scheduleBox = getLayoutInflater().inflate(R.layout.schedule_box, null);
                    ImageView scheduleCategory = scheduleBox.findViewById(R.id.schedule_box_category);
                    TextView titleTextView = scheduleBox.findViewById(R.id.schedule_box_text);
                    TextView timeTextView = scheduleBox.findViewById(R.id.schedule_box_time);
                    ImageButton deleteButton = scheduleBox.findViewById(R.id.schedule_deleteButton);

                    String title = schedule.title;
                    String startTime = schedule.startTime;
                    String endTime = schedule.endTime;
                    String categoryColor = schedule.category;

                    if (categoryColor == null) {
                        // 오류시 비출 기본 색상 값 설정
                        categoryColor = "#000000";
                    }
                    //Log.d("Calendar categorycolor", categoryColor);

                    Log.d("showft", String.valueOf(showft));
                    Log.d("showholid", String.valueOf(showholi));

                    if ((showft && categoryColor.equals("#ed5c55")) ||
                            (showholi && categoryColor.equals("#52c8ed")) ||
                            ((!categoryColor.equals("#52c8ed")) &&
                                    (!categoryColor.equals("#ed5c55")))) {

                        scheduleCategory.setColorFilter(Color.parseColor(categoryColor));
                        titleTextView.setText(title); // 일정 데이터를 각 scheduleBox에 담는 작업

                        // 시작날짜-시간, 종료날짜-시간 분류예정
                        if (selectedDate.compareTo(startDate) == 0) { // selectedDate와 startDate가 같을 때
                            timeTextView.setText(startTime);
                        } else if (selectedDate.compareTo(endDate) == 0) { // selectedDate와 endDate가 같을 때
                            timeTextView.setText(endTime);
                        }

                        scheduleCount++;
                        // 각 scheduleBox를 scheduleContainer에 추가
                        scheduleContainer.addView(scheduleBox);

                        // 삭제 버튼 클릭 리스너 등록
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Boolean[] check = {false};
                                new AlertDialog.Builder(CalendarActivity.this)
                                        .setTitle("일정 삭제")
                                        .setMessage("일정을 삭제하시겠습니까?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // 확인시 처리 로직
                                                Toast.makeText(CalendarActivity.this, "일정을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                                deleteSchedule(schedule);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // 스케줄을 삭제하는 메서드
    private void deleteSchedule(CalendarEntity schedule) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // CalendarDao를 사용하여 데이터베이스에서 일정 데이터를 삭제
                calendarDao.DeleteSchedule(schedule);
                // 삭제한 후에 화면 갱신
                FetchScheduleTask fetchScheduleTask = new FetchScheduleTask(CalendarActivity.this, calendarDao);
                fetchScheduleTask.fetchSchedules(new FetchScheduleTask.FetchScheduleTaskListener() {
                    @Override
                    public void onFetchCompleted(List<CalendarEntity> scheduleList) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUI(scheduleList);
                            }
                        });
                    }
                });
            }
        });
    }


    //날짜 생성
    private ArrayList<Date> daysInMonthArray() {
        ArrayList<Date> dayList = new ArrayList<>();
        //날짜 복사 후 변수 생성
        Calendar monthCalendar = (Calendar) selectedDate.clone();
        //1일로 set
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        while (dayList.size() < 42) { //42칸 채우도록 날짜 추가
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
