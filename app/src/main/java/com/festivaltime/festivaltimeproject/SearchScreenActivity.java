package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

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

    private static final String TAG = "SearchScreenActivity";
    private Executor executor;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private ApiReader apiReader;
    private String type;
    private String cat3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        executor = Executors.newSingleThreadExecutor();

        String query = getIntent().getStringExtra("query");
        type = getIntent().getStringExtra("type");

        if (type == null) {
            type = ""; // type이 null일 경우 빈 문자열로 초기화
        }

        //type="A02070100";
        String apiKey = getResources().getString(R.string.api_key);

        apiReader = new ApiReader();
        apiReader.searchKeyword(apiKey, query, type, new ApiReader.ApiResponseListener() {
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


                                // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
                                String textToShow = getTextToShow(type);
                                TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
                                titleTextView.setText(textToShow);


                                int maxItems = Math.min(festivalList.size(), 6);

                                switch (query) {
                                    case "부산":
                                        cat3 = "A02080500";

                                        apiReader.categoryCode2(apiKey, cat3, new ApiReader.ApiResponseListener() {
                                            @Override
                                            public void onSuccess(String response) {
                                                Log.d("response", response);
                                                ParsingApiData.parseXmlDataFromCategoryCode(response, cat3); // 응답을 파싱하여 데이터를 저장
                                                List<LinkedHashMap<String, String>> parsedFestivalList2 = ParsingApiData.getFestivalList();
                                                executor.execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        festivalList.clear();
                                                        festivalList.addAll(parsedFestivalList2);

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
                                                                festivalImageNText.removeAllViews();

                                                                // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
                                                                String textToShow = getTextToShow(type);
                                                                TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
                                                                titleTextView.setText(textToShow);


                                                                int loopItems = Math.min(festivalList.size(), 3);

                                                                for (int i = 0; i < loopItems; i++) {
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


                                                                    festivalItemView.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            String contentId = id;
                                                                            // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                                                            navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId);
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
                                        break;


                                    case "축제":
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


                                            festivalItemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String contentId = id;
                                                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                                    navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId);
                                                }
                                            });


                                        }
                                        break;
                                }

                                searchContainer.addView(searchContainerView);

                                Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
                                detailSearchButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(SearchScreenActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(SearchScreenActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(SearchScreenActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(SearchScreenActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(SearchScreenActivity.this);
                return true;
            }
        });
    }

    private String getTextToShow(String type) {
        switch (type) {
            case "A02080100":
                return "전통공연";
            case "A02080200":
                return "연극";
            case "A02080300":
                return "뮤지컬";
            case "A02080400":
                return "오페라";
            case "A02080500":
                return "전시회";
            case "A02080600":
                return "박람회";
            case "A02080800":
                return "무용";
            case "A02080900":
                return "클래식음악회";
            case "A02081000":
                return "대중콘서트";
            case "A02081100":
                return "영화";
            default:
                if (!type.isEmpty() && type.startsWith("A0207")) {
                    return "축제";
                } else {
                    return "기타";
                }
        }
    }
}