package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class EntireViewActivity extends AppCompatActivity {
    public ImageButton Back_Btn;
    private ApiReader apiReader;


    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private String selectDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entire_view);

        Back_Btn=findViewById(R.id.before_btn);

        String contentId = getIntent().getStringExtra("contentid");

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
                            ImageView festivalFirstImage = findViewById(R.id.festival_firstimage);
                            TextView overviewText = findViewById(R.id.festival_overview);

                            String title = festivalInfo.get("title");
                            String address1 = festivalInfo.get("address1");
                            String address2 = festivalInfo.get("address2");
                            String id = festivalInfo.get("contentid");
                            String firstImage = festivalInfo.get("img");
                            String overview = festivalInfo.get("overview");

                            Log.d("imgUrl", firstImage);
                            if (firstImage == null || firstImage.isEmpty()) {
                                festivalFirstImage.setImageResource(R.drawable.ic_image);
                            } else {
                                Glide
                                        .with(EntireViewActivity.this)
                                        .load(firstImage)
                                        .transform(new CenterCrop())
                                        .placeholder(R.drawable.ic_image)
                                        .into(festivalFirstImage);
                            }

                            titleTextView.setText(title);
                            address.setText(address1 + " " + address2);
                            overviewText.setText(overview);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });


        String selectdata;
        apiReader = new ApiReader();
        // 'festivallit' 메소드를 사용하여 eventStartDate와 eventEndDate 값을 가져옵니다.
        apiReader.Festivallit(apiKey, selectDate, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // 응답을 파싱하여 eventStartDate와 eventEndDate 값을 가져옵니다.
                ParsingApiData.parseXmlDataFromFestivalA(response);

                // festivalList에서 필요한 정보를 가져옵니다.
                for (HashMap<String, String> festivalInfo : festivalList) {
                    TextView startDateTextView=findViewById(R.id.start_date);
                    TextView endDataTextView=findViewById(R.id.end_date);
                    String eventStartDate = festivalInfo.get("eventstartdate");
                    String eventEndDate = festivalInfo.get("eventenddate");

                    // 'start_date'와 'end_date' 텍스트뷰에 값을 설정합니다.
                    startDateTextView.setText(eventStartDate);
                    endDataTextView.setText(eventEndDate);
                }
            }

            @Override
            public void onError(String error) {
                // 오류 처리 코드를 추가할 수 있습니다.
            }
        });


        Intent intent = getIntent();
        if(intent != null) {
            String content = intent.getStringExtra("content");
            if(content != null){
                TextView entireContent = findViewById(R.id.festival_title);
                entireContent.setText(content);
            }
        }

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   onBackPressed(); }
        });
    }


}
