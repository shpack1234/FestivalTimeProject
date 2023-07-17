package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.festivaltime.festivaltimeproject.userdatabasepackage.*;

public class SearchDetailActivity extends AppCompatActivity {
    private ApiReader apiReader;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private UserDataBase db;
    private UserDao userDao;
    private UserEntity loadedUser;
    private String userId;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        executor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        String query = getIntent().getStringExtra("query");
        String apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();
        apiReader.searchKeyword(apiKey, query, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
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

                                for (HashMap<String, String> festivalInfo : festivalList) {
                                    View festivalInfoBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
                                    TextView titleTextView = festivalInfoBox.findViewById(R.id.festival_title);
                                    TextView locationTextView = festivalInfoBox.findViewById(R.id.festival_location);
                                    TextView idTextView = festivalInfoBox.findViewById(R.id.festival_overview);
                                    ImageButton festivalRepImage = festivalInfoBox.findViewById(R.id.festival_rep_image);
                                    ImageButton addButton = festivalInfoBox.findViewById(R.id.festival_addButton);

                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    layoutParams.setMargins(0, 0, 0, 40); // 아래에 16dp의 마진을 추가
                                    festivalInfoBox.setLayoutParams(layoutParams);

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
                                            navigateToDetailFestivalActivity(SearchDetailActivity.this, contentId);
                                        }
                                    });

                                    addButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("Button Listener", "addBtn");
                                            executor.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String contentId = idTextView.getText().toString();
                                                    loadedUser = userDao.getUserInfoById(userId);
                                                    if (loadedUser != null) {
                                                        List<String> favoriteFestivals = loadedUser.getUserFavoriteFestival();
                                                        for (String festivalId : favoriteFestivals) {
                                                            Log.d("Favorite Festival", festivalId);
                                                        }
                                                        if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
                                                            Log.d("Festival Id", contentId);
                                                            Log.d("Button Listener", "ID already exists in userFavoriteFestival");
                                                        } else {
                                                            Log.d("Festival Id", contentId);
                                                            loadedUser.addUserFavoriteFestival(contentId);
                                                            userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
                                                        }
                                                    } else {
                                                        Log.e("No UserInfo", "You should get your information in MyPage");
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
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


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
                return item.getItemId() == R.id.action_profile;
            }
        });
    }
}
