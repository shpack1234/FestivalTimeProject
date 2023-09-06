package com.festivaltime.festivaltimeproject.map;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import com.festivaltime.festivaltimeproject.ApiReader;
import com.festivaltime.festivaltimeproject.ParsingApiData;
import com.festivaltime.festivaltimeproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

    boolean isFestivalVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        onMapViewInitialized(mapView);
        executor = Executors.newSingleThreadExecutor();

        apiKey = getResources().getString(R.string.api_key);

        searchBarLayout = findViewById(R.id.map_search);
        searchEditText = findViewById(R.id.map_search_bar);
        searchButton = findViewById(R.id.detailButton);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.map_search_fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.map_search_fade_out);

        ImageButton mapResetButton=findViewById(R.id.map_reset_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getQuery().toString();

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

        mapView.removeAllPOIItems();
        mapView.setZoomLevel(11, true);
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
        if (zoomLevel >= 8 && isFestivalVisible==true) {
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

}