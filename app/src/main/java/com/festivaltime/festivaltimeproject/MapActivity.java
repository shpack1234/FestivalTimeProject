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
import android.util.Pair;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {

    private MapView mapView;
    private List<Pair<Double,Double>> coordinates=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = new MapView(this);
        ViewGroup mapViewContainer=(ViewGroup)findViewById(R.id.map_view);
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
        coordinates.add(new Pair<>(37.5665, 126.9780));
        coordinates.add(new Pair<>(33.3949, 126.5614));

        MapPOIItem[] marker={new MapPOIItem(), new MapPOIItem()};

        marKingFestivalGroup(marker);
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
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    private void marKingFestivalGroup(@NonNull MapPOIItem[] marker) {
        Bitmap originalMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_marking);
        Bitmap resizedMarkerBitmap = Bitmap.createScaledBitmap(originalMarkerBitmap, 200, 200, false);
        for(int i=0; i<marker.length; i++) {
            marker[i].setItemName("마커");
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