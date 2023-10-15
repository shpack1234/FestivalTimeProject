package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.map.MapActivity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntireViewActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {
    private MapView mapView;
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

    private String mapx;
    private String mapy;

    private String title;

    private boolean shouldNavigateBackToMapActivity;

    @Override
    public void onBackPressed() {
        // 원하는 뒤로가기 동작을 정의
        if (shouldNavigateBackToMapActivity) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed(); // 기본 뒤로가기 동작 실행
        }
    }



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

        //executor = Executors.newSingleThreadExecutor();
        ExecutorService executor = Executors.newCachedThreadPool();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        calendarDatabase = CalendarDatabase.getInstance(getApplicationContext());
        calendarDao = calendarDatabase.calendarDao();

        String contentId = getIntent().getStringExtra("contentid");
        shouldNavigateBackToMapActivity=getIntent().getBooleanExtra("shouldNavigateBackToMapActivity", false);

        String apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();

        executor.execute(() -> {
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
                                ImageView festivalBack = findViewById(R.id.slider_image_);
                                TextView titleTextView = findViewById(R.id.festival_title);
                                TextView address = findViewById(R.id.festival_address);
                                ImageView festivalFirstImage = findViewById(R.id.festival_firstimage);
                                TextView overviewText = findViewById(R.id.festival_overview);
                                ImageButton favoriteaddButton = findViewById(R.id.favorite_addButton);
                                ImageButton calendaraddButton = findViewById(R.id.calendar_addButton);

                                title = festivalInfo.get("title");
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
                                String backImage = festivalInfo.get("img1");
                                String overview = festivalInfo.get("overview");
                                final String[] finalstartDate = {null};
                                final String[] finalendDate = {null};


                                if (firstImage == null || firstImage.isEmpty()) {
                                    festivalFirstImage.setImageResource(R.mipmap.empty_image);
                                } else {
                                    Log.d("imgUrl", firstImage);
                                    Glide
                                            .with(EntireViewActivity.this)
                                            .load(firstImage)
                                            .transform(new CenterCrop(), new RoundedCorners(46)) // 둥근 테두리 반지름을 조절할 수 있음
                                            .placeholder(R.drawable.radius_corner)
                                            .into(festivalFirstImage);
                                }

                                if (backImage == null || backImage.isEmpty()) {
                                    festivalBack.setImageResource(R.mipmap.empty_image);
                                } else {
                                    Glide
                                            .with(EntireViewActivity.this)
                                            .load(backImage)
                                            .transform(new CenterCrop(), new RoundedCorners(46)) // 둥근 테두리 반지름을 조절할 수 있음
                                            .placeholder(R.drawable.radius_corner)
                                            .into(festivalBack);
                                }

                                titleTextView.setText(title);
                                address.setText(addresstext);
                                //html 형태 변환하여 setText
                                if (overview != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        overviewText.setText(Html.fromHtml(overview, Html.FROM_HTML_MODE_LEGACY));
                                    } else {
                                        overviewText.setText(Html.fromHtml(overview));
                                    }
                                } else {
                                    // detailInfo가 null인 경우에 대한 처리 추가
                                    overviewText.setText(getString(R.string.null_text));
                                }

                                mapx = festivalInfo.get("mapx");
                                mapy = festivalInfo.get("mapy");

                                mapView = new MapView(EntireViewActivity.this);
                                ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
                                mapViewContainer.addView(mapView);
                                onMapViewInitialized(mapView);
                                ExecutorService executor = Executors.newCachedThreadPool();

                                mapView.setMapViewEventListener(EntireViewActivity.this);


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


            // RecyclerView 초기화
            RecyclerView recyclerView = findViewById(R.id.entire_sameday_festival_container);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<LinkedHashMap<String, String>> samedaylist = new ArrayList<>();

            EntireTodayAdapter adapter = new EntireTodayAdapter(this, samedaylist);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EntireViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            apiReader.Festivallit2(apiKey, todaydate, todaydate, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ParsingApiData.parseXmlDataFromFestival(response);
                            List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                            samedaylist.addAll(parsedFestivalList);

                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e(MotionEffect.TAG, "API Error: " + error);
                }
            });

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


    @Override
    public void onMapViewInitialized(MapView mapView) {
        MapPoint festivalPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(mapy), Double.parseDouble(mapx));
        mapView.setMapCenterPointAndZoomLevel(festivalPoint, 3, false);

        Pair<Double, Double> area = new Pair<>(Double.parseDouble(mapy), Double.parseDouble(mapx));

        MapPOIItem marker = new MapPOIItem();
        marKingFestival(marker, area, title);
    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem
            mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint
            mapPoint) {

    }

    private void marKingFestival(@NonNull MapPOIItem
                                         marker, Pair<Double, Double> location, String festivalTitle) {
        Bitmap festivalMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.festival_inmap);
        Bitmap resizedFestivalMarkerBitmap = Bitmap.createScaledBitmap(festivalMarkerBitmap, 100, 100, false);

        marker.setItemName(festivalTitle);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(location.first, location.second));
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageBitmap(resizedFestivalMarkerBitmap);
        marker.setCustomImageAutoscale(false);
        marker.setCustomImageAnchor(0.5f, 0.5f);
        mapView.addPOIItem(marker);

    }

    public void mapOnClick(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout mapContainer = findViewById(R.id.map_container);
                mapContainer.removeView(mapView);
                onPause();
                navigateToSomeActivity.navigateToMapActivity(EntireViewActivity.this, title);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            ViewGroup mapViewContainer = findViewById(R.id.map_view);
            mapViewContainer.removeView(mapView);
            mapView = null;
        }
    }
}
