package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    private TextView queryTextView;
    private ApiReader apiReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        queryTextView = findViewById(R.id.search_text_View);
        String query = getIntent().getStringExtra("query");
        if (query != null) {
            queryTextView.setText(query);
        }

        apiReader=new ApiReader();

        String apiKey="HdKCANJUYTL+ISr7diKfFrqa799Hzt+hsbTMYDFitQ0hkS8r9chW84iZyXeiHGjXmaKq6d2aN7OPAM1imqUoFw==";
        apiReader.searchKeyword(apiKey, query, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG,"API Response: "+response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: "+error);
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);  //하단 바 navigate 처리
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(SearchActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(SearchActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(SearchActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(SearchActivity.this);
                return true;
            } else return item.getItemId() == R.id.action_profile;
        });
    }
}
