package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.festivaltime.festivaltimeproject.calendaract.ScheduleLoader;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
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
    public ImageButton Back_Btn;
    private ApiReader apiReader;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private UserDataBase db;
    private UserDao userDao;
    private UserEntity loadedUser;
    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    private String userId;
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

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Log.d(TAG, "hello");
        Back_Btn = findViewById(R.id.before_btn);
        Scroll_View = findViewById(R.id.scroll_view);
        executor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
        calendarDao = calendarDatabase.calendarDao();

        type = getIntent().getStringExtra("type");
        query = getIntent().getStringExtra("query");
        apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();

        Bundle bundle = DataHolder.getInstance().getBundle();


        calendarDatabase = CalendarDatabase.getInstance(this);


        //날짜 서치인지 형태 확인
        String regex = "\\d{4}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);

        //
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
                                handleApiResponse2(response, festivalResults);
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
                                handleApiResponse2(response, festivalResults);
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

        if (query != null) {
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
            //달력 검색인 경우
            else {
                Log.d("match", "date match success");
                if (!type.isEmpty() && type.startsWith("A0207")) {
                    apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                        public void onSuccess(String response) {
                            handleApiResponse3(response);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "API Error: " + error);
                        }
                    });
                } else {
                    apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                        public void onSuccess(String response) {
                            handleApiResponse3(response);
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

    private void handleApiResponse3(String response) {
        if (!type.isEmpty() && type.startsWith("A0207")) {
            ParsingApiData.parseXmlDataFromFestival(response);
        } else {
            ParsingApiData.parseXmlDataFromFestival(response, type);
        }// 응답을 파싱하여 데이터를 저장

        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
        // UI 갱신을 위한 작업을 executor를 사용하여 백그라운드 스레드에서 실행
        executor.execute(new Runnable() {
            @Override
            public void run() {
                festivalList.clear(); // 기존 데이터를 모두 제거
                festivalList.addAll(parsedFestivalList);

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
                            TextView overviewTextView = festivalInfoBox.findViewById(R.id.festival_overview);
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
                            //문자열길이 일정수 넘어가면 ...형태로 표시
                            if (location != null && location.length() > 15) {
                                location = location.substring(0, 15) + "...";
                            }
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            String startDateStr = festivalInfo.get("eventstartdate");
                            String endDateStr = festivalInfo.get("eventenddate");
                            titleTextView.setText(title);
                            locationTextView.setText(location);

                            if (repImage == null || repImage.isEmpty()) {
                                festivalRepImage.setImageResource(R.drawable.ic_image);
                            } else {
                                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
                                Glide.with(SearchDetailActivity.this)
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
                            apiReader.FestivalInfo2(apiKey, id, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    ParsingApiData.parseXmlDataFromDetailInfo2(response);
                                    //Log.d("festivalinfo response: ", response);
                                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                                    LinkedHashMap<String, String> firstMap = parsedFestivalList.get(0);

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
                                    Log.e(MotionEffect.TAG, "API Error: " + error);
                                }
                            });

                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = id;
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
                                            String contentId = id;
                                            loadedUser = userDao.getUserInfoById(userId);
                                            if (loadedUser != null) {
                                                if (loadedUser.getIsLogin()) {
                                                    if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                                        Log.d("Festival Id", contentId);
                                                        Log.d("Button Listener", "ID already exists in userFavoriteFestival");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "이미 추가된 축제입니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Log.d("Festival Id", contentId);
                                                        loadedUser.addUserFavoriteFestival(contentId);
                                                        userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.e("No UserInfo", "You should get your information in MyPage");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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
                                                                                AddFT dialog = new AddFT(SearchDetailActivity.this, title, id, formattedStartDate, formattedEndDate);
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


                            // 스크롤 이벤트 리스너 설정
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
                                        if (scrollY + visibleHeight >= totalHeight) {
                                            // 추가 데이터 로드
                                            currentPage++; // 다음 페이지 로드를 위해 currentPage 증가
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

    private void handleApiResponse2(String response, List<LinkedHashMap<String, String>> festivalResults) {

        Log.d("response", response);
        if (!type.isEmpty() && type.startsWith("A0207")) {
            ParsingApiData.parseXmlDataFromSearchKeyword3(response, type, null);
        } else {
            ParsingApiData.parseXmlDataFromSearchKeyword3(response, null, type);
        }// 응답을 파싱하여 데이터를 저장

        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
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
                            TextView overviewTextView = festivalInfoBox.findViewById(R.id.festival_overview);
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
                            //문자열길이 일정수 넘어가면 ...형태로 표시
                            if (location != null && location.length() > 15) {
                                location = location.substring(0, 15) + "...";
                            }
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            final String[] finalstartDate = {null};
                            final String[] finalendDate = {null};
                            titleTextView.setText(title);
                            locationTextView.setText(location);

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
                                        finalstartDate[0] = introInfo.get("eventstartdate");
                                        finalendDate[0] = introInfo.get("eventenddate");

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

                            apiReader.FestivalInfo2(apiKey, id, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    ParsingApiData.parseXmlDataFromDetailInfo2(response);
                                    //Log.d("festivalinfo response: ", response);
                                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                                    LinkedHashMap<String, String> firstMap = parsedFestivalList.get(0);

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
                                    Log.e(MotionEffect.TAG, "API Error: " + error);
                                }
                            });

                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = id;
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
                                            String contentId = id;
                                            loadedUser = userDao.getUserInfoById(userId);
                                            if (loadedUser != null) {
                                                if (loadedUser.getIsLogin()) {
                                                    if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                                        Log.d("Festival Id", contentId);
                                                        Log.d("Button Listener", "ID already exists in userFavoriteFestival");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "이미 추가된 축제입니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Log.d("Festival Id", contentId);
                                                        loadedUser.addUserFavoriteFestival(contentId);
                                                        userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.e("No UserInfo", "You should get your information in MyPage");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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

                                                String CompareStartDate = finalstartDate[0];
                                                String CompareEndDate = finalendDate[0];

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
                                                                                AddFT dialog = new AddFT(SearchDetailActivity.this, title, id, formattedStartDate, formattedEndDate);
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


                            // 스크롤 이벤트 리스너 설정
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
                                        if (scrollY + visibleHeight >= totalHeight) {
                                            // 추가 데이터 로드
                                            currentPage++; // 다음 페이지 로드를 위해 currentPage 증가
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
                            TextView overviewTextView = festivalInfoBox.findViewById(R.id.festival_overview);
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
                            //문자열길이 일정수 넘어가면 ...형태로 표시
                            if (location != null && location.length() > 15) {
                                location = location.substring(0, 15) + "...";
                            }
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            final String[] finalstartDate = {null};
                            final String[] finalendDate = {null};
                            titleTextView.setText(title);
                            locationTextView.setText(location);

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
                                        finalstartDate[0] = introInfo.get("eventstartdate");
                                        finalendDate[0] = introInfo.get("eventenddate");

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

                            apiReader.FestivalInfo2(apiKey, id, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    ParsingApiData.parseXmlDataFromDetailInfo2(response);
                                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                                    LinkedHashMap<String, String> firstMap = null;

                                    try {
                                        firstMap = parsedFestivalList.get(0);
                                        String detailInfo = firstMap.get("infotext");

                                        //문자열길이 일정수 넘어가면 ...형태로 표시
                                        if (detailInfo != null && detailInfo.length() > 30) {
                                            detailInfo = detailInfo.substring(0, 30) + "...";
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
                                    } catch (IndexOutOfBoundsException e) {
                                    }



                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(MotionEffect.TAG, "API Error: " + error);
                                }
                            });
                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = id;
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
                                            String contentId = id;
                                            loadedUser = userDao.getUserInfoById(userId);
                                            if (loadedUser != null) {
                                                if (loadedUser.getIsLogin()) {
                                                    if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                                        Log.d("Festival Id", contentId);
                                                        Log.d("Button Listener", "ID already exists in userFavoriteFestival");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "이미 추가된 축제입니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Log.d("Festival Id", contentId);
                                                        loadedUser.addUserFavoriteFestival(contentId);
                                                        userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.e("No UserInfo", "You should get your information in MyPage");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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

                                                String CompareStartDate = finalstartDate[0];
                                                String CompareEndDate = finalendDate[0];

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
                                                                                AddFT dialog = new AddFT(SearchDetailActivity.this, title, id, formattedStartDate, formattedEndDate);
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


                            // 스크롤 이벤트 리스너 설정
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
                                        if (scrollY + visibleHeight >= totalHeight) {
                                            // 추가 데이터 로드
                                            currentPage++; // 다음 페이지 로드를 위해 currentPage 증가
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
        currentPage++;
        isLoading = true; // 로딩 상태 설정

        if (!type.isEmpty() && type.startsWith("A0207")) {
            // type이 "A0207"로 시작하는 경우
            apiReader.searchKeyword2(apiKey, query, type, currentPage, new ApiReader.ApiResponseListener() {
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
            apiReader.searchKeyword(apiKey, query, type, currentPage, new ApiReader.ApiResponseListener() {
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
                            TextView overviewTextView = festivalInfoBox.findViewById(R.id.festival_overview);
                            ImageButton festivalRepImage = festivalInfoBox.findViewById(R.id.festival_rep_image);
                            ImageButton calendaraddButton = festivalInfoBox.findViewById(R.id.calendar_addButton);
                            ImageButton favoriteaddButton = festivalInfoBox.findViewById(R.id.favorite_addButton);

                            String title = festivalInfo.get("title");
                            String location = festivalInfo.get("address");
                            //문자열길이 일정수 넘어가면 ...형태로 표시
                            if (location != null && location.length() > 15) {
                                location = location.substring(0, 15) + "...";
                            }
                            String id = festivalInfo.get("contentid");
                            String repImage = festivalInfo.get("img");
                            final String[] finalstartDate = {null};
                            final String[] finalendDate = {null};
                            titleTextView.setText(title);
                            locationTextView.setText(location);


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
                                        finalstartDate[0] = introInfo.get("eventstartdate");
                                        finalendDate[0] = introInfo.get("eventenddate");

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

                            apiReader.FestivalInfo2(apiKey, id, new ApiReader.ApiResponseListener() {
                                @Override
                                public void onSuccess(String response) {
                                    ParsingApiData.parseXmlDataFromDetailInfo2(response);
                                    //Log.d("festivalinfo response: ", response);
                                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                                    LinkedHashMap<String, String> firstMap = parsedFestivalList.get(0);

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
                                    Log.e(MotionEffect.TAG, "API Error: " + error);
                                }
                            });
                            festivalList.add(festivalInfo);
                            festivalContainer.addView(festivalInfoBox);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = id;
                                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                    navigateToDetailFestivalActivity(SearchDetailActivity.this, contentId);
                                }
                            });
                            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String contentId = id;
                                            loadedUser = userDao.getUserInfoById(userId);
                                            if (loadedUser != null) {
                                                if (loadedUser.getIsLogin()) {
                                                    if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                                        Log.d("Festival Id", contentId);
                                                        Log.d("Button Listener", "ID already exists in userFavoriteFestival");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "이미 추가된 축제입니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Log.d("Festival Id", contentId);
                                                        loadedUser.addUserFavoriteFestival(contentId);
                                                        userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
                                                    }
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.e("No UserInfo", "You should get your information in MyPage");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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

                                                String CompareStartDate = finalstartDate[0];
                                                String CompareEndDate = finalendDate[0];

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
                                                                                AddFT dialog = new AddFT(SearchDetailActivity.this, title, id, formattedStartDate, formattedEndDate);
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

}
