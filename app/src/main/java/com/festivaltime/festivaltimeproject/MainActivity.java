package com.festivaltime.festivaltimeproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToSearchActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private String query, detailInfo;
    public Date date;
    public SimpleDateFormat sdf;
    private ApiReader apiReader;
    private Executor executor;

    private final int numberOfLayouts = 3;

    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    private UserEntity loadedUser;
    private String userId;
    private UserDataBase db;
    private UserDao userDao;

    private String locationSelect;
    private String formattedStartDate;
    private String formattedEndDate;
    private TextView textview;
    private TextView textview2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //색 넣음
        textview = findViewById(R.id.text_view);
        String text = textview.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf("HOT!");
        int endIndex = startIndex + "HOT!".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6666")), startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(spannableString);

        HashGetter.getHashKey(getApplicationContext());
        apiReader = new ApiReader();
        String apiKey = getResources().getString(R.string.api_key);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);


        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
        calendarDao = calendarDatabase.calendarDao();

        searchView = findViewById(R.id.main_search_bar);
        searchView.setOnTouchListener((v, event) -> {
            searchView.setIconified(false);
            searchView.performClick();
            return true;
        });

        ImageButton searchoptionbutton = findViewById(R.id.detailButton);
        searchoptionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {       //검색 시 검색 내용 SearchActivity 로 전달
                query = s;
                performSearch(query);
                navigateToSearchActivity(MainActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        ViewPager2 banner = findViewById(R.id.main_festival_banner);  //배너
        int[] bannerImages = {R.drawable.image02, R.drawable.image02, R.drawable.image02};

        ImageBannerAdapter adapter = new ImageBannerAdapter(this, bannerImages, banner);
        banner.setAdapter(adapter);
        adapter.startAutoSlide();


        int[] vacationImages = {R.drawable.first_image_example, R.drawable.first_image_example, R.drawable.first_image_example, R.drawable.first_image_example, R.drawable.first_image_example};

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String todaydate = sdf2.format(date);

        // 날짜 문자열에서 년도와 월 추출
        String year = todaydate.substring(0, 4);
        String month = todaydate.substring(4, 6);

        LinearLayout vacationFestival = findViewById(R.id.main_vacation_festival_container);

        CalendarDatabase database = CalendarDatabase.getInstance(MainActivity.this);
        CalendarDao calendarDao = database.calendarDao();
        String category = "#52c8ed"; //휴가 db가져오기위한 category text

        List<HashMap<String, String>> holidaylist = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CalendarEntity> calendarEntities = calendarDao.getCalendarEntitiesByCategory(category);

                holidaylist.clear();
                // 데이터 추출 및 holidaylist에 추가
                for (CalendarEntity entity : calendarEntities) {
                    HashMap<String, String> holidayInfo = new HashMap<>();
                    holidayInfo.put("dateName", "휴가");
                    holidayInfo.put("locdate", entity.startDate);
                    holidaylist.add(holidayInfo);
                }
            }
        }).start();
        apiReader.holiday(apiKey, year, month, new ApiReader.ApiResponseListener() { //api로 국경일 불러옴
            @Override
            public void onSuccess(String response) {
                ParsingApiData.parseXmlDataFromHoliday(response);
                List<LinkedHashMap<String, String>> parsedHolidayList = ParsingApiData.getHolidayList();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        holidaylist.addAll(parsedHolidayList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (holidaylist.size() > 0) {
                                    int maxItems = Math.min(holidaylist.size(), 5);
                                    for (int i = 0; i < maxItems; i++) {
                                        List<HashMap<String, String>> festivalList = new ArrayList<>();
                                        View sliderItemView = getLayoutInflater().inflate(R.layout.slider_item, null);
                                        ImageButton imageButton = sliderItemView.findViewById(R.id.image_button);
                                        int finalImages = i;

                                        HashMap<String, String> holidayInfo = holidaylist.get(i);

                                        String name = holidayInfo.get("dateName");
                                        String date = holidayInfo.get("locdate");

                                        Log.d("holiday name", name);
                                        Log.d("holiday date", date);

                                        apiReader.Festivallit(apiKey, date, new ApiReader.ApiResponseListener() {
                                            @Override
                                            public void onSuccess(String response) {
                                                //Log.d("main response", response);
                                                ParsingApiData.parseXmlDataFromFestival(response);
                                                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                                executor.execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        festivalList.clear();
                                                        festivalList.addAll(parsedFestivalList);

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (festivalList.size() > 0) {
                                                                    HashMap<String, String> festivalInfo = festivalList.get(finalImages);

                                                                    String id = festivalInfo.get("contentid");
                                                                    String repImage = festivalInfo.get("img");

                                                                    if (repImage == null || repImage.isEmpty()) {
                                                                        imageButton.setImageResource(R.drawable.first_image_example);
                                                                    } else {
                                                                        Glide.with(MainActivity.this).load(repImage).transform(new CenterCrop(), new RoundedCorners(30)).placeholder(R.drawable.first_image_example).into(imageButton);
                                                                    }

                                                                    imageButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            String contentId = id;
                                                                            navigateToDetailFestivalActivity(MainActivity.this, contentId);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onError(String error) {
                                                Log.e(TAG, "API Error: " + error);
                                            }
                                        });
                                        vacationFestival.addView(sliderItemView);
                                    }
                                }

                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Holiday API Error: " + error);
            }
        });

        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        };

        String[] mainFestivalArea = {"서울", "경기도", "부산", "전라북도"};
        String[] mainFestivalAreaCode = {"1", "31", "6", "37"};


        for (int area = 0; area < 4; area++) {
            List<HashMap<String, String>> festivalList = new ArrayList<>();
            LinearLayout mainFestivalContainerGroup = findViewById(R.id.main_festival_container_group);
            LinearLayout mainfestivalContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.main_festival_container, null);
            TextView areaName = mainfestivalContainer.findViewById(R.id.main_festival_area_title);

            apiReader.FestivallitLoc(apiKey, todaydate, mainFestivalAreaCode[area], new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    parsingData(mainfestivalContainer, apiKey, response, festivalList);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
                }
            });

            areaName.setText(mainFestivalArea[area]);
            mainFestivalContainerGroup.addView(mainfestivalContainer);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item ->

        {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(MainActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(MainActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(MainActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(MainActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(MainActivity.this);
                return true;
            }
        });
    }


    private void parsingData(LinearLayout mainfestivalContainer, String apiKey, String response, List<HashMap<String, String>> festivalList) {
        //Log.d("main response", response);
        ParsingApiData.parseXmlDataFromDetail2(response);
        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                festivalList.clear();
                festivalList.addAll(parsedFestivalList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (festivalList.size() > 0) {
                            int maxItems = Math.min(festivalList.size(), 5);
                            for (int i = 0; i < maxItems; i++) {
                                HashMap<String, String> festivalInfo = festivalList.get(i);

                                View festivalBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
                                TextView titleTextView = festivalBox.findViewById(R.id.festival_title);
                                TextView locationTextView = festivalBox.findViewById(R.id.festival_location);
                                TextView overviewTextView = festivalBox.findViewById(R.id.festival_overview);
                                ImageButton searchImageButton = festivalBox.findViewById(R.id.festival_rep_image);
                                ImageButton calendaraddButton = festivalBox.findViewById(R.id.calendar_addButton);
                                ImageButton favoriteaddButton = festivalBox.findViewById(R.id.favorite_addButton);
                                TextView stateTextView = festivalBox.findViewById(R.id.festival_state);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                layoutParams.setMargins(0, 0, 0, 40);
                                festivalBox.setLayoutParams(layoutParams);

                                String title = festivalInfo.get("title");
                                String id = festivalInfo.get("contentid");
                                String address1 = festivalInfo.get("address1");
                                //문자열길이 일정수 넘어가면 ...형태로 표시
                                if (address1 != null && address1.length() > 20) {
                                    address1 = address1.substring(0, 20) + "...";
                                }
                                String repImage = festivalInfo.get("img");
                                String startDateStr = festivalInfo.get("eventstartdate");
                                String endDateStr = festivalInfo.get("eventenddate");

                                // 날짜 문자열을 Date 객체로 변환
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                                Date startDate = null;
                                Date endDate = null;
                                Date currentDate = new Date(); // 현재 날짜 가져오기

                                try {
                                    startDate = dateFormat.parse(startDateStr);
                                    endDate = dateFormat.parse(endDateStr);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                titleTextView.setText(title);
                                locationTextView.setText(address1);


                                if (startDate != null && endDate != null) {
                                    if (currentDate.before(startDate)) {
                                        // 현재 날짜가 시작일 이전인 경우
                                        stateTextView.setText("진행예정");
                                        stateTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light)); // 주황색으로 설정
                                    } else if (currentDate.after(endDate)) {
                                    } else {
                                        // 현재 날짜가 시작일과 종료일 사이에 있는 경우
                                        stateTextView.setText("진행중");
                                        stateTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light)); // 초록색으로 설정
                                    }
                                }

                                if (repImage == null || repImage.isEmpty()) {
                                    searchImageButton.setImageResource(R.drawable.ic_image);
                                } else {
                                    Glide.with(MainActivity.this).load(repImage).transform(new CenterCrop(), new RoundedCorners(30)).placeholder(R.drawable.ic_image).into(searchImageButton);
                                }

                                apiReader.FestivalInfo2(apiKey, id, new ApiReader.ApiResponseListener() {
                                    @Override
                                    public void onSuccess(String response) {
                                        ParsingApiData.parseXmlDataFromDetailInfo2(response);
                                        //Log.d("festivalinfo response: ", response);
                                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                                        LinkedHashMap<String, String> firstMap = null;

                                        try {
                                            firstMap = parsedFestivalList.get(0);
                                        } catch (IndexOutOfBoundsException e) {
                                        }
                                        String detailInfo = firstMap.get("infotext");

                                        //문자열길이 일정수 넘어가면 ...형태로 표시
                                        if (detailInfo != null && detailInfo.length() > 40) {
                                            detailInfo = detailInfo.substring(0, 40) + "...";
                                        }

                                        //html 형태 변환하여 setText
                                        if (detailInfo != null) {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                overviewTextView.setText(Html.fromHtml(detailInfo, Html.FROM_HTML_MODE_LEGACY));
                                            } else {
                                                overviewTextView.setText(Html.fromHtml(detailInfo));
                                            }
                                        } else {
                                            // detailInfo가 null인 경우에 대한 처리 추가
                                        }


                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "API Error: " + error);
                                    }
                                });

                                mainfestivalContainer.addView(festivalBox);

                                festivalBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 클릭 시 contentid 값을 가져오는 작업 수행
                                        String contentId = id;
                                        // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                        navigateToDetailFestivalActivity(MainActivity.this, contentId);
                                    }
                                });

                                favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("Button Listener", "addBtn");
                                        executor.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                String contentId = id;
                                                loadedUser = userDao.getUserInfoById(userId);
                                                if (loadedUser != null) {
                                                    if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                                        Log.d("Festival Id", contentId);
                                                        Log.d("Button Listener", "ID already exists in userFavoriteFestival");
                                                    } else {
                                                        Log.d("Festival Id", contentId);
                                                        loadedUser.addUserFavoriteFestival(contentId);
                                                        userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
                                                    }
                                                } else {
                                                    Log.e("No UserInfo", "You should get your information in MyPage");
                                                }
                                            }
                                        });
                                    }
                                });

                                calendaraddButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        executor.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                                                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy.M.d", Locale.getDefault());
                                                    Date currentDate = new Date();
                                                    String currentDateStr = sdf.format(currentDate);

                                                    String CompareStartDate = startDateStr;
                                                    String CompareEndDate = endDateStr;

                                                    Log.d("startdate: ", "Start Date: " + CompareStartDate);
                                                    Log.d("enddate: ", "End Date: " + CompareEndDate);

                                                    String startTime = "";
                                                    String endTime = "";

                                                    loadedUser = userDao.getUserInfoById(userId);
                                                    calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
                                                    calendarDao = calendarDatabase.calendarDao();
                                                    if (loadedUser != null) {
                                                        if (loadedUser.getIsLogin()) {
                                                            if (calendarDao.getAllContentIds().contains(id)) {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), "이미 추가된 축제입니다", Toast.LENGTH_SHORT).show();
                                                                        Log.d("Button Listener", "Festival already exists in Calendar");
                                                                    }
                                                                });
                                                            } else {
                                                                Date compareDate = sdf.parse(CompareEndDate);
                                                                Date current = sdf.parse(currentDateStr);

                                                                //날짜없으면 추가안되도록 설정
                                                                if (CompareStartDate != null && CompareEndDate != null) {
                                                                    if (current.compareTo(compareDate) <= 0) {
                                                                        Log.d("Button Action", "Add about Festival to Calendar");

                                                                        Date originalStartDate = sdf.parse(CompareStartDate);
                                                                        String formattedStartDate = targetDateFormat.format(originalStartDate);
                                                                        Date originalEndDate = sdf.parse(CompareEndDate);
                                                                        String formattedEndDate = targetDateFormat.format(originalEndDate);

                                                                        // 일수 계산 위해 밀리초로 날짜 변환
                                                                        long startDateMillis = originalStartDate.getTime();
                                                                        long endDateMillis = originalEndDate.getTime();

                                                                        // 두 날짜 사이의 일 수 계산
                                                                        long daysBetween = (endDateMillis - startDateMillis) / (1000 * 60 * 60 * 24);

                                                                        // 14일(2주) 이상인 경우 팝업
                                                                        if (daysBetween >= 14) {
                                                                            runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    AddFT dialog = new AddFT(MainActivity.this, title, id, formattedStartDate, formattedEndDate);
                                                                                    dialog.show();

                                                                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                                        @Override
                                                                                        public void onDismiss(DialogInterface dialog) {
                                                                                            // 팝업이 닫힐 때 실행할 코드 작성 부분
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        } else {

                                                                            Log.d("formattedStartDate: ", "Formatted Start Date: " + formattedStartDate);
                                                                            Log.d("formattedEndDate: ", "Formatted End Date: " + formattedEndDate);

                                                                            calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
                                                                            calendarDao = calendarDatabase.calendarDao();

                                                                            // CalendarEntity 생성
                                                                            CalendarEntity event = new CalendarEntity();
                                                                            event.title = title;
                                                                            event.startDate = formattedStartDate;
                                                                            event.endDate = formattedEndDate;
                                                                            event.startTime = startTime;
                                                                            event.endTime = endTime;
                                                                            event.category = "#ed5c55";
                                                                            event.contentid = id;

                                                                            // CalendarEntityDao를 사용하여 데이터베이스에 이벤트 추가
                                                                            calendarDao.InsertSchedule(event);
                                                                        }
                                                                    } else {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Toast.makeText(getApplicationContext(), "이미 지난 축제입니다", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                } else {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(getApplicationContext(), "축제날짜를 확인해주세요", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        } else {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }
                                });
                            }

                        }
                    }
                });
            }
        });
    }

    private void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.custom_popup);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_popup, null);
        Button cancelButton = dialogView.findViewById(R.id.dialog_popup_close_btn);
        Button confirmButton = dialogView.findViewById(R.id.dialog_popup_add_btn);
        //진행상황 버튼
        ToggleButton completedToggle = findViewById(R.id.completed);
        ToggleButton ongoingToggle = findViewById(R.id.Ongoing);
        ToggleButton upgoingToggle = findViewById(R.id.Upgoing);
        // 시작 날짜 선택 버튼
        Button startdateClick = dialogView.findViewById(R.id.dialog_popup_start_date);
        Button enddateClick = dialogView.findViewById(R.id.dialog_popup_end_date);
        Button location = dialogView.findViewById(R.id.dialog_popup_location);

        DatePicker StartDatePicker = dialogView.findViewById(R.id.dialog_popup_StartDatePicker);
        DatePicker EndDatePicker = dialogView.findViewById(R.id.dialog_popup_EndDatePicker);

        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);

                getMenuInflater().inflate(R.menu.dialog_region, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.every_region:
                                location.setText("모든 지역");
                                break;
                            case R.id.seoul:
                                location.setText("서울");
                                locationSelect = "1";
                                break;
                            case R.id.gyeonggi:
                                location.setText("경기");
                                locationSelect = "31";
                                break;
                            case R.id.incheon:
                                location.setText("인천");
                                locationSelect = "2";
                                break;
                            case R.id.gangwon:
                                location.setText("강원");
                                locationSelect = "32";
                                break;
                            case R.id.jeju:
                                location.setText("제주");
                                locationSelect = "39";
                                break;
                            case R.id.daejeon:
                                location.setText("대전");
                                locationSelect = "3";
                                break;
                            case R.id.chungbuk:
                                location.setText("충북");
                                locationSelect = "33";
                                break;
                            case R.id.chungnam_sejong:
                                location.setText("충남/세종");
                                locationSelect = "34";
                                break;
                            case R.id.busan:
                                location.setText("부산");
                                locationSelect = "6";
                                break;
                            case R.id.ulsan:
                                location.setText("울산");
                                locationSelect = "7";
                                break;
                            case R.id.gyeongnam:
                                location.setText("경남");
                                locationSelect = "36";
                                break;
                            case R.id.daegu:
                                location.setText("대구");
                                locationSelect = "4";
                                break;
                            case R.id.gyeongbuk:
                                location.setText("경북");
                                locationSelect = "35";
                                break;
                            case R.id.gwangju:
                                location.setText("광주");
                                locationSelect = "5";
                                break;
                            case R.id.jeonnam:
                                location.setText("전남");
                                locationSelect = "38";
                                break;
                            case R.id.jeonju_jeonbuk:
                                location.setText("전주/전북");
                                locationSelect = "37";
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });


        startdateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StartDatePicker의 가시성을 토글
                switch (StartDatePicker.getVisibility()) {
                    case View.VISIBLE:
                        StartDatePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        StartDatePicker.setVisibility(View.VISIBLE);
                        EndDatePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        enddateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EndDatePicker의 가시성을 토글
                switch (EndDatePicker.getVisibility()) {
                    case View.VISIBLE:
                        EndDatePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        EndDatePicker.setVisibility(View.VISIBLE);
                        StartDatePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        //시작 시간-날짜 변화시
        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));

                formattedStartDate = String.format("%04d%02d%02d", year, month + 1, day);
            }
        });

        //end 시간-날짜 변화시
        EndDatePicker.init(EndDatePicker.getYear(), EndDatePicker.getMonth(), EndDatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                enddateClick.setText(String.format("%d.%d.%d", year, month + 1, day));

                formattedEndDate = String.format("%04d%02d%02d", year, month + 1, day);
            }
        });
        dialogBuilder.setView(dialogView);


        AlertDialog alertDialog = dialogBuilder.create();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 취소 버튼을 눌렀을 때 실행할 코드 작성
                alertDialog.dismiss(); // 팝업 대화상자 닫기
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String startdate = startdateClick.getText().toString();
                String enddate = enddateClick.getText().toString();

                // 시작 및 종료 날짜-시간 문자열을 적절한 형식으로 변환
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                try {
                    Date startDate = sdf.parse(startdate);
                    Date endDate = sdf.parse(enddate);

                    if (endDate.after(startDate)) {
                        // 종료 날짜-시간이 시작 날짜-시간보다 나중일 경우
                        // 여기에 추가 동작 수행

                        // 대화 상자 닫기
                        alertDialog.dismiss();

                        // SearchScreen으로 상세 검색 설정값 전송
                        Intent queryIntent = new Intent(MainActivity.this, SearchScreenActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("startdate", formattedStartDate);
                        bundle.putString("enddate", formattedEndDate);

                        if (locationSelect != null && !locationSelect.isEmpty()) {
                            bundle.putString("location", locationSelect);
                            DataHolder.getInstance().setBundle(bundle);
                            //navigateToSearchActivity(MainActivity.this, query, queryIntent);


                        } else {
                            Toast.makeText(MainActivity.this, "위치가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(MainActivity.this, "기간을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    private void performSearch(String query) {
        System.out.println("검색어: " + query);
    }

}
