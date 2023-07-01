package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToBadgeActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ApiReader apiReader;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getStringExtra("query");
        String apiKey=getResources().getString(R.string.api_key);
        apiReader=new ApiReader();
        apiReader.searchKeyword(apiKey, query, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장

                List<HashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { //UI 추가 부분
                        festivalList.clear(); // 기존 데이터를 모두 제거
                        festivalList.addAll(parsedFestivalList);
                        LinearLayout festivalContainer = findViewById(R.id.festival_container);
                        festivalContainer.removeAllViews();

                        for (HashMap<String, String> festivalInfo : festivalList) {
                            View festivalInfoBox = getLayoutInflater().inflate(R.layout.festival_info_box, null);
                            TextView titleTextView = festivalInfoBox.findViewById(R.id.festival_title);
                            TextView locationTextView=festivalInfoBox.findViewById(R.id.festival_location);
                            TextView idTextView=festivalInfoBox.findViewById(R.id.festival_id);
                            ImageButton festivalRepImage=festivalInfoBox.findViewById(R.id.festival_rep_image);

                            String title = festivalInfo.get("title");
                            String location=festivalInfo.get("address");
                            String id=festivalInfo.get("contentid");
                            String repImage=festivalInfo.get("img");
                            titleTextView.setText(title);
                            locationTextView.setText(location);
                            idTextView.setText(id);

                            festivalInfoBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 클릭 시 contentid 값을 가져오는 작업 수행
                                    String contentId = idTextView.getText().toString();

                                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                    navigateToDetailFestivalActivity(SearchActivity.this, contentId);
                                }
                            });

                            Log.d(TAG, "Rep Image URL: " + repImage);
                            if(repImage == null || repImage.isEmpty()) {
                                festivalRepImage.setImageResource(R.drawable.ic_image);
                            } else {
                                Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(festivalRepImage);
                            }
                            festivalContainer.addView(festivalInfoBox);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
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
