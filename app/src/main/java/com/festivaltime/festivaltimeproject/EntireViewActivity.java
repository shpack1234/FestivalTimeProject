package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    private String userId;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private List<HashMap<String, String>> samedaylist = new ArrayList<>();

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

        Back_Btn = findViewById(R.id.before_btn);

        executor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
        calendarDao = calendarDatabase.calendarDao();

        String contentId = getIntent().getStringExtra("contentid");
        LinearLayout samedayFestival = findViewById(R.id.entire_sameday_festival_container);

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
                            String addresstext = "";

                            //주소 null시
                            if ((address1 == null || address1.contains("null")) &&
                                    ((address2 == null || address2.contains("null")))) {
                                addresstext = getString(R.string.null_text);
                            } else {
                                addresstext = address1 + " " + address2;
                            }
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
                            address.setText(addresstext);
                            //html 형태 변환하여 setText
                            if (overview != null) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    overviewText.setText(Html.fromHtml(overview, Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    overviewText.setText(Html.fromHtml(overview));
                                }
                            } else {
                                // detailInfo가 null인 경우에 대한 처리 추가
                                overviewText.setText(getString(R.string.null_text));
                            }


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
                                                                                AddFT dialog = new AddFT(EntireViewActivity.this, title, id, formattedStartDate, formattedEndDate);
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
                });
            }

            @Override
            public void onError(String error) {

            }
        });

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String todaydate = sdf2.format(date);

        apiReader.Festivallit2(apiKey, todaydate, todaydate, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("entire startdate", todaydate);
                Log.d("entire enddate", todaydate);
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromFestival(response);
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        samedaylist.clear();
                        samedaylist.addAll(parsedFestivalList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (samedaylist.size() > 0) {
                                    for (int i = 0; i < 5; i++) {
                                        View sliderItemView = getLayoutInflater().inflate(R.layout.slider_item, null);
                                        ImageButton imageButton = sliderItemView.findViewById(R.id.image_button);
                                        TextView datetext = sliderItemView.findViewById(R.id.date_text);

                                        HashMap<String, String> samedayInfo = samedaylist.get(i);

                                        String contentid = samedayInfo.get("contentid");
                                        String img = samedayInfo.get("img");
                                        String startdate = samedayInfo.get("eventstartdate");
                                        String enddate = samedayInfo.get("eventenddate");

                                        if (img == null || img.isEmpty()) {
                                            imageButton.setImageResource(R.drawable.first_image_example);
                                        } else {
                                            Glide.with(EntireViewActivity.this)
                                                    .load(img)
                                                    .fitCenter()
                                                    .transform(new CenterCrop(), new RoundedCorners(30))
                                                    .placeholder(R.drawable.first_image_example)
                                                    .into(imageButton);
                                        }

                                        String minMonth = String.valueOf(Integer.parseInt(startdate.substring(4, 6)));
                                        String minDay = String.valueOf(Integer.parseInt(startdate.substring(6, 8)));
                                        String maxMonth = String.valueOf(Integer.parseInt(enddate.substring(4, 6)));
                                        String maxDay = String.valueOf(Integer.parseInt(enddate.substring(6, 8)));

                                        String entitydate = minMonth + "/" + minDay + "~" + maxMonth + "/" + maxDay;
                                        datetext.setText(entitydate);

                                        imageButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String contentId = contentid;
                                                navigateToDetailFestivalActivity(EntireViewActivity.this, contentId);
                                            }
                                        });
                                        samedayFestival.addView(sliderItemView);
                                    }

                                }

                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(MotionEffect.TAG, "API Error: " + error);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String content = intent.getStringExtra("content");
            if (content != null) {
                TextView entireContent = findViewById(R.id.festival_title);
                entireContent.setText(content);
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
