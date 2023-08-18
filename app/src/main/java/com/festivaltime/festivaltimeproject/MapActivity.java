package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {

    private String[] areaName = {"서울", "인천", "대전", "대구", "광주", "부산", "울산", "세종", "경기도"
            , "강원도", "충청북도", "충청남도", "경상북도", "경상남도", "전라북도", "전라남도", "제주도"};
    private MapView mapView;
    private List<Pair<Double, Double>> coordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        onMapViewInitialized(mapView);



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
        mapView.setZoomLevel(11, true);
        coordinates.add(new Pair<>(37.5665, 126.9780)); //서울 1
        coordinates.add(new Pair<>(37.4562, 126.7052)); //인천 2
        coordinates.add(new Pair<>(36.3504, 127.3845)); //대전 3
        coordinates.add(new Pair<>(35.8714, 128.6014)); //대구 4
        coordinates.add(new Pair<>(35.1595, 126.8526)); //광주 5
        coordinates.add(new Pair<>(35.1795, 129.0756)); //부산 6
        coordinates.add(new Pair<>(35.5383, 129.3113)); //울산 7
        coordinates.add(new Pair<>(36.5040, 127.2494)); //세종 8
        coordinates.add(new Pair<>(37.5671, 127.1902)); //경기도 31
        coordinates.add(new Pair<>(37.5558, 128.2093)); //강원도 32
        coordinates.add(new Pair<>(36.6424, 127.4890)); //충청북도 33
        coordinates.add(new Pair<>(35.1603, 126.8247)); //충청남도 34
        coordinates.add(new Pair<>(36.6640, 128.4342)); //경상북도 35
        coordinates.add(new Pair<>(35.4606, 128.2132)); //경상남도 36
        coordinates.add(new Pair<>(35.7175, 127.153)); //전라북도 37
        coordinates.add(new Pair<>(34.8679, 126.991)); //전라남도 38
        coordinates.add(new Pair<>(33.3949, 126.5614)); //제주도 39

        MapPOIItem[] marker = new MapPOIItem[coordinates.size()];

        for (int i = 0; i < marker.length; i++) {
            marker[i] = new MapPOIItem();
        }

        marKingFestivalGroup(marker);
        mapView.setPOIItemEventListener(this); // 이벤트 리스너 등록
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
        MapPoint selectedMarkerPoint=mapPOIItem.getMapPoint();
        Log.d("MapActivity", "Selected Marker Point: " + selectedMarkerPoint.getMapPointGeoCoord());
        mapView.setMapCenterPointAndZoomLevel(selectedMarkerPoint, 7, true);


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

    private void marKingFestivalGroup(@NonNull MapPOIItem[] marker) {
        Bitmap originalMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_marking);
        Bitmap resizedMarkerBitmap = Bitmap.createScaledBitmap(originalMarkerBitmap, 200, 200, false);
        for (int i = 0; i < marker.length; i++) {
            marker[i].setItemName(areaName[i]);
            marker[i].setTag(i);
            marker[i].setMapPoint(MapPoint.mapPointWithGeoCoord(coordinates.get(i).first, coordinates.get(i).second));
            marker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker[i].setCustomImageBitmap(resizedMarkerBitmap);
            marker[i].setCustomImageAutoscale(false);
            marker[i].setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(marker[i]);
        }

    }
}