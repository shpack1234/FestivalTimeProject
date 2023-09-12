package com.festivaltime.festivaltimeproject.map;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToSearchActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.widget.SearchView;

import com.festivaltime.festivaltimeproject.ApiReader;
import com.festivaltime.festivaltimeproject.DataHolder;
import com.festivaltime.festivaltimeproject.MainActivity;
import com.festivaltime.festivaltimeproject.ParsingApiData;
import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.SearchScreenActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

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
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {

    private String[] areaNum = {"1", "2", "3", "4", "5", "6", "7", "8", "31"
            , "32", "33", "34", "35", "36", "37", "38", "39"};
    private MapView mapView;

    private List<Pair<Double, Double>> areas = new ArrayList<>();

    private List<Pair<String, String>> areasInfo = new ArrayList<>();

    private String query;
    private ApiReader apiReader;
    private String apiKey;
    private Executor executor;

    String selectedFestivalName;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();

    private List<Pair<Double, Double>> areaFestivals = new ArrayList<>();

    private List<Pair<String, String>> areaFestivalInfo = new ArrayList<>();

    private ConstraintLayout searchBarLayout;

    private SearchView searchEditText;

    private ImageButton searchButton;

    private Animation fadeOutAnimation;
    private Animation fadeInAnimation;

    private String locationSelect;
    private String formattedStartDate;
    private String formattedEndDate;

    boolean isFestivalVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        onMapViewInitialized(mapView);
        executor = Executors.newSingleThreadExecutor();

        apiKey = getResources().getString(R.string.api_key);

        searchBarLayout = findViewById(R.id.map_search);
        searchButton = findViewById(R.id.detailButton);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.map_search_fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.map_search_fade_out);

        ImageButton mapResetButton=findViewById(R.id.map_reset_button);

        searchEditText = findViewById(R.id.map_search_bar);
        searchEditText.setOnTouchListener((v, event) -> {
            searchEditText.setIconified(false);
            searchEditText.performClick();
            return true;
        });

        //상세 검색 버튼
        
        ImageButton searchoptionbutton = findViewById(R.id.detailButton);
         searchoptionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();
            }
        });
        searchEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {       //검색 시 검색 내용 SearchActivity 로 전달
                query = s;
                showSearchedFestival(mapView, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mapResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapViewInitialized(mapView);
            }
        });

        mapView.setMapViewEventListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(MapActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                return false;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(MapActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(MapActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(MapActivity.this);
                return true;
            }
        });
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        isFestivalVisible=false;

        MapPoint centerPoint=MapPoint.mapPointWithGeoCoord(36.5, 127.5);

        mapView.removeAllPOIItems();
        mapView.setMapCenterPointAndZoomLevel(centerPoint,11,false);
        areas.add(new Pair<>(37.5665, 126.9780)); //서울 1
        areas.add(new Pair<>(37.4562, 126.7052)); //인천 2
        areas.add(new Pair<>(36.3504, 127.3845)); //대전 3
        areas.add(new Pair<>(35.8714, 128.6014)); //대구 4
        areas.add(new Pair<>(35.1595, 126.8526)); //광주 5
        areas.add(new Pair<>(35.1795, 129.0756)); //부산 6
        areas.add(new Pair<>(35.5383, 129.3113)); //울산 7
        areas.add(new Pair<>(36.5040, 127.2494)); //세종 8
        areas.add(new Pair<>(37.5671, 127.1902)); //경기도 31
        areas.add(new Pair<>(37.5558, 128.2093)); //강원도 32
        areas.add(new Pair<>(36.6424, 127.4890)); //충청북도 33
        areas.add(new Pair<>(35.1603, 126.8247)); //충청남도 34
        areas.add(new Pair<>(36.6640, 128.4342)); //경상북도 35
        areas.add(new Pair<>(35.4606, 128.2132)); //경상남도 36
        areas.add(new Pair<>(35.7175, 127.153)); //전라북도 37
        areas.add(new Pair<>(34.8679, 126.991)); //전라남도 38
        areas.add(new Pair<>(33.3949, 126.5614)); //제주도 39

        areasInfo.add(new Pair<>("서울", "1"));
        areasInfo.add(new Pair<>("인천", "2"));
        areasInfo.add(new Pair<>("대전", "3"));
        areasInfo.add(new Pair<>("대구", "4"));
        areasInfo.add(new Pair<>("광주", "5"));
        areasInfo.add(new Pair<>("부산", "6"));
        areasInfo.add(new Pair<>("울산", "7"));
        areasInfo.add(new Pair<>("세종", "8"));
        areasInfo.add(new Pair<>("경기도", "31"));
        areasInfo.add(new Pair<>("강원도", "32"));
        areasInfo.add(new Pair<>("충청북도", "33"));
        areasInfo.add(new Pair<>("충청남도", "34"));
        areasInfo.add(new Pair<>("경상북도", "35"));
        areasInfo.add(new Pair<>("경상남도", "36"));
        areasInfo.add(new Pair<>("전라북도", "37"));
        areasInfo.add(new Pair<>("전라남도", "38"));
        areasInfo.add(new Pair<>("제주도", "39"));

        MapPOIItem[] marker = new MapPOIItem[areas.size()];

        for (int i = 0; i < marker.length; i++) {
            marker[i] = new MapPOIItem();
        }

        marKingFestivalGroup(marker, areas, areasInfo);
        mapView.setPOIItemEventListener(this); // 이벤트 리스너 등록
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        if (zoomLevel >= 10 && isFestivalVisible==true) {
            mapView.removeAllPOIItems();
            onMapViewInitialized(mapView);
        }
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
        if(searchBarLayout.getVisibility()==View.VISIBLE) {
            searchBarLayout.startAnimation(fadeOutAnimation); // 사라지는 애니메이션 실행
            searchBarLayout.setVisibility(View.GONE); // 검색 바 레이아웃을 숨기도록 설정
        }
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        if (searchBarLayout.getVisibility() != View.VISIBLE) {
            searchBarLayout.setVisibility(View.VISIBLE); // 검색 바 레이아웃을 보이도록 설정
            searchBarLayout.startAnimation(fadeInAnimation); // 나타나는 애니메이션 실행
        }
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        MapPoint selectedMarkerPoint = mapPOIItem.getMapPoint();
        Log.d("MapActivity", "Selected Marker Point: " + selectedMarkerPoint.getMapPointGeoCoord());

        // 축제 선택 시
        if (mapPOIItem.getTag() > 100) {
            isFestivalVisible=true;

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://dapi.kakao.com")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PlacesApi placesApi = retrofit.create(PlacesApi.class);

            Call<ApiResponseModel> call = placesApi.searchPlaces(
                    "음식점", selectedMarkerPoint.getMapPointGeoCoord().longitude,
                    selectedMarkerPoint.getMapPointGeoCoord().latitude, 1000);

            call.enqueue(new Callback<ApiResponseModel>() {
                @Override
                public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                    if (response.isSuccessful()) {
                        ApiResponseModel apiResponse = response.body();
                        Gson gson = new Gson();

                        ApiResponseModel restApiData = gson.fromJson(apiResponse.getJsonString(), ApiResponseModel.class);

                        if (restApiData != null) {
                            Log.d("hello", "not null");
                            List<PoiItem> places = restApiData.getDocuments();
                            int placesCount = places.size(); // 리스트의 크기를 가져옴
                            Log.d("API Response", "Number of places: " + placesCount);
                            showPlacesOnMap(places);
                        } else {
                            Log.e("API Error", "Response body is null");
                        }

                    } else {
                        Log.e("API Error", "Response Code: " + response.code() + " - Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                    Log.e("Network Error", t.getMessage());
                }
            });

            mapView.setMapCenterPointAndZoomLevel(selectedMarkerPoint, 1, true);


            View popupView;

            //음식점
            if (mapPOIItem.getTag() == 101) {
                popupView = getLayoutInflater().inflate(R.layout.etc_callout_balloon, null);

                // 팝업 창 생성 및 설정
                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );

                TextView titleTextView = popupView.findViewById(R.id.etc_detail_title);
                TextView etcNearFestival = popupView.findViewById(R.id.etc_near_festival);
                TextView etcLoc = popupView.findViewById(R.id.etc_location);
                String[] etcInfo = mapPOIItem.getItemName().split(",");
                titleTextView.setText(etcInfo[0]);
                etcNearFestival.setText("근처 축제   " + selectedFestivalName);
                etcLoc.setText("위치   " + etcInfo[1]);

                int bottomBarHeight = findViewById(R.id.bottom_navigation).getHeight();
                int popupHeight = popupView.getMeasuredHeight(); // 팝업 높이 측정
                int yOffset = bottomBarHeight + popupHeight; // 팝업 높이만큼 추가 오프셋

                popupWindow.showAtLocation(mapView, Gravity.TOP, 0, 0);
            } else {
                selectedFestivalName = mapPOIItem.getItemName();
                popupView = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
                String contentId = String.valueOf(mapPOIItem.getTag());
                apiReader.detailIntro(apiKey, contentId, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromdetailIntro(response); // 응답을 파싱하여 데이터를 저장

                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() { //UI 추가 부분
                                festivalList.clear(); // 기존 데이터를 모두 제거
                                festivalList.addAll(parsedFestivalList);
                                for (HashMap<String, String> festivalInfo : festivalList) {

                                    String location = festivalInfo.get("eventplace");
                                    String eventstartdate = festivalInfo.get("eventstartdate");
                                    String eventenddate = festivalInfo.get("eventenddate");
                                    String eventStart = eventstartdate.substring(0, 4) + "." + eventstartdate.substring(4, 6) + "." + eventstartdate.substring(6);
                                    String eventEnd = eventenddate.substring(0, 4) + "." + eventenddate.substring(4, 6) + "." + eventenddate.substring(6);

                                    // 팝업 창 생성 및 설정
                                    PopupWindow popupWindow = new PopupWindow(
                                            popupView,
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            true
                                    );

                                    TextView titleTextView = popupView.findViewById(R.id.festival_detail_title);
                                    TextView festivalLoc = popupView.findViewById(R.id.festival_location);
                                    TextView festivalPer = popupView.findViewById(R.id.festival_period);
                                    titleTextView.setText(mapPOIItem.getItemName());
                                    festivalLoc.setText(location);
                                    festivalPer.setText(eventStart + " ~ " + eventEnd);

                                    int bottomBarHeight = findViewById(R.id.bottom_navigation).getHeight();
                                    int popupHeight = popupView.getMeasuredHeight(); // 팝업 높이 측정
                                    int yOffset = bottomBarHeight + popupHeight; // 팝업 높이만큼 추가 오프셋

                                    popupWindow.showAtLocation(mapView, Gravity.TOP, 0, 0);


                                    // 팝업 창 내부의 버튼 등의 UI 요소에 대한 동작 처리
                                    Button detailButton = popupView.findViewById(R.id.festival_detail_button);
                                    detailButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int contentId = mapPOIItem.getTag();
                                            navigateToDetailFestivalActivity(MapActivity.this, String.valueOf(contentId));
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
            }


        } else { //지역 선택 시
            isFestivalVisible=true;

            mapView.setMapCenterPointAndZoomLevel(selectedMarkerPoint, 5, true);

            apiReader = new ApiReader();
            String selectedAreaNum = String.valueOf(mapPOIItem.getTag());
            apiReader.areaBasedSync(apiKey, selectedAreaNum, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.d("response", response);
                    ParsingApiData.parseXmlDataFromAreaBasedSync(response); // 응답을 파싱하여 데이터를 저장

                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                    Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            areaFestivals.clear();
                            areaFestivalInfo.clear();
                            for (HashMap<String, String> festivalInfo : parsedFestivalList) {
                                String title = festivalInfo.get("title");
                                String mapx = festivalInfo.get("mapx");
                                String mapy = festivalInfo.get("mapy");
                                String contentid = festivalInfo.get("contentid");

                                Log.d("title", title);
                                Log.d("contentid", contentid);
                                areaFestivals.add(new Pair<>(Double.parseDouble(mapy), Double.parseDouble(mapx)));
                                areaFestivalInfo.add(new Pair<>(title, contentid));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MapPOIItem[] marker = new MapPOIItem[areaFestivals.size()];

                                    for (int i = 0; i < marker.length; i++) {
                                        marker[i] = new MapPOIItem();
                                    }

                                    marKingFestivalGroup(marker, areaFestivals, areaFestivalInfo);
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
        }
    }

    private void showSearchedFestival(MapView mapView, String query) {
        isFestivalVisible=false;
        mapView.removeAllPOIItems();


        MapPoint centerPoint=MapPoint.mapPointWithGeoCoord(36.5, 127.5);
        mapView.setMapCenterPointAndZoomLevel(centerPoint,11,true);

        apiReader=new ApiReader();
        apiReader.searchKeyword(apiKey, query, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장

                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        areaFestivals.clear();
                        areaFestivalInfo.clear();
                        for (HashMap<String, String> festivalInfo : parsedFestivalList) {
                            String title = festivalInfo.get("title");
                            String mapx = festivalInfo.get("mapx");
                            String mapy = festivalInfo.get("mapy");
                            String contentid = festivalInfo.get("contentid");

                            Log.d("title", title);
                            Log.d("contentid", contentid);
                            areaFestivals.add(new Pair<>(Double.parseDouble(mapy), Double.parseDouble(mapx)));
                            areaFestivalInfo.add(new Pair<>(title, contentid));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MapPOIItem[] marker = new MapPOIItem[areaFestivals.size()];

                                for (int i = 0; i < marker.length; i++) {
                                    marker[i] = new MapPOIItem();
                                }

                                marKingFestivalGroup(marker, areaFestivals, areaFestivalInfo);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showPlacesOnMap(List<PoiItem> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PoiItem place : places) {
                    MapPOIItem poiItem = new MapPOIItem();
                    poiItem.setTag(101);
                    poiItem.setItemName(place.getPlaceName() + "," + place.getAddressName());
                    poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(place.getY()), Double.parseDouble(place.getX())));
                    poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
                    mapView.addPOIItem(poiItem);
                }
            }
        });
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    private void marKingFestivalGroup(@NonNull MapPOIItem[] marker, List<Pair<Double, Double>> location, List<Pair<String, String>> Info) {
        for (int i = 0; i < marker.length; i++) {
            Log.d("Tag", String.valueOf(Info.get(i).second));
        }
        Bitmap originalMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_marking);
        Bitmap resizedMarkerBitmapArea = Bitmap.createScaledBitmap(originalMarkerBitmap, 100, 100, false);
        Bitmap resizedMarkerBitmapFestival = Bitmap.createScaledBitmap(originalMarkerBitmap, 50, 50, false);
        for (int i = 0; i < marker.length; i++) {
            marker[i].setItemName(Info.get(i).first);
            Log.d("Tag", Info.get(i).second);
            marker[i].setTag(Integer.parseInt(Info.get(i).second));
            marker[i].setMapPoint(MapPoint.mapPointWithGeoCoord(location.get(i).first, location.get(i).second));
            marker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
            if(marker[i].getTag()<100)
                marker[i].setCustomImageBitmap(resizedMarkerBitmapArea);
            else marker[i].setCustomImageBitmap(resizedMarkerBitmapFestival);
            marker[i].setCustomImageAutoscale(false);
            marker[i].setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(marker[i]);
        }
    }

    private void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.custom_popup);
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
        Button starttimeClick = dialogView.findViewById(R.id.dialog_popup_start_time);
        Button enddateClick = dialogView.findViewById(R.id.dialog_popup_end_date);
        Button endtimeClick = dialogView.findViewById(R.id.dialog_popup_end_time);
        Button location = dialogView.findViewById(R.id.dialog_popup_location);

        DatePicker StartDatePicker = dialogView.findViewById(R.id.dialog_popup_StartDatePicker);
        TimePicker StartTimePicker = dialogView.findViewById(R.id.dialog_popup_StartTimePicker);
        DatePicker EndDatePicker = dialogView.findViewById(R.id.dialog_popup_EndDatePicker);
        TimePicker EndTimePicker = dialogView.findViewById(R.id.dialog_popup_EndTimePicker);

        Dialog dialog =dialogBuilder.create();
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
                        Intent queryIntent = new Intent(MapActivity.this, SearchScreenActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("startdate", formattedStartDate);
                        bundle.putString("enddate", formattedEndDate);

                        if (locationSelect != null && !locationSelect.isEmpty()) {
                            bundle.putString("location", locationSelect);
                            DataHolder.getInstance().setBundle(bundle);
                            //navigateToSearchActivity(MainActivity.this, query, queryIntent);


                        } else {
                            Toast.makeText(MapActivity.this, "위치가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(MapActivity.this, "기간을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

}