package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class EntireViewActivity extends AppCompatActivity {

    private ApiReader apiReader;

    private List<HashMap<String, String>> festivalList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entire_view);

        final TextView title=(TextView)findViewById(R.id.Entire_view_title);

        String contentId = getIntent().getStringExtra("contentid");
        TextView contentidTextView = findViewById(R.id.festival_contentid);
        contentidTextView.setText(contentId);

        String apiKey = getResources().getString(R.string.api_key);
        apiReader = new ApiReader();
        apiReader.detailCommon(apiKey, contentId, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromDetailCommon(response); // 응답을 파싱하여 데이터를 저장

                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() { //UI 추가 부분
                        festivalList.clear(); // 기존 데이터를 모두 제거
                        festivalList.addAll(parsedFestivalList);
                        for (HashMap<String, String> festivalInfo : festivalList) {
                            TextView titleTextView = findViewById(R.id.festival_title);
                            TextView address = findViewById(R.id.festival_address);
                            TextView idTextView = findViewById(R.id.festival_contentid);
                            ImageView festivalFirstImage = findViewById(R.id.festival_firstimage);
                            TextView overviewText = findViewById(R.id.festival_overview);

                            String title = festivalInfo.get("title");
                            String address1 = festivalInfo.get("address1");
                            String address2 = festivalInfo.get("address2");
                            String id = festivalInfo.get("contentid");
                            String firstImage = festivalInfo.get("img");
                            String overview = festivalInfo.get("overview");

                            Log.d("imgUrl", firstImage);

                            titleTextView.setText(title);
                            address.setText(address1 + " " + address2);
                            idTextView.setText(id);
                            overviewText.setText(overview);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });

        Intent intent = getIntent();
        if(intent != null) {
            String content = intent.getStringExtra("content");
            if(content != null){
                TextView entireContent = findViewById(R.id.Entire_view_title);
                entireContent.setText(content);
            }
        }
    }


}