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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private String query, detailInfo;
    public Date date;
    public SimpleDateFormat sdf;
    private ApiReader apiReader;
    private final int numberOfLayouts = 3;
    private ExecutorService executor;

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
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#E31B1B")), startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(spannableString);

        HashGetter.getHashKey(getApplicationContext());
        apiReader = new ApiReader();
        String apiKey = getResources().getString(R.string.api_key);
        String holidayKey = getResources().getString(R.string.holiday_key);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);


        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
        calendarDao = calendarDatabase.calendarDao();

        searchView = findViewById(R.id.main_search_bar);
        searchView.setQueryHint("어떤 축제를 즐겨볼까요?");
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
        String[] bannerImageUrls = {
                "http://tong.visitkorea.or.kr/cms/resource/98/3012698_image2_1.jpg",
                "http://tong.visitkorea.or.kr/cms/resource/15/2594915_image2_1.jpg",
                "http://tong.visitkorea.or.kr/cms/resource/62/2786362_image2_1.jpg"
        };
        String[] ids = {"3012714","506926","2786391"};

        ImageBannerAdapter adapter = new ImageBannerAdapter(this, bannerImageUrls, ids, banner, this);
        banner.setAdapter(adapter);


        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy.M.d");
        Date date = new Date();
        String todaydate = sdf2.format(date);
        String todaydatestand = sdff.format(date);

        // 날짜 문자열에서 년도와 월 추출
        String year = todaydate.substring(0, 4);
        String month = todaydate.substring(4, 6);

        executor = Executors.newCachedThreadPool();

        LinearLayout vacationFestival = findViewById(R.id.main_vacation_festival_container);

        CalendarDatabase database = CalendarDatabase.getInstance(MainActivity.this);
        CalendarDao calendarDao = database.calendarDao();
        String category = "#52c8ed"; //휴가 db가져오기위한 category text

        List<HashMap<String, String>> holidaylist = new ArrayList<>();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);
                if (loadedUser == null || loadedUser.getIsLogin() == false) { //미로그인시 국경일만 visible하도록 예외처리
                    Log.e("Err", "No UserInfo");
                } else {
                    List<CalendarEntity> calendarEntities = calendarDao.getCalendarEntitiesByCategory(category);

                    holidaylist.clear();
                    // 데이터 추출 및 holidaylist에 추가
                    for (CalendarEntity entity : calendarEntities) {
                        HashMap<String, String> holidayInfo = new HashMap<>();

                        if (entity.endDate.compareTo(todaydatestand) < 0) {
                            continue; // maxLocdate가 이미 지난 날짜이면 추가하지 않음
                        }

                        String[] parts = entity.startDate.split("\\."); // 점(.)을 구분자로 사용하여 문자열을 분할
                        String formattedDate = parts[0] + String.format("%02d", Integer.parseInt(parts[1])) + String.format("%02d", Integer.parseInt(parts[2]));

                        String[] endparts = entity.endDate.split("\\."); // 점(.)을 구분자로 사용하여 문자열을 분할
                        String formattedendDate = endparts[0] + String.format("%02d", Integer.parseInt(endparts[1])) + String.format("%02d", Integer.parseInt(endparts[2]));

                        String minMonth = String.valueOf(Integer.parseInt(formattedDate.substring(4, 6)));
                        String minDay = String.valueOf(Integer.parseInt(formattedDate.substring(6, 8)));
                        String maxMonth = String.valueOf(Integer.parseInt(formattedendDate.substring(4, 6)));
                        String maxDay = String.valueOf(Integer.parseInt(formattedendDate.substring(6, 8)));

                        String entitylocdate = minMonth + "/" + minDay + "~" + maxMonth + "/" + maxDay;

                        holidayInfo.put("dateName", "휴가");
                        holidayInfo.put("locdate", entitylocdate);
                        holidayInfo.put("startdate", formattedDate);
                        holidayInfo.put("enddate", formattedendDate);
                        Log.d("locdate", entitylocdate);
                        holidaylist.add(holidayInfo);
                    }
                }

                apiReader.holiday(holidayKey, year, month, new ApiReader.ApiResponseListener() { //api로 국경일 불러옴
                    @Override
                    public void onSuccess(String response) {
                        ParsingApiData.parseXmlDataFromHoliday(response);
                        Log.d("holiday respnse", response);
                        List<LinkedHashMap<String, String>> groupedHolidayList = ParsingApiData.getHolidayList();
                        List<LinkedHashMap<String, String>> updatedHolidayList = groupAndCombineDates(groupedHolidayList);

                        holidaylist.addAll(updatedHolidayList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (holidaylist.size() > 0) {
                                    for (int i = 0; i < 5; i++) {
                                        List<HashMap<String, String>> festivalList = new ArrayList<>();
                                        View sliderItemView = getLayoutInflater().inflate(R.layout.slider_item, null);
                                        ImageButton imageButton = sliderItemView.findViewById(R.id.image_button);
                                        TextView datetext = sliderItemView.findViewById(R.id.date_text);
                                        int finalImages = i;

                                        HashMap<String, String> holidayInfo;
                                        if (holidaylist.size() <= i) { //휴가일수가 작은경우 0번째 휴가로 5개까지 계속 검색
                                            holidayInfo = holidaylist.get(0);
                                        } else {
                                            holidayInfo = holidaylist.get(i);
                                        }

                                        String name = holidayInfo.get("dateName");
                                        String date = holidayInfo.get("locdate");
                                        String startholidate = holidayInfo.get("startdate");
                                        String endholidate = holidayInfo.get("enddate");

                                        Log.d("holiday name", name);
                                        Log.d("holiday date", date);
                                        Log.d("holiday start", startholidate);
                                        Log.d("holiday end", endholidate);

                                        apiReader.Festivallit2(apiKey, startholidate, endholidate, new ApiReader.ApiResponseListener() {
                                            @Override
                                            public void onSuccess(String response) {
                                                //Log.d("main response", response);
                                                ParsingApiData.parseXmlDataFromFestival(response);
                                                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                                festivalList.clear();
                                                festivalList.addAll(parsedFestivalList);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (festivalList != null && festivalList.size() > 0) {
                                                            HashMap<String, String> festivalInfo = null;
                                                            if (finalImages >= 0 && finalImages < festivalList.size()) {
                                                                festivalInfo = festivalList.get(finalImages);

                                                                if (festivalInfo != null) {
                                                                    String id = festivalInfo.get("contentid");
                                                                    String repImage = festivalInfo.get("img");

                                                                    if (repImage == null || repImage.isEmpty()) {
                                                                        imageButton.setImageResource(R.mipmap.empty_image);
                                                                    } else {
                                                                        Glide.with(MainActivity.this)
                                                                                .load(repImage)
                                                                                .fitCenter()
                                                                                .transform(new CenterCrop(), new RoundedCorners(30))
                                                                                .placeholder(R.mipmap.empty_image)
                                                                                .into(imageButton);
                                                                    }

                                                                    //휴가가 하루일경우 해당 날짜만 settext
                                                                    if (startholidate.equals(endholidate)) {
                                                                        String monthtext = String.valueOf(Integer.parseInt(startholidate.substring(4, 6)));
                                                                        String datext = String.valueOf(Integer.parseInt(startholidate.substring(6, 8)));
                                                                        datetext.setText(monthtext + "/" + datext);
                                                                    } else {
                                                                        datetext.setText(date);
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
                                                        }
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

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Holiday API Error: " + error);
                    }
                });

            }
        });

        String[] mainFestivalArea = {"서울", "경기도", "부산", "전라북도"};
        String[] mainFestivalAreaCode = {"1", "31", "6", "37"};

        RecyclerView recyclerView = findViewById(R.id.main_festival_container_group);
        FestivalAdapter adapters = new FestivalAdapter(this, MainActivity.this, userId); // Context와 MainActivity를 전달
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapters);

        // 데이터 로딩을 시작하기 전에 로딩 상태를 설정
        adapters.setLoading(true);

        for (int area = 0; area < 3; area++) {
            String areaName = mainFestivalArea[area];

            apiReader.FestivallitLoc(apiKey, todaydate, mainFestivalAreaCode[area], new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parsingData(apiKey, response, adapters, areaName);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
                }
            });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티 종료 시 ExecutorService 종료
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }


    public void parsingData(String apiKey, String response, FestivalAdapter adapter, String areaName) {
        ParsingApiData.parseXmlDataFromDetail2(response);
        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (parsedFestivalList.size() > 0) {
                    int maxItems = Math.min(parsedFestivalList.size(), 5);
                    List<HashMap<String, String>> festivalList = new ArrayList<>();
                    festivalList.addAll(parsedFestivalList.subList(0, maxItems));

                    // Adapter에 데이터 추가
                    adapter.addData(festivalList, areaName);
                }

                // 모든 지역의 데이터 로딩이 완료되면 로딩 상태를 해제
                adapter.setLoading(false);
            }
        });
    }

    public void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.custom_popup);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_popup, null);
        Button cancelButton = dialogView.findViewById(R.id.dialog_popup_close_btn);
        Button confirmButton = dialogView.findViewById(R.id.dialog_popup_add_btn);

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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
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

    private List<LinkedHashMap<String, String>> groupAndCombineDates(List<LinkedHashMap<String, String>> orholidaylist) {
        Map<String, List<String>> dateGroups = new HashMap<>();

        for (LinkedHashMap<String, String> holidayInfo : orholidaylist) {
            String dateName = holidayInfo.get("dateName");
            String locdate = holidayInfo.get("locdate");

            if (dateGroups.containsKey(dateName)) {
                dateGroups.get(dateName).add(locdate);
            } else {
                // 새로운 dateName을 위한 리스트를 생성
                List<String> dateList = new ArrayList<>();
                dateList.add(locdate);
                dateGroups.put(dateName, dateList);
            }
        }

        // 날짜 범위 조합
        List<LinkedHashMap<String, String>> combinedList = new ArrayList<>();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentLocdate = dateFormat.format(currentDate);

        for (Map.Entry<String, List<String>> entry : dateGroups.entrySet()) {
            String dateName = entry.getKey();
            List<String> locdates = entry.getValue();

            // 최소 및 최대 locdate
            String minLocdate = Collections.min(locdates);
            String maxLocdate = Collections.max(locdates);

            if (maxLocdate.compareTo(currentLocdate) <= 0) {
                continue; // maxLocdate가 이미 지난 날짜이면 추가하지 않음
            }

            String minMonth = String.valueOf(Integer.parseInt(minLocdate.substring(4, 6)));
            String minDay = String.valueOf(Integer.parseInt(minLocdate.substring(6, 8)));
            String maxMonth = String.valueOf(Integer.parseInt(maxLocdate.substring(4, 6)));
            String maxDay = String.valueOf(Integer.parseInt(maxLocdate.substring(6, 8)));

            String dateRange = minMonth + "/" + minDay + "~" + maxMonth + "/" + maxDay;

            // 조합된 목록에 새 항목을 생성
            LinkedHashMap<String, String> combinedInfo = new LinkedHashMap<>();
            combinedInfo.put("dateName", dateName);
            combinedInfo.put("locdate", dateRange);
            combinedInfo.put("startdate", minLocdate);
            combinedInfo.put("enddate", maxLocdate);

            combinedList.add(combinedInfo);
        }

        // holidaylist를 조합된 목록으로 업데이트
        return combinedList;
    }

}
