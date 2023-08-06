package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;



public class SearchScreenActivity extends AppCompatActivity {

    String type;


    private ApiReader apiReader;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private Executor executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        //setContentView(R.layout.festivalsearch_container);


        executor = Executors.newSingleThreadExecutor();

        //type = "A02080200";

        String query = getIntent().getStringExtra("query");

        String type = getIntent().getStringExtra("type");
        String apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();
        apiReader.searchKeyword(apiKey, query, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

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
                                LinearLayout searchContainer = findViewById(R.id.search_container);
                                searchContainer.removeAllViews();


                                View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
                                GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
                                //GridLayout festivalImageNText = findViewById(R.id.festivalSearch_container3);
                                festivalImageNText.removeAllViews();



                                int maxItems = Math.min(festivalList.size(), 6);

                                for (int i = 0; i < maxItems; i++) {
                                    HashMap<String, String> festivalInfo = festivalList.get(i);
                                    View festivalItemView = getLayoutInflater().inflate(R.layout.festival_search_imagentext, null);
                                    TextView searchTextView = festivalItemView.findViewById(R.id.search_text);
                                    ImageButton searchImageButton = festivalItemView.findViewById(R.id.search_image);

                                    String title = festivalInfo.get("title");
                                    String id = festivalInfo.get("contentid");
                                    String repImage = festivalInfo.get("img");

                                    searchTextView.setText(title);
                                    searchTextView.setMaxEms(8);

                                    Log.d(TAG, "Rep Image URL: " + repImage);
                                    if (repImage == null || repImage.isEmpty()) {
                                        searchImageButton.setImageResource(R.drawable.ic_image);
                                    } else {
                                        Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(searchImageButton);
                                    }
                                    festivalImageNText.addView(festivalItemView);


                                    festivalItemView.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v){
                                            String contentId = id;
                                            // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                            navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId);
                                        }
                                    });


                                }
                                searchContainer.addView(searchContainerView);

                                Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
                                detailSearchButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent =new Intent (SearchScreenActivity.this,SearchDetailActivity.class);
                                        navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, type);
                                    }
                                });

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
/**
        apiReader.categoryCode1(apiKey, query, type, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromCategoryCode(response); // 응답을 파싱하여 데이터를 저장
                List<HashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

**/


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);//하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    navigateToMainActivity(SearchScreenActivity.this);
                    return true;
                case R.id.action_map:
                    navigateToMapActivity(SearchScreenActivity .this);
                    return true;
                case R.id.action_calendar:
                    navigateToCalendarActivity(SearchScreenActivity.this);
                    return true;
                case R.id.action_favorite:
                    navigateToFavoriteActivity(SearchScreenActivity.this);
                    return true;
                case R.id.action_profile:
                    navigateToMyPageActivity(SearchScreenActivity.this);
                    return true;
            }
            return false;
        });
    }


}
