package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EntireViewActivity extends AppCompatActivity {
    public ImageButton Back_Btn;
    private ApiReader apiReader;
    private Executor executor;
    private UserDataBase db;
    private UserDao userDao;
    private UserEntity loadedUser;
    private String userId;


    private List<HashMap<String, String>> festivalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entire_view);

        Back_Btn=findViewById(R.id.before_btn);

        executor = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

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
                            ImageButton favoriteaddButton = findViewById(R.id.favorite_addButton);

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


                            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Button Listener", "addBtn");
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            String contentId = overviewText.getText().toString();
                                            loadedUser = userDao.getUserInfoById(userId);
                                            if (loadedUser != null) {
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

            @Override
            public void onError(String error) {

            }
        });


        apiReader.detailIntro(apiKey, contentId, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                ParsingApiData.parseXmlDataFromdetailIntro(response);

                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                Log.d(TAG, "Festival List Size: " + parsedFestivalList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);
                        for (HashMap<String, String> festivalInfo : festivalList) {
                            String eventStartDate = festivalInfo.get("eventstartdate");
                            String eventEndDate = festivalInfo.get("eventenddate");

                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

                                Date startDate = inputFormat.parse(eventStartDate);
                                Date endDate = inputFormat.parse(eventEndDate);

                                String formattedStartDate = outputFormat.format(startDate);
                                String formattedEndDate = outputFormat.format(endDate);

                                TextView startDateTextView = findViewById(R.id.start_date);
                                TextView endDateTextView = findViewById(R.id.end_date);

                                startDateTextView.setText(formattedStartDate);
                                endDateTextView.setText(formattedEndDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

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
