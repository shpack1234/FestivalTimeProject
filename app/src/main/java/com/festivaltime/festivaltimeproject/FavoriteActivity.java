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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.festivaltime.festivaltimeproject.festivaldatabasepackage.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class FavoriteActivity extends AppCompatActivity {
    private ApiReader apiReader;

    private UserDataBase db;

    private UserDao userDao;

    private UserEntity loadedUser;
    private String userId;
    private Executor executor;

    List<HashMap<String, String>> festivalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        String apiKey = getResources().getString(R.string.api_key);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);
                if (loadedUser == null) {
                    Log.e("Err", "No UserInfo");
                } else {
                    List<String> favoriteFestivals = loadedUser.getUserFavoriteFestival();
                    for (String festivalId : favoriteFestivals) {
                        Log.d("Favorite Festival", festivalId);
                    }

                    List<HashMap<String, String>> parsedFestivalList = new ArrayList<>();

                    for (String contentId : loadedUser.getUserFavoriteFestival()) {
                        apiReader = new ApiReader();
                        apiReader.detailCommon(apiKey, contentId, new ApiReader.ApiResponseListener() {
                            @Override
                            public void onSuccess(String response) {
                                ParsingApiData.parseXmlDataFromDetailCommon(response); // 응답을 파싱하여 데이터를 저장
                                List<HashMap<String, String>> festivalList = ParsingApiData.getFestivalList();
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

    private void updateUI(List<HashMap<String, String>> festivalList) {
        LinearLayout festivalContainer = findViewById(R.id.festival_container);
        festivalContainer.removeAllViews();

        for (HashMap<String, String> festivalInfo : festivalList) {
            View festivalInfoBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
            TextView titleTextView = festivalInfoBox.findViewById(R.id.festival_title);
            TextView locationTextView = festivalInfoBox.findViewById(R.id.festival_location);
            TextView idTextView = festivalInfoBox.findViewById(R.id.festival_overview);
            ImageButton festivalRepImage = festivalInfoBox.findViewById(R.id.festival_rep_image);

            String title = festivalInfo.get("title");
            String location = festivalInfo.get("address");
            String id = festivalInfo.get("contentid");
            String repImage = festivalInfo.get("img");
            titleTextView.setText(title);
            locationTextView.setText(location);
            idTextView.setText(id);

            Log.d(TAG, "Rep Image URL: " + repImage);
            if (repImage == null || repImage.isEmpty()) {
                festivalRepImage.setImageResource(R.drawable.ic_image);
            } else {
                Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
            }
            festivalContainer.addView(festivalInfoBox);

            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 시 contentid 값을 가져오는 작업 수행
                    String contentId = idTextView.getText().toString();
                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                    navigateToDetailFestivalActivity(FavoriteActivity.this, contentId);
                }
            });

        }
    }
}