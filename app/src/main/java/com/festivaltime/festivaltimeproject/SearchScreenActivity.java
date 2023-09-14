package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToSearchActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchScreenActivity extends AppCompatActivity {

    private static final String TAG = "SearchScreenActivity";
    private Executor executor;
    private SearchView searchView;
    private String query;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private List<HashMap<String, String>> parsedFestivalList = new ArrayList<>();
    private ApiReader apiReader;
    private String type;
    private String cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10, cat11, cat12 = "", cat13, cat14;


    // 카테고리 숫자 순서대로 나오도록 하는 변수들
    private Semaphore firstSemaphore = new Semaphore(0);
    private Semaphore secondSemaphore = new Semaphore(0);
    private Semaphore thirdSemaphore = new Semaphore(0);
    private Semaphore fourthSemaphore = new Semaphore(0);
    private Semaphore fifthSemaphore = new Semaphore(0);
    private Semaphore sixthSemaphore = new Semaphore(0);
    private Semaphore seventhSemaphore = new Semaphore(0);
    private Semaphore eightSemaphore = new Semaphore(0);
    private Semaphore ninthSemaphore = new Semaphore(0);
    private Semaphore tenthSemaphore = new Semaphore(0);
    private Semaphore eleventhSemaphore = new Semaphore(0);
    private Semaphore twelveSemaphore = new Semaphore(0);
    private Semaphore thirteenSemaphore = new Semaphore(0);

    MainActivity main = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        HashGetter.getHashKey(getApplicationContext());

        searchView = findViewById(R.id.main_search_bar);
        searchView.setOnTouchListener((v, event) -> {
            searchView.setIconified(false);
            searchView.performClick();
            return true;
        });
        ImageButton searchoptionbutton = findViewById(R.id.detailButton);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {       //검색 시 검색 내용 SearchActivity 로 전달
                query = s;
                performSearch(query);
                navigateToSearchActivity(SearchScreenActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        executor = Executors.newSingleThreadExecutor();

        //키워드 서치용 query
        String query = getIntent().getStringExtra("query");
        //날짜 서치용 boolean
        //Boolean searchFestival = false;

        String seoul = "서울";

        type = getIntent().getStringExtra("type");

        if (type == null) {
            type = ""; // type이 null일 경우 빈 문자열로 초기화
        }

        String apiKey = getResources().getString(R.string.api_key);

        Bundle bundle = DataHolder.getInstance().getBundle();


        cat2 = "A0207";
        cat3 = "A02080500";
        cat4 = "A02080200";
        cat5 = "A02080100";
        cat6 = "A02080300";
        cat7 = "A02080400";
        cat8 = "A02080600";
        cat9 = "A02080800";
        cat10 = "A02080900";
        cat11 = "A02081000";
        cat12 = "A02081100";
        cat13 = "A02081200";
        cat14 = "A02081300";


        //날짜 서치인지 형태 확인
        String regex = "\\d{4}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);


        apiReader = new ApiReader();

        firstSemaphore.release();


        if (bundle != null) {
            String startDate = bundle.getString("startdate");
            String endDate = bundle.getString("enddate");
            String selectedLocation = bundle.getString("location");

            // {0/0/0] 형태로 ,,,,했음
            String[] queryArray = new String[3];
            queryArray[0] = selectedLocation;
            queryArray[1] = startDate;
            queryArray[2] = endDate;

            apiReader.searchKeyword2(apiKey, queryArray[0], query, cat2, new ApiReader.ApiResponseListener() {

                @Override
                public void onSuccess(String response) {

                    festivalList.clear(); // 기존 데이터를 모두 제거

                    ParsingApiData.parseXmlDataFromSearchKeyword3(response, cat2, null);
                    List<LinkedHashMap<String, String>> keywordResults = ParsingApiData.getFestivalList();

                    CountDownLatch latch = new CountDownLatch(keywordResults.size());


                    for (LinkedHashMap<String, String> result : keywordResults) {

                        apiReader.FestivalInfo(apiKey, result.get("contentid"), new ApiReader.ApiResponseListener() {
                            @Override
                            public void onSuccess(String response) {
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {


                                        Log.d("response", response);
                                        ParsingApiData.parseXmlDataFromDetailInfo(response); // 응답을 파싱하여 데이터를 저장
                                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                        for (LinkedHashMap<String, String> parsedItem : parsedFestivalList) {
                                            if (result.get("title") != null &&
                                                    result.get("title").contains(query)) {

                                                // result에서 필요한 값들을 가져와서 새로운 LinkedHashMap에 넣습니다.
                                                LinkedHashMap<String, String> newItem = new LinkedHashMap<>();

                                                newItem.put("title", result.get("title"));
                                                newItem.put("firstimage2", result.get("firstimage2"));
                                                newItem.put("cat2", result.get("cat2"));

                                                // "contentid"는 이미 parsedItem에 있으므로 별도로 가져옵니다.
                                                newItem.putAll(parsedItem);

                                                // 조건에 맞는 아이템들만 리스트에 넣음
                                                festivalList.add(newItem);

                                            }
                                            latch.countDown();
                                        }

                                        if (latch.getCount() == 0) {
                                            // 모든 FestivalInfo 응답이 끝났을 때
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (!festivalList.isEmpty()) {
                                                        LinearLayout searchContainer = findViewById(R.id.search_container);
                                                        //searchContainer.removeAllViews();

                                                        View searchContainerView =
                                                                getLayoutInflater().inflate(R.layout.festivalsearch_container,
                                                                        null);
                                                        GridLayout festivalImageNText =
                                                                searchContainerView.findViewById(R.id.festivalSearch_container3);
                                                        //festivalImageNText.removeAllViews();

                                                        String textToShow = getTextToShow(cat2);
                                                        TextView titleTextView =
                                                                searchContainerView.findViewById(R.id.title_name);
                                                        titleTextView.setText(textToShow);

                                                        int maxItems =
                                                                Math.min(festivalList.size(), 6);

                                                        for (int i=0; i<maxItems; i++) {
                                                            HashMap<String,String> festivalInfo = festivalList.get(i);

                                                            View festivalItemView =
                                                                    getLayoutInflater().inflate(
                                                                            R.layout.festival_search_imagentext,
                                                                            null);
                                                            TextView searchTextView =
                                                                    festivalItemView.findViewById(
                                                                            R.id.search_text);
                                                            ImageButton searchImageButton =
                                                                    festivalItemView.findViewById(
                                                                            R.id.search_image);

                                                            searchTextView.setText(festivalInfo.get(
                                                                    "title"));
                                                            searchTextView.setMaxEms(8);

                                                            /**Log.d(TAG,
                                                                    "Rep Image URL: " +
                                                                            repImage);**/

                                                            Glide.with(SearchScreenActivity.this)
                                                                    .load(festivalInfo.get(
                                                                            "firstimage2"))
                                                                    .transform(new CenterCrop(),
                                                                            new RoundedCorners(30))
                                                                    .placeholder(R.drawable.ic_image)
                                                                    .into(searchImageButton);


                                                            festivalItemView.setOnClickListener(
                                                                    new View.OnClickListener(){
                                                                        @Override
                                                                        public void onClick(View v){
                                                                            navigateToDetailFestivalActivity(SearchScreenActivity.this, festivalInfo.get("contentid"), cat2);
                                                                        }
                                                                    });




                                                        }
                                                        // 축제 건수 띄우는 텍스트
                                                        String progessToShow = "(" + festivalList.size() + "건)";
                                                        TextView progressTextView = searchContainerView.findViewById(R.id.title_progress);
                                                        progressTextView.setText(progessToShow);

                                                        Button detailSearchButton=
                                                                searchContainerView.findViewById(R.id.detail_search_button);

                                                        detailSearchButton.setOnClickListener(new View.OnClickListener(){

                                                            @Override
                                                            public void onClick(View v){
                                                                navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this,
                                                                        query,
                                                                        cat2);

                                                            }});

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(String error){
                                Log.e(TAG,"API Error: "+error);
                                latch.countDown();


                            }



                        });


                    }


                }


                @Override
                public void onError(String error){
                    Log.e(TAG,"API Error: "+error);

                }

            });



        }



        if (query != null && bundle == null) {
            Matcher matcher = pattern.matcher(query);

            //키워드 서치
            if (!matcher.matches()) {
                Log.d("match", "date match fail");
                apiReader.searchKeyword2(apiKey, query, cat2, new ApiReader.ApiResponseListener() {

                    @Override
                    public void onSuccess(String response) {
                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                        try {
                            firstSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear(); // 기존 데이터를 모두 제거
                                festivalList.addAll(parsedFestivalList);

                                // UI 갱신은 메인 스레드에서 실행
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            // UI 갱신 코드
                                            LinearLayout searchContainer = findViewById(R.id.search_container);
                                            searchContainer.removeAllViews();

                                            View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
                                            GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
                                            festivalImageNText.removeAllViews();


                                            // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
                                            String textToShow = getTextToShow(cat2);
                                            TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
                                            titleTextView.setText(textToShow);

                                            // 축제 건수 띄우는 텍스트
                                            String progessToShow = "(" + festivalList.size() + "건)";
                                            TextView progressTextView = searchContainerView.findViewById(R.id.title_progress);
                                            progressTextView.setText(progessToShow);


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
                                                    //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(searchImageButton);
                                                    Glide
                                                            .with(SearchScreenActivity.this)
                                                            .load(repImage)
                                                            .transform(new CenterCrop(), new RoundedCorners(30))
                                                            .placeholder(R.drawable.ic_image)
                                                            .into(searchImageButton);
                                                }
                                                festivalImageNText.addView(festivalItemView);


                                                festivalItemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String contentId = id;
                                                        // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                                        navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId, cat2);
                                                    }
                                                });


                                            }


                                            searchContainer.addView(searchContainerView);

                                            Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
                                            detailSearchButton.setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, cat2);
                                                }
                                            });
                                        }
                                    }

                                });
                                secondSemaphore.release();
                            }

                        });


                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "API Error: " + error);
                    }
                });

                apiReader.searchKeyword(apiKey, query, cat3, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            thirdSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat3); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {

                                            loopUI(query, cat3, 3);
                                        }
                                        fourthSemaphore.release();
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

                apiReader.searchKeyword(apiKey, query, cat4, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            secondSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat4); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat4, 3);
                                        }
                                        thirdSemaphore.release();
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

                apiReader.searchKeyword(apiKey, query, cat5, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            fourthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat5); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat5, 3);
                                        }
                                        fifthSemaphore.release();
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

                apiReader.searchKeyword(apiKey, query, cat6, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            fifthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat6); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat6, 3);
                                        }
                                        sixthSemaphore.release();

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

                apiReader.searchKeyword(apiKey, query, cat7, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            sixthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat7); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat7, 3);
                                        }
                                        seventhSemaphore.release();

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

                apiReader.searchKeyword(apiKey, query, cat8, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            seventhSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat8); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat8, 3);
                                        }
                                        eightSemaphore.release();

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

                apiReader.searchKeyword(apiKey, query, cat9, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            eightSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat9); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat9, 3);
                                        }
                                        ninthSemaphore.release();

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

                apiReader.searchKeyword(apiKey, query, cat10, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            ninthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat10); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat10, 3);
                                        }
                                        tenthSemaphore.release();

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

                apiReader.searchKeyword(apiKey, query, cat11, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            tenthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat11); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat11, 3);
                                        }
                                        eleventhSemaphore.release();

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

                apiReader.searchKeyword(apiKey, query, cat12, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            eleventhSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat12); // 응답을 파싱하여 데이터를 저장
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat12, 3);
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
            } else {
                Log.d("match", "date match success");

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        ParsingApiData.parseXmlDataFromFestivalA(response);
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
                                        if (festivalList.size() > 0) {
                                            // UI 갱신 코드
                                            LinearLayout searchContainer = findViewById(R.id.search_container);
                                            searchContainer.removeAllViews();

                                            View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
                                            GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
                                            festivalImageNText.removeAllViews();

                                            String textToShow = getTextToShow(cat2);
                                            TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
                                            titleTextView.setText(textToShow);

                                            String progessToShow = "(" + festivalList.size() + "건)";
                                            TextView progressTextView = searchContainerView.findViewById(R.id.title_progress);
                                            progressTextView.setText(progessToShow);

                                            int maxItems = Math.min(festivalList.size(), 6);

                                            for (int i = 0; i < maxItems; i++) {
                                                HashMap<String, String> festivalInfo = festivalList.get(i);
                                                View festivalItemView = getLayoutInflater().inflate(R.layout.festival_search_imagentext, null);
                                                TextView searchTextView = festivalItemView.findViewById(R.id.search_text);
                                                ImageButton searchImageButton = festivalItemView.findViewById(R.id.search_image);

                                                String title = festivalInfo.get("title");
                                                String id = festivalInfo.get("contentid");
                                                String repImage = festivalInfo.get("img");
                                                Log.d("cat2: ", festivalInfo.get("cat2"));
                                                Log.d("startdate: ", festivalInfo.get("eventstartdate"));
                                                Log.d("enddate: ", festivalInfo.get("eventenddate"));

                                                searchTextView.setText(title);
                                                searchTextView.setMaxEms(8);

                                                Log.d(TAG, "Rep Image URL: " + repImage);
                                                if (repImage == null || repImage.isEmpty()) {
                                                    searchImageButton.setImageResource(R.drawable.ic_image);
                                                } else {
                                                    Glide
                                                            .with(SearchScreenActivity.this)
                                                            .load(repImage)
                                                            .transform(new CenterCrop(), new RoundedCorners(30))
                                                            .placeholder(R.drawable.ic_image)
                                                            .into(searchImageButton);
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

                                            searchContainer.addView(searchContainerView);
                                            Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
                                            detailSearchButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, cat2);
                                                }
                                            });
                                        }
                                    }

                                });
                                secondSemaphore.release();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "API Error: " + error);
                    }
                });

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            secondSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat3);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {

                                            loopUI(query, cat3, 3);
                                        }
                                        thirdSemaphore.release();
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
                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            thirdSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat4);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat4, 3);
                                        }
                                        fourthSemaphore.release();
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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            fourthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat5);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat5, 3);
                                        }
                                        fifthSemaphore.release();
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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            fifthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat6);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat6, 3);
                                        }
                                        sixthSemaphore.release();
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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            sixthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat7);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat7, 3);
                                        }
                                        seventhSemaphore.release();

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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            seventhSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat8);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat8, 3);
                                        }
                                        eightSemaphore.release();

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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            eightSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat9);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat9, 3);
                                        }
                                        ninthSemaphore.release();

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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            ninthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat10);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat10, 3);
                                        }
                                        tenthSemaphore.release();

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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            tenthSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat11);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat11, 3);
                                        }
                                        eleventhSemaphore.release();

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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {


                        try {
                            eleventhSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat12);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat12, 3);
                                        }
                                        thirteenSemaphore.release();
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

                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            twelveSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat13);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat13, 3);
                                        }
                                        thirteenSemaphore.release();

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
                apiReader.Festivallit(apiKey, query, new ApiReader.ApiResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            thirteenSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("response", response);
                        ParsingApiData.parseXmlDataFromFestival(response, cat14);
                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                festivalList.clear();
                                festivalList.addAll(parsedFestivalList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (festivalList.size() > 0) {
                                            loopUI(query, cat14, 3);
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
            }

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

    }

    private void fetchAndDisplayData(String apiKey, String query, String cat, Semaphore
            semaphore) {
        apiReader.searchKeyword(apiKey, query, cat, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat);
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loopUI(query, cat, 3);
                                semaphore.release();
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


    private void loopUI(String query, String cat, int count) {
        LinearLayout searchContainer = findViewById(R.id.search_container);

        View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
        GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
        festivalImageNText.removeAllViews();

        // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
        String textToShow = getTextToShow(cat);
        TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
        titleTextView.setText(textToShow);

        String progessToShow = "(" + festivalList.size() + "건)";
        TextView progressTextView = searchContainerView.findViewById(R.id.title_progress);
        progressTextView.setText(progessToShow);


        int loopItems = Math.min(festivalList.size(), count);

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
                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(searchImageButton);
                Glide
                        .with(SearchScreenActivity.this)
                        .load(repImage)
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .placeholder(R.drawable.ic_image)
                        .into(searchImageButton);
            }
            festivalImageNText.addView(festivalItemView);


            festivalItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contentId = id;
                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                    navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId, cat);
                }
            });

        }

        searchContainer.addView(searchContainerView);

        Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
        detailSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, cat);
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
            case "A02081200":
                return "스포츠";
            case "A02081300":
                return "기타";
            default:
                if (!type.isEmpty() && type.startsWith("A0207")) {
                    return "축제";
                } else {
                    return "기타";
                }
        }
    }

    private void performSearch(String query) {
        System.out.println("검색어: " + query);
    }
}