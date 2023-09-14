package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.festivaltime.festivaltimeproject.calendaract.ScheduleLoader;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.festivaltime.festivaltimeproject.festivaldatabasepackage.*;
//import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoriteActivity extends AppCompatActivity {
    private ApiReader apiReader;

    private UserDataBase db;

    private UserDao userDao;

    private UserEntity loadedUser;
    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    private String userId;
    private Executor executor;

    List<LinkedHashMap<String, String>> festivalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        String apiKey = getResources().getString(R.string.api_key);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
        calendarDao = calendarDatabase.calendarDao();

        executor = Executors.newSingleThreadExecutor();

        TextView textView = findViewById(R.id.no_info_msg);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);
                if (loadedUser == null || loadedUser.getIsLogin() == false) {
                    Log.e("Err", "No UserInfo");
                    textView.setText("로그인 후 이용 가능합니다.");
                } else {
                    List<String> favoriteFestivals = loadedUser.getUserFavoriteFestival();
                    
                    //찜 축제 없음
                    if (favoriteFestivals.size() == 0) {
                        textView.setText("찜 축제를 추가하고 원하는 축제를 편하게 찾아보세요!");
                    } else {   //찜 축제 있음
                        for (String festivalId : favoriteFestivals) {
                            Log.d("Favorite Festival", festivalId);
                        }

                        List<LinkedHashMap<String, String>> parsedFestivalList = new ArrayList<>();

                        for (String contentId : loadedUser.getUserFavoriteFestival()) {
                            apiReader = new ApiReader();
                            apiReader.detailCommon(apiKey, contentId, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    ParsingApiData.parseXmlDataFromDetailCommon(response); // 응답을 파싱하여 데이터를 저장
                                    List<LinkedHashMap<String, String>> festivalList = ParsingApiData.getFestivalList();
                                    synchronized (parsedFestivalList) {
                                        parsedFestivalList.addAll(festivalList);
                                    }

                                    // 모든 API 응답 처리가 완료된 후에 UI를 업데이트
                                    if (parsedFestivalList.size() == favoriteFestivals.size()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateUI(parsedFestivalList);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        }
                    }
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);  //하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_favorite);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(FavoriteActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(FavoriteActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(FavoriteActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(FavoriteActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(FavoriteActivity.this);
                return true;
            }
        });

    }

    private void updateUI(List<LinkedHashMap<String, String>> festivalList) {    //축제 박스 UI 추가 메소드
        LinearLayout festivalContainer = findViewById(R.id.festival_container);
        festivalContainer.removeAllViews();
        String apiKey = getResources().getString(R.string.api_key);

        for (LinkedHashMap<String, String> festivalInfo : festivalList) {   //API 파싱한 데이터를 해시맵 리스트에 저장한 뒤 불러옴
            View favoriteInfoBox = getLayoutInflater().inflate(R.layout.favorite_info_box, null);
            TextView titleTextView = favoriteInfoBox.findViewById(R.id.festival_title);
            TextView locationTextView = favoriteInfoBox.findViewById(R.id.festival_location);
            TextView idTextView = favoriteInfoBox.findViewById(R.id.festival_overview);
            ImageButton festivalRepImage = favoriteInfoBox.findViewById(R.id.festival_rep_image);
            ImageButton deleteButton = favoriteInfoBox.findViewById(R.id.festival_deleteButton);
            ImageButton calendaraddButton = favoriteInfoBox.findViewById(R.id.festival_calButton);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 40);
            favoriteInfoBox.setLayoutParams(layoutParams);

            String title = festivalInfo.get("title");
            String location = festivalInfo.get("address");
            String id = festivalInfo.get("contentid");
            String repImage = festivalInfo.get("img");
            String overview = festivalInfo.get("overview");
            final String[] finalstartDate = {null};
            final String[] finalendDate = {null};
            titleTextView.setText(title);
            locationTextView.setText(location);
            idTextView.setText(overview);

            if (repImage == null || repImage.isEmpty()) {
                festivalRepImage.setImageResource(R.drawable.ic_image);
            } else {
                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
            }
            festivalContainer.addView(favoriteInfoBox);

            favoriteInfoBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 시 contentid 값을 가져오는 작업 수행
                    String contentId = idTextView.getText().toString();
                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                    navigateToDetailFestivalActivity(FavoriteActivity.this, contentId);
                }
            });

            apiReader.detailIntro(apiKey, id, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    // ApiReader에서 가져온 데이터를 ParsingApiData를 사용하여 파싱
                    ParsingApiData.parseXmlDataFromdetailIntro(response);

                    // 축제의 시작일과 종료일 가져오기
                    List<LinkedHashMap<String, String>> detailIntroList = ParsingApiData.getFestivalList();
                    if (!detailIntroList.isEmpty()) {
                        LinkedHashMap<String, String> introInfo = detailIntroList.get(0); // 첫 번째 항목 가져오기

                        finalstartDate[0] = introInfo.get("eventstartdate");
                        finalendDate[0] = introInfo.get("eventenddate");
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
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
                                if (loadedUser != null || loadedUser.getIsLogin() == false) {
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

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Button Listener", "deleteBtn");
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            String contentId = id;
                            loadedUser = userDao.getUserInfoById(userId);
                            if (loadedUser != null || loadedUser.getIsLogin() == false) {
                                if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                    loadedUser.deleteUserFavoriteFestival(contentId);
                                    userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            festivalContainer.removeView(favoriteInfoBox);
                                        }
                                    });
                                }
                            } else {
                                Log.e("No UserInfo", "You should get your information in MyPage");
                            }
                        }
                    });
                }
            });


        }
    }
}