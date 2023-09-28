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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


class ApiResponse {
    final int index;
    final String response;

    ApiResponse(int index, String response) {
        this.index = index;
        this.response = response;
    }
}

public class SearchScreenActivity extends AppCompatActivity {

    private static final String TAG = "SearchScreenActivity";
    private Executor executor;
    private SearchView searchView;
    private String query;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private List<HashMap<String, String>> newfestivalList = new ArrayList<>();
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
        List<String> categories = Arrays.asList(cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10, cat11, cat12, cat13, cat14);

        int apiCallCount = 13; // API 호출 횟수 설정
        //AtomicInteger emptyCategoryCount = new AtomicInteger(0); // 데이터가 없는 카테고리의 수
        CountDownLatch latch = new CountDownLatch(apiCallCount);


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

            Log.d("location response", queryArray[0]);
            Log.d("date response", queryArray[1]);


            Observable<String> keywordSearchObservable = apiReader.searchKeyword2(apiKey, queryArray[0], query, cat2);
            keywordSearchObservable
                    .subscribeOn(Schedulers.io())
                    .flatMap(response -> {
                        ParsingApiData.parseXmlDataFromSearchKeyword3(response, cat2, null);
                        List<LinkedHashMap<String, String>> keywordResults = ParsingApiData.getFestivalList();
                        //Log.d(TAG, "Parsed keyword results: " + keywordResults); // 로그 추가

                        return Observable.fromIterable(keywordResults)
                                //.take(6)  // 최대 6개의 결과만 처리합니다.
                                .concatMapEager(result -> {
                                    return apiReader.FestivalInfo(apiKey, result.get("contentid"))
                                            .subscribeOn(Schedulers.io())
                                            .map(detailResponse -> {
                                                Log.d("search&locate response", detailResponse);
                                                ParsingApiData.parseXmlDataFromDetailInfo(detailResponse); // 응답을 파싱하여 데이터를 저장
                                                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                                LinkedHashMap<String, String> parsedItem;
                                                try {
                                                    parsedItem = parsedFestivalList.get(0);

                                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                                    LocalDate startDate1 = LocalDate.parse(queryArray[1], formatter);
                                                    LocalDate eventStartDate = LocalDate.parse(parsedItem.get("eventstartdate"), formatter);
                                                    LocalDate eventEndDate = LocalDate.parse(parsedItem.get("eventenddate"), formatter);

                                                    Log.d("item startdate", parsedItem.get("eventstartdate"));
                                                    Log.d("item enddate", parsedItem.get("eventenddate"));

                                                    if (!(result.get("title").contains(query) &&
                                                            startDate1.isBefore(eventStartDate) &&
                                                            !startDate1.isAfter(eventEndDate))) {
                                                        result.put("_remove_", "true"); // '_remove_' 키에 'true' 값을 설정하여 제거 대상임을 표시합니다.
                                                        Log.d("item del", "delete");
                                                    }

                                                } catch (IndexOutOfBoundsException e) {
                                                }

                                                return result;
                                            });
                                })
                                .filter(result -> !result.containsKey("_remove_")) // '_remove_' 키가 있는 아이템을 제거합니다.
                                .toList()
                                .toObservable();  // Single을 Observable로 변환합니다.
                    })

                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        loopUI(query, cat2, 6, result);
                    }, error -> {
                        Log.e(TAG, "API Error: ", error);
                    });

            /*
            Observable<List<LinkedHashMap<String, String>>> keywordSearchObservable2 = Observable.fromIterable(categories)
                    .flatMap(catValue -> apiReader.searchKeyword3(apiKey, queryArray[0], query, catValue)
                            .subscribeOn(Schedulers.io())
                            .map(response -> {
                                ParsingApiData.parseXmlDataFromSearchKeyword3(response, null, catValue);
                                return ParsingApiData.getFestivalList();
                            })
                    )
                    .concatMapEager(keywordResults -> Observable.fromIterable(keywordResults)
                            .concatMapEager(result -> apiReader.FestivalInfo(apiKey, result.get("contentid"))
                                    .subscribeOn(Schedulers.io())
                                    .map(detailResponse -> {
                                        Log.d("search&locate response", detailResponse);
                                        ParsingApiData.parseXmlDataFromDetailInfo(detailResponse); // 응답을 파싱하여 데이터를 저장
                                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                        LinkedHashMap<String, String> parsedItem;
                                        try {
                                            parsedItem = parsedFestivalList.get(0);

                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                            LocalDate startDate1 = LocalDate.parse(queryArray[1], formatter);
                                            LocalDate eventStartDate = LocalDate.parse(parsedItem.get("eventstartdate"), formatter);
                                            LocalDate eventEndDate = LocalDate.parse(parsedItem.get("eventenddate"), formatter);

                                            Log.d("item startdate", parsedItem.get("eventstartdate"));
                                            Log.d("item enddate", parsedItem.get("eventenddate"));

                                            if (!(result.get("title").contains(query) &&
                                                    startDate1.isBefore(eventStartDate) &&
                                                    !startDate1.isAfter(eventEndDate))) {
                                                result.put("_remove_", "true"); // '_remove_' 키에 'true' 값을 설정하여 제거 대상임을 표시합니다.
                                                Log.d("item del", "delete");
                                            }

                                        } catch (IndexOutOfBoundsException e) {

                                        }

                                        return result;
                                    })
                            )
                            .filter(result -> !result.containsKey("_remove_")) // '_remove_' 키가 있는 아이템을 제거
                            .toList().toObservable()
                    )
                    .observeOn(AndroidSchedulers.mainThread());
            Disposable disposable = keywordSearchObservable2.subscribe(result -> {
                for (String category : categories) {
                    loopUI(query, category, 3, result, latch);
                }
            }, error -> {
                Log.e(TAG, "API Error: ", error);
            });
            */

            Observable<List<LinkedHashMap<String, String>>> keywordSearchObservable2 =
                    Observable.fromIterable(categories)
                            .concatMapEager(catValue ->
                                    apiReader.searchKeyword3(apiKey, queryArray[0], query, catValue)
                                            .subscribeOn(Schedulers.io())
                                            .map(response -> {
                                                ParsingApiData.parseXmlDataFromSearchKeyword3(response, null, catValue);
                                                return ParsingApiData.getFestivalList();
                                            })
                                            .concatMapEager(keywordResults ->
                                                    Observable.fromIterable(keywordResults)
                                                            .concatMapEager(result ->
                                                                    apiReader.FestivalInfo(apiKey, result.get("contentid"))
                                                                            .subscribeOn(Schedulers.io())
                                                                            .map(detailResponse -> {
                                                                                Log.d("search&locate response", detailResponse);
                                                                                ParsingApiData.parseXmlDataFromDetailInfo(detailResponse); // 응답을 파싱하여 데이터를 저장
                                                                                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                                                                LinkedHashMap<String, String> parsedItem;
                                                                                try {
                                                                                    parsedItem = parsedFestivalList.get(0);

                                                                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                                                                    LocalDate startDate1 = LocalDate.parse(queryArray[1], formatter);
                                                                                    LocalDate eventStartDate = LocalDate.parse(parsedItem.get("eventstartdate"), formatter);
                                                                                    LocalDate eventEndDate = LocalDate.parse(parsedItem.get("eventenddate"), formatter);

                                                                                    Log.d("item startdate", parsedItem.get("eventstartdate"));
                                                                                    Log.d("item enddate", parsedItem.get("eventenddate"));

                                                                                    if (!(result.get("title").contains(query) &&
                                                                                            startDate1.isBefore(eventStartDate) &&
                                                                                            !startDate1.isAfter(eventEndDate))) {
                                                                                        result.put("_remove_", "true"); // '_remove_' 키에 'true' 값을 설정하여 제거 대상임을 표시합니다.
                                                                                        //Log.d("item del", "delete");
                                                                                    }

                                                                                } catch (
                                                                                        IndexOutOfBoundsException e) {

                                                                                }

                                                                                return result;
                                                                            })
                                                            )
                                                            .filter(result -> !result.containsKey("_remove_"))
                                                            .toList().toObservable()
                                            )
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .map(resultListForCategory -> {
                                                loopUI(query, catValue , 3,resultListForCategory);  // Pass the category and its result list to 'loopUI' function
                                                return resultListForCategory;
                                            })
                            )
                            .observeOn(AndroidSchedulers.mainThread());

            Disposable disposable = keywordSearchObservable2.subscribe(resultListForCategory -> {}, error -> {
                Log.e(TAG,"API Error: ", error);
            });



/*

            apiReader.searchKeyword2(apiKey, queryArray[0], query, cat2, new ApiReader.ApiResponseListener() {

                @Override
                public void onSuccess(String response) {

                    ParsingApiData.parseXmlDataFromSearchKeyword3(response, cat2, null);
                    List<LinkedHashMap<String, String>> keywordResults = ParsingApiData.getFestivalList();

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            newfestivalList.clear(); // 기존 데이터를 모두 제거
                            newfestivalList.addAll(keywordResults);

                            int maxItems = Math.min(newfestivalList.size(), 6);

                            List<HashMap<String, String>> itemsToRemove = new ArrayList<>();

                            for (int i = 0; i < maxItems; i++) {
                                HashMap<String, String> result = newfestivalList.get(i);

                                apiReader.FestivalInfo(apiKey, result.get("contentid"), new ApiReader.ApiResponseListener() {
                                    @Override
                                    public void onSuccess(String response) {
                                        Log.d("search&locate response", response);
                                        ParsingApiData.parseXmlDataFromDetailInfo(response); // 응답을 파싱하여 데이터를 저장
                                        List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                                        executor.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    LinkedHashMap<String, String> parsedItem = parsedFestivalList.get(0);
                                                    Log.d("item startdate", parsedItem.get("eventstartdate"));
                                                    Log.d("item enddate", parsedItem.get("eventenddate"));

                                                    if (result.get("title").contains(query) &&
                                                            queryArray[1].compareTo(parsedItem.get("eventstartdate")) >= 0 &&
                                                            queryArray[2].compareTo(parsedItem.get("eventenddate")) <= 0) {
                                                    } else {
                                                        itemsToRemove.add(result);
                                                        Log.d("item del", "delete");
                                                    }
                                                } catch (IndexOutOfBoundsException e) {
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "API Error: " + error);
                                    }
                                });//api끝부분

                                newfestivalList.removeAll(itemsToRemove);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (newfestivalList.size() > 0) {
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
                                        String progessToShow = "(" + newfestivalList.size() + "건)";
                                        TextView progressTextView = searchContainerView.findViewById(R.id.title_progress);
                                        progressTextView.setText(progessToShow);


                                        int maxItems = Math.min(newfestivalList.size(), 6);


                                        for (int i = 0; i < maxItems; i++) {
                                            HashMap<String, String> festivalInfo = newfestivalList.get(i);
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
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);

                }

            });
*/


        } else if (query != null && bundle == null) {
            Matcher matcher = pattern.matcher(query);

            //키워드 서치
            if (!matcher.matches()) {
                Log.d("match", "date match fail");

                // 백그라운드 실행
                List<LinkedHashMap<String, String>> totalResult = new ArrayList<>();
                Observable.concat(
                                apiReader.searchKeyword2(apiKey, query, cat2)
                                        .toObservable()
                                        .subscribeOn(Schedulers.io())
                                        .map(response -> new ApiResponse(0, response)),
                                Observable.range(0, categories.size())
                                        .concatMap(index ->
                                                apiReader.searchKeyword(apiKey, query, categories.get(index))
                                                        .toObservable()
                                                        .subscribeOn(Schedulers.io())
                                                        .map(response -> new ApiResponse(index + 1, response)))
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ApiResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(ApiResponse apiResponse) {
                                ParsingApiData.parseXmlDataFromSearchKeyword(apiResponse.response);
                                List<LinkedHashMap<String, String>> result = ParsingApiData.getFestivalList();

                                if (result.isEmpty()) {
                                    latch.countDown();

                                }

                                totalResult.addAll(result);

                                if (apiResponse.index == 0) {
                                    loopUI(query, cat2, 6, result);
                                } else {
                                    loopUI(query, categories.get(apiResponse.index - 1), 3, result);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "API Error: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() { // 모든 api호출 완료된 후
                                if (totalResult.isEmpty()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView textView = findViewById(R.id.no_festival_msg);
                                            textView.setText("데이터가 없습니다.");
                                        }
                                    });
                                }

                            }
                        });


            } else {
                Log.d("match", "date match success");
                // 백그라운드 실행
                List<LinkedHashMap<String, String>> totalResult = new ArrayList<>();
                Observable.range(0, categories.size() + 1)
                        .concatMap(index ->
                                apiReader.Festivallit(apiKey, query)
                                        .subscribeOn(Schedulers.io())
                                        .map(response -> new ApiResponse(index, response))
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ApiResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(ApiResponse apiResponse) {

                                int index = apiResponse.index;

                                if (index == 0) {
                                    ParsingApiData.parseXmlDataFromFestivalA(apiResponse.response);
                                } else {
                                    ParsingApiData.parseXmlDataFromFestival(apiResponse.response, categories.get(index - 1));
                                }
                                List<LinkedHashMap<String, String>> result = ParsingApiData.getFestivalList();

                                if (result.isEmpty()) {
                                    latch.countDown();

                                }

                                totalResult.addAll(result);

                                if (apiResponse.index == 0) {
                                    loopUI(query, cat2, 6, result);
                                } else {
                                    loopUI(query, categories.get(apiResponse.index - 1), 3, result);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "API Error: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() { // 모든 api호출 완료된 후
                                if (totalResult.isEmpty()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView textView = findViewById(R.id.no_festival_msg);
                                            textView.setText("데이터가 없습니다.");
                                        }
                                    });
                                }

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

    private void loopUI(String query, String cat, int count, List<LinkedHashMap<String, String>>
            festivalList) {


        // 이미지 정렬 코드
        Collections.sort(festivalList, new Comparator<LinkedHashMap<String, String>>() {
            @Override
            public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {
                boolean o1HasImage = o1.get("img") != null && !o1.get("img").isEmpty();
                boolean o2HasImage = o2.get("img") != null && !o2.get("img").isEmpty();

                if (o1HasImage && !o2HasImage) {
                    return -1; // 이미지가 있는 항목을 앞으로
                } else if (!o1HasImage && o2HasImage) {
                    return 1; // 이미지가 없는 항목을 뒤로
                } else {
                    return 0; // 그 외의 경우 순서 유지
                }
            }
        });

        LinearLayout searchContainer = findViewById(R.id.search_container);

        View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
        GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
        festivalImageNText.removeAllViews();


        /*
        if (latch.getCount() == 0) {
            TextView textView = findViewById(R.id.no_festival_msg);
            textView.setText("데이터가 없습니다.");

            return;
        }*/

        if (festivalList.isEmpty()) {
            return;
        }


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