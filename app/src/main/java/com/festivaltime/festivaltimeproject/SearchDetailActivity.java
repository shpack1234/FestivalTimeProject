package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.festivaltime.festivaltimeproject.calendaract.ScheduleLoader;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.festivaltime.festivaltimeproject.userdatabasepackage.*;

public class SearchDetailActivity extends AppCompatActivity {

    private CalendarDatabase calendarDatabase;
    public ImageButton Back_Btn;
    private ApiReader apiReader;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private UserDataBase db;
    private UserDao userDao;
    private UserEntity loadedUser;
    private String userId;
    private static String CompareStartDate;
    private static String CompareEndDate;
    private Executor executor;

    private boolean isLoading = false;
    private int currentPage = 1; // 시작 페이지
    private final int ITEMS_PER_PAGE = 10; // 페이지당 아이템 수
    private String apiKey;
    private String query;
    private String type;
    ScrollView Scroll_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        Log.d(TAG, "hello");
        Back_Btn = findViewById(R.id.before_btn);
        Scroll_View = findViewById(R.id.scroll_view);
        executor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        type = getIntent().getStringExtra("type");
        query = getIntent().getStringExtra("query");
        apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();

        Bundle bundle = DataHolder.getInstance().getBundle();

        CompareStartDate = "";
        CompareEndDate = "";

        calendarDatabase = CalendarDatabase.getInstance(this);


        //날짜 서치인지 형태 확인
        String regex = "\\d{4}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);

        //날짜 서치인경우
        if (bundle != null) {
            String startDate = bundle.getString("startdate");
            String endDate = bundle.getString("enddate");
            String selectedLocation = bundle.getString("location");

            Log.d("startDate", startDate);
            Log.d("endD", endDate);
            Log.d("selLoc", selectedLocation);
            // {0/0/0] 형태로 ,,,,했음
            String[] queryArray = new String[3];
            queryArray[0] = selectedLocation;
            queryArray[1] = startDate;
            queryArray[2] = endDate;

            Log.d("queries: ", queryArray[0]);
            Log.d("queries: ", queryArray[1]);
            Log.d("queries: ", queryArray[2]);


            apiReader.searchFestival(apiKey, queryArray[0], queryArray[1], queryArray[2], new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    List<LinkedHashMap<String, String>> festivalResults = ParsingApiData.parseXmlDataFromSearchFestival2(response);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy MM dd");
                    Date date = new Date();
                    String todayDate = sdf2.format(date);
                    if (!type.isEmpty() && type.startsWith("A0207")) {
                        // type이 "A0207"로 시작하는 경우
                        apiReader.searchKeyword2(apiKey, todayDate, type, new ApiReader.ApiResponseListener() {
                            @Override
                            public void onSuccess(String response) {
                                handleApiResponse2(response,festivalResults);
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "API Error: " + error);
                            }
                        });
                    } else {
                        // 그 외의 경우
                        apiReader.searchKeyword(apiKey, todayDate, type, new ApiReader.ApiResponseListener() {
                            @Override
                            public void onSuccess(String response) {
                                handleApiResponse2(response,festivalResults);
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "API Error: " + error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
                }
            });
        }

        if (query != null ) {
            Matcher matcher = pattern.matcher(query);

            //키워드 서치
            if (!matcher.matches()) {
                Log.d("match", "date match fail");

                if (!type.isEmpty() && type.startsWith("A0207")) {
                    // type이 "A0207"로 시작하는 경우
                    apiReader.searchKeyword2(apiKey, query, type, new ApiReader.ApiResponseListener() {
                        @Override
                        public void onSuccess(String response) {
                            handleApiResponse(response);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "API Error: " + error);
                        }
                    });
                } else {
                    // 그 외의 경우
                    apiReader.searchKeyword(apiKey, query, type, new ApiReader.ApiResponseListener() {
                        @Override
                        public void onSuccess(String response) {
                            handleApiResponse(response);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "API Error: " + error);
                        }
                    });
                }
            }
        }





            Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(SearchDetailActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(SearchDetailActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(SearchDetailActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(SearchDetailActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(SearchDetailActivity.this);
                return true;
            }
        });


    }

    private void handleApiResponse2(String response, List<LinkedHashMap<String, String>> festivalResults) {

        Log.d("response", response);
         if(!type.isEmpty() && type.startsWith("A0207")){
            ParsingApiData.parseXmlDataFromSearchKeyword3(response, type, null);
        }
         else{
             ParsingApiData.parseXmlDataFromSearchKeyword3(response, null, type);
         }// 응답을 파싱하여 데이터를 저장

        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
        Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());

        // UI 갱신을 위한 작업을 executor를 사용하여 백그라운드 스레드에서 실행
        executor.execute(new Runnable() {
            @Override
            public void run() {
                festivalList.clear(); // 기존 데이터를 모두 제거
                festivalList.addAll(parsedFestivalList);

                for (LinkedHashMap<String, String> item : festivalResults) {
                    for (LinkedHashMap<String, String> parsedItem : parsedFestivalList) {
                        String title = item.get("title");
                        if (title != null && title.contains(query) && item.equals(parsedItem)) {
                            // title 값에 query가 포함되어 있고, item과 parsedItem이 같다면
                            festivalList.add(item); // festivalList에 추가
                        }
                    }
                }

                // UI 갱신은 메인 스레드에서 실행
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            // UI 갱신 코드
                        LinearLayout festivalContainer = findViewById(R.id.festival_container);
                        festivalContainer.removeAllViews();

                        String textToShow = getTextToShow(type);
                        TextView titleNameTextView = findViewById(R.id.Entire_view_title);
                        titleNameTextView.setText(textToShow);

                        for (HashMap<String, String> festivalInfo : festivalList) {
                            View festivalInfoBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
                            TextView titleTextView = festivalInfoBox.findViewById(R.id.festival_title);
                            TextView locationTextView = festivalInfoBox.findViewById(R.id.festival_location);
                            TextView idTextView = festivalInfoBox.findViewById(R.id.festival_overview);
                            ImageButton festivalRepImage = festivalInfoBox.findViewById(R.id.festival_rep_image);
                            ImageButton calendaraddButton = festivalInfoBox.findViewById(R.id.calendar_addButton);
                            ImageButton favoriteaddButton = festivalInfoBox.findViewById(R.id.favorite_addButton);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(0, 0, 0, 40);
                            festivalInfoBox.setLayoutParams(layoutParams);

                            String title = festivalInfo.get("title");
                            String location = festivalInfo.get("address");
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            titleTextView.setText(title);
                            locationTextView.setText(location);
                            idTextView.setText(id);

                            Log.d(TAG, "Rep Image URL: " + repImage);
                            if (repImage == null || repImage.isEmpty()) {
                                festivalRepImage.setImageResource(R.drawable.ic_image);
                            } else {
                                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
                                Glide
                                        .with(SearchDetailActivity.this)
                                        .load(repImage)
                                        .transform(new CenterCrop(), new RoundedCorners(30))
                                        .placeholder(R.drawable.ic_image)
                                        .into(festivalRepImage);
                            }
                            apiReader.detailIntro(apiKey, id, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    // ApiReader에서 가져온 데이터를 ParsingApiData를 사용하여 파싱
                                    ParsingApiData.parseXmlDataFromdetailIntro(response);

                                    // 축제의 시작일과 종료일 가져오기
                                    List<LinkedHashMap<String, String>> detailIntroList = ParsingApiData.getFestivalList();
                                    if (!detailIntroList.isEmpty()) {
                                        LinkedHashMap<String, String> introInfo = detailIntroList.get(0); // 첫 번째 항목 가져오기

                                        String startDateStr = introInfo.get("eventstartdate");
                                        String endDateStr = introInfo.get("eventenddate");
                                        SearchDetailActivity.CompareStartDate = startDateStr;
                                        SearchDetailActivity.CompareEndDate = endDateStr;

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

                                        // 축제의 상태를 판단하여 텍스트뷰에 설정
                                        TextView stateTextView = festivalInfoBox.findViewById(R.id.festival_state);
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
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "API Error: " + error);
                                }
                            });
                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = idTextView.getText().toString();
                                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                    navigateToDetailFestivalActivity(SearchDetailActivity.this, contentId);
                                }
                            });

                            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Button Listener", "addBtn");
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String contentId = idTextView.getText().toString();
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

                                                        ScheduleLoader loader = new ScheduleLoader(SearchDetailActivity.this, newSchedule, calendarDatabase.calendarDao());
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



                            Scroll_View.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                @Override
                                public void onScrollChanged() {
                                    if (!isLoading) {
                                        // 현재 스크롤 위치
                                        int scrollY = Scroll_View.getScrollY();
                                        // 현재 보여지는 영역의 높이
                                        int visibleHeight = Scroll_View.getHeight();
                                        // 스크롤 전체 영역의 높이
                                        int totalHeight = Scroll_View.getChildAt(0).getHeight();

                                        // 마지막에 도달했는지 체크
                                        if (scrollY + visibleHeight >= totalHeight && festivalList.size() % ITEMS_PER_PAGE == 0) {
                                            // 추가 데이터 로드
                                            loadMoreData();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    private void handleApiResponse(String response) {

        Log.d("response", response);
        ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장

        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
        Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());

        // UI 갱신을 위한 작업을 executor를 사용하여 백그라운드 스레드에서 실행
        executor.execute(new Runnable() {
            @Override
            public void run() {
                festivalList.clear(); // 기존 데이터를 모두 제거
                festivalList.addAll(parsedFestivalList);

                // UI 갱신은 메인 스레드에서 실행
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // UI 갱신 코드
                        LinearLayout festivalContainer = findViewById(R.id.festival_container);
                        festivalContainer.removeAllViews();

                        String textToShow = getTextToShow(type);
                        TextView titleNameTextView = findViewById(R.id.Entire_view_title);
                        titleNameTextView.setText(textToShow);

                        for (HashMap<String, String> festivalInfo : festivalList) {
                            View festivalInfoBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
                            TextView titleTextView = festivalInfoBox.findViewById(R.id.festival_title);
                            TextView locationTextView = festivalInfoBox.findViewById(R.id.festival_location);
                            TextView idTextView = festivalInfoBox.findViewById(R.id.festival_overview);
                            ImageButton festivalRepImage = festivalInfoBox.findViewById(R.id.festival_rep_image);
                            ImageButton calendaraddButton = festivalInfoBox.findViewById(R.id.calendar_addButton);
                            ImageButton favoriteaddButton = festivalInfoBox.findViewById(R.id.favorite_addButton);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(0, 0, 0, 40);
                            festivalInfoBox.setLayoutParams(layoutParams);

                            String title = festivalInfo.get("title");
                            String location = festivalInfo.get("address");
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            titleTextView.setText(title);
                            locationTextView.setText(location);
                            idTextView.setText(id);

                            Log.d(TAG, "Rep Image URL: " + repImage);
                            if (repImage == null || repImage.isEmpty()) {
                                festivalRepImage.setImageResource(R.drawable.ic_image);
                            } else {
                                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
                                Glide
                                        .with(SearchDetailActivity.this)
                                        .load(repImage)
                                        .transform(new CenterCrop(), new RoundedCorners(30))
                                        .placeholder(R.drawable.ic_image)
                                        .into(festivalRepImage);
                            }
                            apiReader.detailIntro(apiKey, id, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    // ApiReader에서 가져온 데이터를 ParsingApiData를 사용하여 파싱
                                    ParsingApiData.parseXmlDataFromdetailIntro(response);

                                    // 축제의 시작일과 종료일 가져오기
                                    List<LinkedHashMap<String, String>> detailIntroList = ParsingApiData.getFestivalList();
                                    if (!detailIntroList.isEmpty()) {
                                        LinkedHashMap<String, String> introInfo = detailIntroList.get(0); // 첫 번째 항목 가져오기

                                        String startDateStr = introInfo.get("eventstartdate");
                                        String endDateStr = introInfo.get("eventenddate");
                                        CompareStartDate = startDateStr;
                                        CompareEndDate = endDateStr;

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

                                        // 축제의 상태를 판단하여 텍스트뷰에 설정
                                        TextView stateTextView = festivalInfoBox.findViewById(R.id.festival_state);
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
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "API Error: " + error);
                                }
                            });
                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = idTextView.getText().toString();
                                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                    navigateToDetailFestivalActivity(SearchDetailActivity.this, contentId);
                                }
                            });

                            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Button Listener", "addBtn");
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String contentId = idTextView.getText().toString();
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

                                            // 키워드 서치 날짜 미포함으로 전체 주석처리해놓음.
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

                                                        ScheduleLoader loader = new ScheduleLoader(SearchDetailActivity.this, newSchedule, calendarDatabase.calendarDao());
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



                            Scroll_View.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                @Override
                                public void onScrollChanged() {
                                    if (!isLoading) {
                                        // 현재 스크롤 위치
                                        int scrollY = Scroll_View.getScrollY();
                                        // 현재 보여지는 영역의 높이
                                        int visibleHeight = Scroll_View.getHeight();
                                        // 스크롤 전체 영역의 높이
                                        int totalHeight = Scroll_View.getChildAt(0).getHeight();

                                        // 마지막에 도달했는지 체크
                                        if (scrollY + visibleHeight >= totalHeight && festivalList.size() % ITEMS_PER_PAGE == 0) {
                                            // 추가 데이터 로드
                                            loadMoreData();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    //loadMoreData 메소드
    private void loadMoreData() {
        isLoading = true; // 로딩 상태 설정

        if (!type.isEmpty() && type.startsWith("A0207")) {
            // type이 "A0207"로 시작하는 경우
            apiReader.searchKeyword2(apiKey, query, type, currentPage + 1, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    handleLoadMoreApiResponse(response);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
                    isLoading = false; // 로딩 상태 해제
                }
            });
        } else {
            // 그 외의 경우
            apiReader.searchKeyword(apiKey, query, type, currentPage + 1, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    handleLoadMoreApiResponse(response);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
                    isLoading = false; // 로딩 상태 해제
                }
            });
        }
    }

    private void handleLoadMoreApiResponse(String response) {
        ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                // 데이터가 없으면 로딩 상태 해제 후 종료
                if (parsedFestivalList.isEmpty()) {
                    isLoading = false;
                    return;
                }

                // UI 갱신은 메인 스레드에서 실행
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout festivalContainer = findViewById(R.id.festival_container);

                        for (HashMap<String, String> festivalInfo : parsedFestivalList) {
                            View festivalInfoBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
                            TextView titleTextView = festivalInfoBox.findViewById(R.id.festival_title);
                            TextView locationTextView = festivalInfoBox.findViewById(R.id.festival_location);
                            TextView idTextView = festivalInfoBox.findViewById(R.id.festival_overview);
                            ImageButton festivalRepImage = festivalInfoBox.findViewById(R.id.festival_rep_image);
                            ImageButton calendaraddButton = festivalInfoBox.findViewById(R.id.calendar_addButton);
                            ImageButton favoriteaddButton = festivalInfoBox.findViewById(R.id.favorite_addButton);

                            String title = festivalInfo.get("title");
                            String location = festivalInfo.get("address");
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            titleTextView.setText(title);
                            locationTextView.setText(location);
                            idTextView.setText(id);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(0, 0, 0, 40); // 40dp 간격 설정
                            festivalInfoBox.setLayoutParams(layoutParams);

                            Log.d(TAG, "Rep Image URL: " + repImage);
                            if (repImage == null || repImage.isEmpty()) {
                                festivalRepImage.setImageResource(R.drawable.ic_image);
                            } else {
                                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
                                Glide
                                        .with(SearchDetailActivity.this)
                                        .load(repImage)
                                        .transform(new CenterCrop(), new RoundedCorners(30))
                                        .placeholder(R.drawable.ic_image)
                                        .into(festivalRepImage);
                            }
                            festivalList.add(festivalInfo);
                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = idTextView.getText().toString();
                                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                    navigateToDetailFestivalActivity(SearchDetailActivity.this, contentId);
                                }
                            });
                            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Button Listener", "addBtn");
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String contentId = idTextView.getText().toString();
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
                        }

                        // 다음 페이지 로드를 위해 currentPage 증가
                        currentPage++;
                        isLoading = false; // 로딩 상태 해제
                    }
                });
            }
        });
    }

    private String getTextToShow(String type) {
        switch (type) {
            case "A02080100":
                return "전통공연";
            case "A02080200":
                return "연극";
            case "A02080300":
                return "뮤지컬";
            case "A02080400":
                return "오페라";
            case "A02080500":
                return "전시회";
            case "A02080600":
                return "박람회";
            case "A02080800":
                return "무용";
            case "A02080900":
                return "클래식음악회";
            case "A02081000":
                return "대중콘서트";
            case "A02081100":
                return "영화";
            default:
                if (!type.isEmpty() && type.startsWith("A0207")) {
                    return "축제";
                } else {
                    return "기타";
                }
        }
    }

    private class haredPreferences {
    }
}
