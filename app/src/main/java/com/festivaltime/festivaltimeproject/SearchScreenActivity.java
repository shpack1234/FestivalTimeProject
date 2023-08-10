package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
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
    private String cat2 = "";

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

        type="A02070100";
        String apiKey = getResources().getString(R.string.api_key);

        apiReader = new ApiReader();
        apiReader.searchKeyword(apiKey, query, type, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "response: " + response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response);
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout searchContainer = findViewById(R.id.search_container);
                                searchContainer.removeAllViews();

                                View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
                                GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
                                festivalImageNText.removeAllViews();


                                // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
                                String textToShow = getTextToShow(type);
                                TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
                                titleTextView.setText(textToShow);

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