package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.festivaltime.festivaltimeproject.calendaract.ScheduleLoader;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EntireViewActivity extends AppCompatActivity {
    public ImageButton Back_Btn;
    private ApiReader apiReader;
    private Executor executor;
    private UserDataBase db;
    private UserDao userDao;
    private UserEntity loadedUser;
    private String userId;


    private List<HashMap<String, String>> festivalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entire_view);
        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Back_Btn=findViewById(R.id.before_btn);

        executor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        String contentId = getIntent().getStringExtra("contentid");

        String apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();
        apiReader.detailCommon(apiKey, contentId, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromDetailCommon(response); // 응답을 파싱하여 데이터를 저장

                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() { //UI 추가 부분
                        festivalList.clear(); // 기존 데이터를 모두 제거
                        festivalList.addAll(parsedFestivalList);
                        for (HashMap<String, String> festivalInfo : festivalList) {
                            TextView titleTextView = findViewById(R.id.festival_title);
                            TextView address = findViewById(R.id.festival_address);
                            ImageView festivalFirstImage = findViewById(R.id.festival_firstimage);
                            TextView overviewText = findViewById(R.id.festival_overview);
                            ImageButton favoriteaddButton = findViewById(R.id.favorite_addButton);
                            ImageButton calendaraddButton = findViewById(R.id.calendar_addButton);

                            String title = festivalInfo.get("title");
                            String address1 = festivalInfo.get("address1");
                            String address2 = festivalInfo.get("address2");
                            String id = festivalInfo.get("contentid");
                            String firstImage = festivalInfo.get("img");
                            String overview = festivalInfo.get("overview");
                            final String[] finalstartDate = {null};
                            final String[] finalendDate = {null};

                            Log.d("imgUrl", firstImage);
                            if (firstImage == null || firstImage.isEmpty()) {
                                festivalFirstImage.setImageResource(R.drawable.ic_image);
                            } else {
                                Glide
                                        .with(EntireViewActivity.this)
                                        .load(firstImage)
                                        .transform(new CenterCrop())
                                        .placeholder(R.drawable.ic_image)
                                        .into(festivalFirstImage);
                            }

                            titleTextView.setText(title);
                            address.setText(address1 + " " + address2);
                            overviewText.setText(overview);


                            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Button Listener", "addBtn");
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String contentId = overviewText.getText().toString();
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

                            apiReader.detailIntro(apiKey, contentId, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    Log.d("response", response);
                                    ParsingApiData.parseXmlDataFromdetailIntro(response);

                                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                                    Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            festivalList.clear();
                                            festivalList.addAll(parsedFestivalList);
                                            for (HashMap<String, String> festivalInfo : festivalList) {
                                                String eventStartDate = festivalInfo.get("eventstartdate");
                                                String eventEndDate = festivalInfo.get("eventenddate");
                                                finalstartDate[0] = festivalInfo.get("eventstartdate");
                                                finalendDate[0] = festivalInfo.get("eventenddate");

                                                try {
                                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

                                                    Date startDate = inputFormat.parse(eventStartDate);
                                                    Date endDate = inputFormat.parse(eventEndDate);

                                                    String formattedStartDate = outputFormat.format(startDate);
                                                    String formattedEndDate = outputFormat.format(endDate);

                                                    TextView startDateTextView = findViewById(R.id.start_date);
                                                    TextView endDateTextView = findViewById(R.id.end_date);

                                                    startDateTextView.setText(formattedStartDate);
                                                    endDateTextView.setText(formattedEndDate);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(String error) {
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

                                                String CompareStartDate = finalstartDate[0];
                                                String CompareEndDate = finalendDate[0];

                                                Log.d("startdate: ", "Start Date: " + CompareStartDate);
                                                Log.d("enddate: ", "End Date: " + CompareEndDate);

                                                String startTime = "";
                                                String endTime = "";

                                                loadedUser = userDao.getUserInfoById(userId);
                                                if (loadedUser != null) {
                                                    Date compareDate = sdf.parse(CompareEndDate);
                                                    Date current = sdf.parse(currentDateStr);

                                                    if (current.compareTo(compareDate) <= 0) {
                                                        Log.d("Button Action", "Add about Festival to Calendar");

                                                        Date originalStartDate = sdf.parse(CompareStartDate);
                                                        String formattedStartDate = targetDateFormat.format(originalStartDate);
                                                        Date originalEndDate = sdf.parse(CompareEndDate);
                                                        String formattedEndDate = targetDateFormat.format(originalEndDate);

                                                        Log.d("formattedStartDate: ", "Formatted Start Date: " + formattedStartDate);
                                                        Log.d("formattedEndDate: ", "Formatted End Date: " + formattedEndDate);

                                                        // 데이터베이스 초기화
                                                        CalendarDatabase calendarDatabase = Room.databaseBuilder(getApplicationContext(),
                                                                CalendarDatabase.class, "calendar-database").build();

                                                        // DAO 가져오기
                                                        CalendarDao calendarDao = calendarDatabase.calendarDao();

                                                        CalendarEntity newSchedule = new CalendarEntity();
                                                        newSchedule.title = title;
                                                        newSchedule.startDate = formattedStartDate;
                                                        newSchedule.endDate = formattedEndDate;
                                                        newSchedule.startTime = startTime;
                                                        newSchedule.endTime = endTime;
                                                        newSchedule.category = "#ed5c55";
                                                        newSchedule.contentid = id;

                                                        ScheduleLoader loader = new ScheduleLoader(EntireViewActivity.this, newSchedule, calendarDao);
                                                        loader.forceLoad();
                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "이미 지난 축제입니다", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    Log.e("No UserInfo", "You should get your information in MyPage");
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
                });
            }

            @Override
            public void onError(String error) {

            }
        });


        Intent intent = getIntent();
        if(intent != null) {
            String content = intent.getStringExtra("content");
            if(content != null){
                TextView entireContent = findViewById(R.id.festival_title);
                entireContent.setText(content);
            }
        }

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   onBackPressed(); }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(EntireViewActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(EntireViewActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(EntireViewActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(EntireViewActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(EntireViewActivity.this);
                return true;
            }
        });
    }


}
