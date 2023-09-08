package com.festivaltime.festivaltimeproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToSearchActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.festivaltime.festivaltimeproject.calendaract.ScheduleLoader;
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

    private String locationSelect;
    private CalendarDatabase calendarDatabase;
    private UserEntity loadedUser;
    private String userId;
    private UserDataBase db;
    private UserDao userDao;
    private static String CompareStartDate;
    private static String CompareEndDate;
    private String formattedStartDate;
    private String formattedEndDate;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashGetter.getHashKey(getApplicationContext());
        apiReader = new ApiReader();
        String apiKey = getResources().getString(R.string.api_key);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);


        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

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

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy MM dd");
        Date date = new Date();
        String todaydate = sdf2.format(date);

        LinearLayout vacationFestival = findViewById(R.id.main_vacation_festival_container);

        for (int images = 0; images < 5; images++) {
            List<HashMap<String, String>> festivalList = new ArrayList<>();
            View sliderItemView = getLayoutInflater().inflate(R.layout.slider_item, null);
            ImageView imageView = sliderItemView.findViewById(R.id.image_view);

            int finalImages = images;
            apiReader.Festivallit(apiKey, todaydate, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    //Log.d("main response", response);
                    ParsingApiData.parseXmlDataFromSearchKeyword(response);
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

                                        String title = festivalInfo.get("title");
                                        String id = festivalInfo.get("contentid");
                                        String repImage = festivalInfo.get("img");

                                        if (repImage == null || repImage.isEmpty()) {
                                            imageView.setImageResource(R.drawable.first_image_example);
                                        } else {
                                            Glide
                                                    .with(MainActivity.this)
                                                    .load(repImage)
                                                    .transform(new CenterCrop(), new RoundedCorners(30))
                                                    .placeholder(R.drawable.first_image_example)
                                                    .into(imageView);
                                        }
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

        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        };


        String[] mainFestivalArea = {"서울", "인천", "부산", "제주도"};
        String[] mainFestivalAreaCode = {"1", "2", "6", "39"};


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
        bottomNavigationView.setOnItemSelectedListener(item -> {
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

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );

                                layoutParams.setMargins(0, 0, 0, 40);
                                festivalBox.setLayoutParams(layoutParams);

                                String title = festivalInfo.get("title");
                                String id = festivalInfo.get("contentid");
                                String address1 = festivalInfo.get("addr1");
                                String repImage = festivalInfo.get("img");
                                //String overview = FestivalDetail(apiKey, id);
                                String startDateStr = festivalInfo.get("eventstartdate");
                                String endDateStr = festivalInfo.get("eventenddate");

                                MainActivity.CompareStartDate = startDateStr;
                                MainActivity.CompareEndDate = endDateStr;

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
                                //overviewTextView.setText(overview);
                                if (startDate != null && endDate != null) {
                                    if (currentDate.before(startDate)) {
                                        // 현재 날짜가 시작일 이전인 경우
                                        stateTextView.setText("진행예정");
                                        stateTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light)); // 주황색으로 설정
                                    } else if (currentDate.after(endDate)) {
                                        // 현재 날짜가 종료일 이후인 경우
                                        stateTextView.setText("종료됨");
                                        stateTextView.setTextColor(getResources().getColor(android.R.color.darker_gray)); // 회색으로 설정
                                    } else {
                                        // 현재 날짜가 시작일과 종료일 사이에 있는 경우
                                        stateTextView.setText("진행중");
                                        stateTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light)); // 초록색으로 설정
                                    }
                                }

                                if (repImage == null || repImage.isEmpty()) {
                                    searchImageButton.setImageResource(R.drawable.ic_image);
                                } else {
                                    Glide
                                            .with(MainActivity.this)
                                            .load(repImage)
                                            .transform(new CenterCrop(), new RoundedCorners(30))
                                            .placeholder(R.drawable.ic_image)
                                            .into(searchImageButton);
                                }
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

                                /*calendaraddButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        executor.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                                                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
                                                Date currentDate = new Date();
                                                String currentDateStr = sdf.format(currentDate);

                                                Log.d("startdate: ", CompareStartDate);
                                                Log.d("enddate: ", CompareEndDate);

                                                String startTime = "";
                                                String endTime = "";

                                                loadedUser = userDao.getUserInfoById(userId);
                                                if (loadedUser != null) {
                                                    // 이미 추가된지 여부 확인 추가예정
                                                    try {
                                                        Date compareDate = sdf.parse(CompareEndDate);
                                                        Date current = sdf.parse(currentDateStr);

                                                        if (current.compareTo(compareDate) <= 0) {
                                                            Log.d("Button Action", "Add about Festival to Calendar");

                                                            Date originalStartDate = sdf.parse(CompareStartDate);
                                                            String formattedStartDate = targetDateFormat.format(originalStartDate);
                                                            Date originalEndDate = sdf.parse(CompareEndDate);
                                                            String formattedEndDate = targetDateFormat.format(originalEndDate);

                                                            Log.d("formattedStartDate: ", formattedStartDate);
                                                            Log.d("formattedEndDate: ", formattedEndDate);

                                                            CalendarEntity newSchedule = new CalendarEntity();
                                                            newSchedule.title = title;
                                                            newSchedule.startDate = formattedStartDate;
                                                            newSchedule.endDate = formattedEndDate;
                                                            newSchedule.startTime = startTime;
                                                            newSchedule.endTime = endTime;
                                                            newSchedule.category = "#ed5c55";

                                                            ScheduleLoader loader = new ScheduleLoader(MainActivity.this, newSchedule, calendarDatabase.calendarDao());
                                                            loader.forceLoad();
                                                        } else {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), "이미 지난 축제입니다", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    Log.e("No UserInfo", "You should get your information in MyPage");
                                                }
                                            }
                                        });
                                    }
                                });*/
                            }

                        }
                    }
                });
            }
        });
    }

    private String FestivalDetail(String ApiKey, String contentID) {
        apiReader = new ApiReader();
        apiReader.FestivalInfo2(ApiKey, contentID, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                ParsingApiData.parseXmlDataFromDetailInfo2(response);
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                LinkedHashMap<String, String> firstMap = parsedFestivalList.get(0);

                detailInfo = firstMap.get("infotext");
                //Log.d("Festival Infotext: ", detailInfo);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        return detailInfo;
    }

    private void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_popup, null);
        Button cancelButton = dialogView.findViewById(R.id.dialog_popup_close_btn);
        Button confirmButton = dialogView.findViewById(R.id.dialog_popup_add_btn);
        // 시작 날짜 선택 버튼
        Button startdateClick = dialogView.findViewById(R.id.dialog_popup_start_date);
        Button starttimeClick = dialogView.findViewById(R.id.dialog_popup_start_time);
        Button enddateClick = dialogView.findViewById(R.id.dialog_popup_end_date);
        Button endtimeClick = dialogView.findViewById(R.id.dialog_popup_end_time);
        Button location = dialogView.findViewById(R.id.dialog_popup_location);

        DatePicker StartDatePicker = dialogView.findViewById(R.id.dialog_popup_StartDatePicker);
        TimePicker StartTimePicker = dialogView.findViewById(R.id.dialog_popup_StartTimePicker);
        DatePicker EndDatePicker = dialogView.findViewById(R.id.dialog_popup_EndDatePicker);
        TimePicker EndTimePicker = dialogView.findViewById(R.id.dialog_popup_EndTimePicker);


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
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        starttimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StartTimePicker의 가시성을 토글
                switch (StartTimePicker.getVisibility()) {
                    case View.VISIBLE:
                        StartTimePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        StartTimePicker.setVisibility(View.VISIBLE);
                        StartDatePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
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
                        StartTimePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        endtimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EndTimePicker의 가시성을 토글
                switch (EndTimePicker.getVisibility()) {
                    case View.VISIBLE:
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        EndTimePicker.setVisibility(View.VISIBLE);
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        //시작 시간-날짜 변화시
        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));

                        formattedStartDate = String.format("%04d%02d%02d", year, month + 1, day);
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

                        formattedEndDate = String.format("%04d%02d%02d", year, month + 1, day);
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
                String starttime = starttimeClick.getText().toString();
                String enddate = enddateClick.getText().toString();
                String endtime = endtimeClick.getText().toString();

                // 시작 및 종료 날짜-시간 문자열을 적절한 형식으로 변환
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                try {
                    Date startDate = sdf.parse(startdate + " " + starttime);
                    Date endDate = sdf.parse(enddate + " " + endtime);

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
