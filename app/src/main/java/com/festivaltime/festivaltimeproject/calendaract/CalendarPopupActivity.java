package com.festivaltime.festivaltimeproject.calendaract;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.MainActivity;
import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//캘린더 일정 추가 dialog class
public class CalendarPopupActivity extends Dialog {
    private CalendarCategoryDataBase categoryDataBase;
    protected Context mContext;
    public String categorycolor = null;
    public EditText TitleText;
    final Button shutdownClick, addBtn, startdateClick, starttimeClick, enddateClick, endtimeClick, categoryButton;
    public DatePicker StartDatePicker, EndDatePicker;
    public TimePicker StartTimePicker, EndTimePicker;
    public Switch alldaySwitch;

    // CalendarDatabase에 대한 참조 추가
    private CalendarDatabase calendarDatabase;

    public CalendarPopupActivity(@NonNull Context context) {
        super(context);

        //팝업 애니메이션 위한 윈도우
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // 팝업창 배경을 투명하게 설정
        this.setContentView(R.layout.activity_calendar_popup); // 팝업창 레이아웃 설정
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); // 팝업창 크기 설정
        this.getWindow().setGravity(Gravity.BOTTOM); // 팝업창 위치 설정

        setContentView(R.layout.activity_calendar_popup);
        this.mContext = context;


        TitleText = findViewById(R.id.calendar_popup_title_Text);
        shutdownClick = findViewById(R.id.calendar_popup_close_btn);
        addBtn = findViewById(R.id.calendar_popup_add_finish_btn);
        categoryButton = findViewById(R.id.calendar_popup_category);
        startdateClick = findViewById(R.id.calendar_popup_start_date);
        starttimeClick = findViewById(R.id.calendar_popup_start_time);
        StartDatePicker = findViewById(R.id.calendar_popup_StartDatePicker);
        StartTimePicker = findViewById(R.id.calendar_popup_StartTimePicker);
        enddateClick = findViewById(R.id.calendar_popup_end_date);
        endtimeClick = findViewById(R.id.calendar_popup_end_time);
        EndDatePicker = findViewById(R.id.calendar_popup_EndDatePicker);
        EndTimePicker = findViewById(R.id.calendar_popup_EndTimePicker);
        alldaySwitch = findViewById(R.id.calendar_popup_switchView);

        // CalendarDatabase 및 CalendarCategoryDataBase 인스턴스 초기화
        calendarDatabase = CalendarDatabase.getInstance(context);
        categoryDataBase = CalendarCategoryDataBase.getInstance(context);


        //뒤로가기 true
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window window = getWindow();

        if (window != null) {
            //팝업 배경 투명설정 별로같아서 주석처리함)
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes(params);
            // UI 하단 정렬
            window.setGravity(Gravity.BOTTOM);
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
                Log.d("startdate", startDate);
                String startTime = starttimeClick.getText().toString();
                String endDate = enddateClick.getText().toString();
                String endTime = endtimeClick.getText().toString();
                String category = categoryButton.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

                if (title.isEmpty()) {
                    // 제목이 입력되지 않았을 때 토스트 메시지를 표시합니다.
                    Toast.makeText(mContext, "Title을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Date startdate = sdf.parse(startDate + " " + startTime);
                        Date enddate = sdf.parse(endDate + " " + endTime);

                        if (enddate.after(startdate) || enddate.equals(startdate)) {
                            // 종료 날짜-시간이 시작 날짜-시간보다 나중일 경우

                            if (startTime.equals("00:00") && endTime.equals("00:00")) {
                                startTime = "";
                                endTime = "";
                            }

                            // 사용자 입력으로 새로운 CalendarEntity 객체를 생성
                            CalendarEntity newSchedule = new CalendarEntity();
                            newSchedule.title = title;
                            newSchedule.startDate = startDate;
                            newSchedule.endDate = endDate;
                            newSchedule.startTime = startTime;
                            newSchedule.endTime = endTime;
                            newSchedule.contentid = "";

                            new LoadCategoryColorTask(new CategoryColorCallback() {
                                @Override
                                public void onCategoryColorLoaded(String categoryColor) {
                                    categorycolor = categoryColor;
                                    newSchedule.category = categorycolor;

                                    // ScheduleLoader를 사용하여 새 일정을 데이터베이스에 추가
                                    ScheduleLoader loader = new ScheduleLoader(getContext(), newSchedule, calendarDatabase.calendarDao());
                                    loader.forceLoad();

                                    // 대화 상자 닫기
                                    dismiss();
                                }
                            }).

                                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, category);
                        } else {
                            Toast.makeText(mContext, "기간을 확인해주세요", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        //하루종일 스위치 on off시 시간(time) 표시 on off
        alldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StartDatePicker.setVisibility(View.GONE);
                StartTimePicker.setVisibility(View.GONE);
                EndDatePicker.setVisibility(View.GONE);
                EndTimePicker.setVisibility(View.GONE);

                if (isChecked) {
                    starttimeClick.setText("00:00");
                    endtimeClick.setText("00:00");

                    starttimeClick.setVisibility(View.GONE);
                    endtimeClick.setVisibility(View.GONE);
                } else {
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
                            StartDatePicker.setVisibility(View.GONE);
                        } else {
                            StartDatePicker.setVisibility(View.VISIBLE);
                        }
                        break;

                    case R.id.calendar_popup_start_time: //시작 시간-시간 클릭
                        StartDatePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (StartTimePicker.getVisibility() == View.VISIBLE) {
                            StartTimePicker.setVisibility(View.GONE);
                        } else {
                            StartTimePicker.setVisibility(View.VISIBLE);
                        }
                        break;

                    case R.id.calendar_popup_end_date: //end 시간-날짜 클릭시 표시
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        if (EndDatePicker.getVisibility() == View.VISIBLE) {
                            EndDatePicker.setVisibility(View.GONE);
                        } else {
                            EndDatePicker.setVisibility(View.VISIBLE);
                        }
                        break;

                    case R.id.calendar_popup_end_time: //end 시간-시간 클릭
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        if (EndTimePicker.getVisibility() == View.VISIBLE) {
                            EndTimePicker.setVisibility(View.GONE);
                        } else {
                            EndTimePicker.setVisibility(View.VISIBLE);
                        }
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

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 백그라운드 스레드에서 작업을 처리하기 위해 AsyncTask를 생성하여 실행
                new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private class LoadCategoryTask extends AsyncTask<Void, Void, List<CalendarCategoryEntity>> {
        @Override
        protected List<CalendarCategoryEntity> doInBackground(Void... voids) {
            // 데이터베이스 인스턴스 생성
            CalendarCategoryDataBase categoryDataBase = CalendarCategoryDataBase.getInstance(mContext);

            // 데이터베이스에서 카테고리 목록을 가져옴
            return categoryDataBase.categoryDao().getAllCategoryEntity();
        }

        @Override
        protected void onPostExecute(List<CalendarCategoryEntity> categories) {
            // 데이터베이스에서 가져온 카테고리 목록을 메뉴에 추가하는 코드
            setupCategoryMenu(categories);
        }
    }

    private void setupCategoryMenu(List<CalendarCategoryEntity> categories) {
        // 팝업 메뉴를 생성하고 위치 지정
        PopupMenu popupMenu = new PopupMenu(mContext, categoryButton);

        // 메뉴 인플레이션
        popupMenu.getMenuInflater().inflate(R.menu.dialog_categorypopup, popupMenu.getMenu());

        // 동적으로 추가할 그룹 ID
        int groupId = R.id.dynamic_items_group;

        // 데이터베이스에서 가져온 카테고리 목록을 메뉴에 추가
        for (CalendarCategoryEntity category : categories) {
            popupMenu.getMenu().add(groupId, Menu.NONE, Menu.NONE, category.getName());
        }

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 선택한 메뉴 아이템의 이름을 가져와서 처리
                String selectedCategory = item.getTitle().toString();

                // 선택한 카테고리 이름을 버튼에 설정
                categoryButton.setText(selectedCategory);
                return true;
            }
        });

        // 팝업 메뉴 보이기
        popupMenu.show();
    }

    private String setCategoryColor(List<CalendarCategoryEntity> categories, String text) {
        String returnColor = null;
        if (text.equals("축제")) {
            returnColor = "#ed5c55";
        } else if (text.equals("휴가")) {
            returnColor = "#52c8ed";
        } else {
            // 대소문자를 무시하고 비교
            String lowercaseText = text.toLowerCase(Locale.getDefault());
            for (CalendarCategoryEntity category : categories) {
                String lowercaseCategoryName = category.getName().toLowerCase(Locale.getDefault());
                if (lowercaseText.equals(lowercaseCategoryName)) {
                    returnColor = category.color;
                    break; // 일치하는 카테고리를 찾았으면 루프 종료
                }
            }
            if (returnColor == null) {
                // 일치하는 카테고리를 찾지 못한 경우 로그 출력
                Log.d("CategoryColorDebug", "No matching category color found for: " + text);
            }
        }
        return returnColor;
    }

    public interface CategoryColorCallback {
        void onCategoryColorLoaded(String categoryColor);
    }

    private class LoadCategoryColorTask extends AsyncTask<String, Void, String> {
        private CategoryColorCallback callback;

        public LoadCategoryColorTask(CategoryColorCallback callback) {
            this.callback = callback;
        }

        private CalendarPopupActivity mActivity;

        public LoadCategoryColorTask(CalendarPopupActivity activity) {
            mActivity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String categorytxt = params[0];
            // 데이터베이스 인스턴스 생성
            CalendarCategoryDataBase categoryDataBase = CalendarCategoryDataBase.getInstance(mContext);

            List<CalendarCategoryEntity> categories = categoryDataBase.categoryDao().getAllCategoryEntity();

            // setCategoryColor 메서드 호출
            return setCategoryColor(categories, categorytxt);
        }

        @Override
        protected void onPostExecute(String categorycolor) {
            if (callback != null) {
                callback.onCategoryColorLoaded(categorycolor);
            }
        }

    }


}