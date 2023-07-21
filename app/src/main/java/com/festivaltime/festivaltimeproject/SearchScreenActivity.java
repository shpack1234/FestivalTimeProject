package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    private LinearLayout festivalContainer2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        //setContentView(R.layout.festivalsearch_container);


        executor = Executors.newSingleThreadExecutor();

        type="A02080200";

        String query = getIntent().getStringExtra("query");
/**
        Button entireButton = findViewById(R.id.detail_search_button);
        entireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, type);
            }
        });
**/


        String type = getIntent().getStringExtra("type");
        String apiKey = getResources().getString(R.string.api_key);
        apiReader=new ApiReader();
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
                                }
                                searchContainer.addView(searchContainerView);

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






    }



}
