package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
    private final Context mContext;
    private final View mCalloutBalloon;
    public CustomCalloutBalloonAdapter(Context context) {
        mContext=context;
        mCalloutBalloon= LayoutInflater.from(mContext).inflate(R.layout.custom_callout_balloon, null);
    }

    @Override
    public View getCalloutBalloon(MapPOIItem mapPOIItem) {
        ((TextView)mCalloutBalloon.findViewById(R.id.festival_detail_title)).setText(mapPOIItem.getItemName());
        ((Button)mCalloutBalloon.findViewById(R.id.festival_detail_button)).setText("Custom CalloutBalloon");
        return mCalloutBalloon;
    }

    @Override
    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }
}

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener {
    private MapView mapView;

    private List<Pair<Double,Double>> coordinates=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
        }


        MapView.setMapTilePersistentCacheEnabled(false);
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setPOIItemEventListener(this);
        onMapViewInitialized(mapView);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter(MapActivity.this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(MapActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(MapActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(MapActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(MapActivity.this);
                return true;
            } else return item.getItemId() == R.id.action_profile;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (!check_result) {
                finish();
            }
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

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
        Log.d("MapActivity", "Current Zoom Level"+i);
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
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        MapPoint selectedMapPoint=mapPOIItem.getMapPoint();
        mapView.setMapCenterPointAndZoomLevel(selectedMapPoint, 5, true);
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